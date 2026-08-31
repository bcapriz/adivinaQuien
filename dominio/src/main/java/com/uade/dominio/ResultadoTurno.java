package com.uade.dominio;

public class ResultadoTurno {

    private final Jugador jugador;
    private final Accion accion;
    private final boolean partidaTerminada;
    private final Jugador ganador;

    public ResultadoTurno(Jugador jugador, Accion accion, boolean partidaTerminada, Jugador ganador) {
        this.jugador = jugador;
        this.accion = accion;
        this.partidaTerminada = partidaTerminada;
        this.ganador = ganador;
    }

    public Jugador getJugador() { return jugador; }
    public Accion getAccion() { return accion; }
    public boolean isPartidaTerminada() { return partidaTerminada; }
    public Jugador getGanador() { return ganador; }
}
