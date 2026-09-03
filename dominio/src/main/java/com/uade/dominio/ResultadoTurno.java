package com.uade.dominio;

public class ResultadoTurno {

    private final Jugador jugador;
    private final Accion accion;
    private final Boolean respuestaFiltro; // si/no cuando la accion fue AplicarFiltro; null si fue Adivinanza
    private final int candidatosRestantes;
    private final boolean partidaTerminada;
    private final Jugador ganador;

    public ResultadoTurno(Jugador jugador, Accion accion, Boolean respuestaFiltro,
                          int candidatosRestantes, boolean partidaTerminada, Jugador ganador) {
        this.jugador = jugador;
        this.accion = accion;
        this.respuestaFiltro = respuestaFiltro;
        this.candidatosRestantes = candidatosRestantes;
        this.partidaTerminada = partidaTerminada;
        this.ganador = ganador;
    }

    public Jugador getJugador() { return jugador; }
    public Accion getAccion() { return accion; }

    /**
     * Respuesta si/no a la pregunta del filtro, evaluada contra el personaje
     * secreto del rival. Presente solo si la accion del turno fue AplicarFiltro;
     * null si fue una Adivinanza.
     */
    public Boolean getRespuestaFiltro() { return respuestaFiltro; }

    /** Cantidad de candidatos que le quedan al jugador del turno tras jugar esta accion. */
    public int getCandidatosRestantes() { return candidatosRestantes; }

    public boolean isPartidaTerminada() { return partidaTerminada; }
    public Jugador getGanador() { return ganador; }

    /** true si la accion fue una adivinanza correcta (equivale a que este turno gano la partida). */
    public boolean fueAdivinanzaAcertada() {
        return accion instanceof Accion.Adivinanza && partidaTerminada && ganador == jugador;
    }
}
