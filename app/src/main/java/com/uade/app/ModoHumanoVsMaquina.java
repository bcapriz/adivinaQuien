package com.uade.app;

import com.uade.dominio.*;
import com.uade.jugadores.MaquinaBasica;

/**
 * Orquesta una partida Humano vs Maquina por consola: corre el bucle de turnos,
 * pide la {@link Accion} a cada jugador a traves del port {@link Jugador} y
 * traduce cada {@link ResultadoTurno} a mensajes de consola. No conoce la
 * heuristica de la maquina ni la logica de particion — eso vive en el dominio.
 */
public class ModoHumanoVsMaquina {

    private final Consola consola;
    private final Jugador maquina;

    public ModoHumanoVsMaquina(Consola consola) {
        this(consola, new MaquinaBasica());
    }

    ModoHumanoVsMaquina(Consola consola, Jugador maquina) {
        this.consola = consola;
        this.maquina = maquina;
    }

    public void jugar(RegistroDePersonajes registro) {
        consola.mostrar("=== Adivina Quien - Humano vs Maquina ===");

        Jugador humano = new ConsolaJugadorHumano(consola);
        Partida partida = new Partida(humano, maquina, registro);

        consola.mostrar("\nLa maquina ya eligio su personaje. Empezas vos.");

        while (!partida.estaTerminada()) {
            narrar(partida.jugarTurno(), humano);
        }

        boolean ganoHumano = partida.ganador() == humano;
        consola.mostrar("\n=== " + (ganoHumano ? "Ganaste!" : "Gano la maquina.") + " ===");
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
        }
    }
}
