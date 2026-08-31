package com.uade.dominio;

import java.util.List;

// Implementado en Sprint 2
public class Partida {

    private final Jugador jugadorA;
    private final Jugador jugadorB;
    private final Personaje personajeSecretoA;
    private final Personaje personajeSecretoB;

    public Partida(Jugador jugadorA, Jugador jugadorB, RegistroDePersonajes registro) {
        this.jugadorA = jugadorA;
        this.jugadorB = jugadorB;
        List<Personaje> yaElegidos = new java.util.ArrayList<>();
        this.personajeSecretoA = jugadorA.elegirPersonaje(registro, yaElegidos);
        yaElegidos.add(personajeSecretoA);
        this.personajeSecretoB = jugadorB.elegirPersonaje(registro, yaElegidos);
    }

    public ResultadoTurno jugarTurno() {
        throw new UnsupportedOperationException("Sprint 2");
    }

    public boolean estaTerminada() {
        throw new UnsupportedOperationException("Sprint 2");
    }

    public Jugador ganador() {
        throw new UnsupportedOperationException("Sprint 2");
    }

    public List<Filtro> getHistorialFiltros(Jugador jugador) {
        throw new UnsupportedOperationException("Sprint 2");
    }
}
