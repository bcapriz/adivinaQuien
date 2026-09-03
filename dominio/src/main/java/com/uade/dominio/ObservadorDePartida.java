package com.uade.dominio;

/**
 * Recibe notificaciones del avance de una partida turno a turno, sin que
 * {@link Partida} conozca quién las consume (consola, GUI, log). Partida no
 * imprime ni dibuja: solo notifica.
 */
public interface ObservadorDePartida {

    /** Se invoca al final de cada turno, con el resultado de la acción jugada. */
    void onTurnoJugado(ResultadoTurno resultado);

    /** Se invoca una vez, cuando alguien acierta y la partida termina. */
    void onPartidaTerminada(Jugador ganador);
}
