package com.uade.app;

import com.uade.dominio.*;

/**
 * Orquesta una partida Humano vs Maquina por consola: corre el bucle de turnos,
 * pide la {@link Accion} a cada jugador a traves del port {@link Jugador} y
 * traduce cada {@link ResultadoTurno} a mensajes de consola. No conoce la
 * heuristica de la maquina ni la logica de particion — eso vive en el dominio.
 * Tampoco conoce el tipo concreto de la maquina (HU-9: la elige quien arma
 * este modo, ver {@link Main}), por eso el nombre para el marcador viene
 * inyectado en vez de inferirse con un {@code instanceof}.
 */
public class ModoHumanoVsMaquina {

    private final Consola consola;
    private final MarcadorRepository marcador;
    private final Jugador maquina;
    private final String nombreMaquina;

    public ModoHumanoVsMaquina(Consola consola, MarcadorRepository marcador, Jugador maquina, String nombreMaquina) {
        this.consola = consola;
        this.marcador = marcador;
        this.maquina = maquina;
        this.nombreMaquina = nombreMaquina;
    }

    public void jugar(RegistroDePersonajes registro) {
        consola.mostrar("=== Adivina Quien - Humano vs Maquina ===");
        consola.mostrar("Tu nombre: ");
        String nombre = consola.leerLinea().trim();
        if (nombre.isEmpty()) {
            nombre = "Jugador";
        }

        Jugador humano = new ConsolaJugadorHumano(consola);
        Partida partida = new Partida(humano, maquina, registro);

        consola.mostrar("\nLa maquina ya eligio su personaje. Empezas vos.");

        while (!partida.estaTerminada()) {
            narrar(partida.jugarTurno(), humano);
        }

        boolean ganoHumano = partida.ganador() == humano;
        consola.mostrar("\n=== " + (ganoHumano ? "Ganaste!" : "Gano la maquina.") + " ===");
        marcador.registrarVictoria(ganoHumano ? nombre : nombreMaquina);
        VistaMarcador.imprimir(consola, marcador);
    }

    private void narrar(ResultadoTurno resultado, Jugador humano) {
        String quien = resultado.getJugador() == humano ? "Vos" : "La maquina";
        Accion accion = resultado.getAccion();

        if (accion instanceof Accion.AplicarFiltro f) {
            String respuesta = Boolean.TRUE.equals(resultado.getRespuestaFiltro()) ? "SI" : "NO";
            consola.mostrar(quien + "  ->  filtro " + f.filtro() + "  ->  " + respuesta
                    + "   (quedan " + resultado.getCandidatosRestantes() + ")");
        } else if (accion instanceof Accion.Adivinanza a) {
            String desenlace = resultado.isPartidaTerminada() ? "ACERTO" : "erro";
            consola.mostrar(quien + "  ->  adivina " + a.personaje().getNombre() + "  ->  " + desenlace);
        } else if (accion instanceof Accion.Rendirse) {
            consola.mostrar(quien + "  ->  se rindio");
        }
    }
}
