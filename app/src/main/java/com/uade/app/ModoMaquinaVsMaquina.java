package com.uade.app;

import com.uade.dominio.*;
import com.uade.jugadores.MaquinaAsertiva;
import com.uade.jugadores.MaquinaBasica;

import java.util.List;

/**
 * Modo espectador: corre una partida entre dos máquinas y relata por consola,
 * turno a turno, el filtro elegido, la respuesta y cuántos candidatos le quedan
 * a cada una. Se suscribe a la partida como observador; el dominio no imprime.
 */
public class ModoMaquinaVsMaquina implements ObservadorDePartida {

    private static final int TOPE_DE_TURNOS = 500;
    private static final String NOMBRE_M1 = "Maquina Basica";
    private static final String NOMBRE_M2 = "Maquina Asertiva";

    private final Consola consola;
    private final MarcadorRepository marcador;
    private final Jugador maquina1;
    private final Jugador maquina2;

    public ModoMaquinaVsMaquina(Consola consola, MarcadorRepository marcador) {
        // Máquina 2 arranca con ventaja informativa: lee los filtros de Máquina 1.
        this(consola, marcador, new MaquinaBasica(), new MaquinaAsertiva(true));
    }

    ModoMaquinaVsMaquina(Consola consola, MarcadorRepository marcador, Jugador maquina1, Jugador maquina2) {
        this.consola = consola;
        this.marcador = marcador;
        this.maquina1 = maquina1;
        this.maquina2 = maquina2;
    }

    public void jugar(RegistroDePersonajes registro) {
        consola.mostrar("=== Maquina vs Maquina (modo espectador) ===");
        consola.mostrar("M1 = basica (orden fijo)   |   M2 = asertiva (mejor filtro, informada)\n");

        Partida partida = new Partida(maquina1, maquina2, registro, List.of(this));

        int turnos = 0;
        while (!partida.estaTerminada() && turnos < TOPE_DE_TURNOS) {
            partida.jugarTurno();
            turnos++;
        }
        if (!partida.estaTerminada()) {
            consola.mostrar("\nSin ganador tras " + TOPE_DE_TURNOS + " turnos (personajes indistinguibles).");
            return;
        }

        marcador.registrarVictoria(partida.ganador() == maquina1 ? NOMBRE_M1 : NOMBRE_M2);
        VistaMarcador.imprimir(consola, marcador);
    }

    @Override
    public void onTurnoJugado(ResultadoTurno resultado) {
        String quien = resultado.getJugador() == maquina1 ? "M1" : "M2";
        Accion accion = resultado.getAccion();

        if (accion instanceof Accion.AplicarFiltro f) {
            String respuesta = Boolean.TRUE.equals(resultado.getRespuestaFiltro()) ? "SI" : "NO";
            int quedan = resultado.getCandidatosRestantes();
            consola.mostrar(quien + "  filtro " + f.filtro() + "  ->  " + respuesta
                    + "   (le queda" + (quedan == 1 ? " 1 candidato" : "n " + quedan + " candidatos") + ")");
        } else if (accion instanceof Accion.Adivinanza a) {
            String desenlace = resultado.fueAdivinanzaAcertada() ? "ACIERTA" : "falla";
            consola.mostrar(quien + "  adivina " + a.personaje().getNombre() + "  ->  " + desenlace);
        }
    }

    @Override
    public void onPartidaTerminada(Jugador ganador) {
        consola.mostrar("\n=== Gana " + (ganador == maquina1 ? "M1 (basica)" : "M2 (asertiva)") + " ===");
    }
}
