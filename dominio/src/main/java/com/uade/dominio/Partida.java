package com.uade.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Partida {

    private final Jugador jugadorA;
    private final Jugador jugadorB;
    private final Personaje personajeSecretoA;
    private final Personaje personajeSecretoB;

    private EspacioDeBusqueda espacioA;
    private EspacioDeBusqueda espacioB;

    private final List<Filtro> historialA = new ArrayList<>();
    private final List<Filtro> historialB = new ArrayList<>();

    private boolean terminada = false;
    private Jugador ganador = null;
    private Jugador turnoActual;

    public Partida(Jugador jugadorA, Jugador jugadorB, RegistroDePersonajes registro) {
        this.jugadorA = jugadorA;
        this.jugadorB = jugadorB;

        List<Personaje> yaElegidos = new ArrayList<>();
        this.personajeSecretoA = jugadorA.elegirPersonaje(registro, yaElegidos);
        yaElegidos.add(personajeSecretoA);
        this.personajeSecretoB = jugadorB.elegirPersonaje(registro, yaElegidos);

        this.espacioA = new EspacioDeBusqueda(registro.listar());
        this.espacioB = new EspacioDeBusqueda(registro.listar());
        this.turnoActual = jugadorA;
    }

    public ResultadoTurno jugarTurno() {
        if (terminada) throw new IllegalStateException("La partida ya termino");

        boolean esA = turnoActual == jugadorA;
        EspacioDeBusqueda espacioPropio = esA ? espacioA : espacioB;
        List<Filtro> historialPropio  = esA ? historialA : historialB;
        List<Filtro> historialRival   = esA ? historialB : historialA;
        Personaje secretoRival        = esA ? personajeSecretoB : personajeSecretoA;

        Accion accion = turnoActual.decidirTurno(
                new EstadoVisible(espacioPropio, historialPropio, historialRival));

        if (accion instanceof Accion.Adivinanza a) {
            if (a.personaje().getId() == secretoRival.getId()) {
                terminada = true;
                ganador   = turnoActual;
            }
        } else if (accion instanceof Accion.AplicarFiltro f) {
            /*
             * DIVIDE & CONQUER — paso recursivo (SDD 4.1 / 4.2)
             * El jugador eligio un filtro (decision Greedy, implementada en cada
             * maquina). Partida lo aplica sobre el espacio propio y recursa
             * implicitamente: el siguiente decidirTurno recibe el subconjunto
             * reducido. Esquema: D&C(espacio) → elegirFiltro [Greedy] →
             * aplicarFiltro [D&C] → D&C(subconjunto).
             * La recursion termina cuando esUnico()==true y el jugador lanza
             * Accion.Adivinanza en lugar de Accion.AplicarFiltro.
             */
            EspacioDeBusqueda reducido = espacioPropio.aplicarFiltro(f.filtro());
            historialPropio.add(f.filtro());
            if (esA) espacioA = reducido; else espacioB = reducido;
        }

        Jugador jugadorDelTurno = turnoActual;
        turnoActual = esA ? jugadorB : jugadorA;
        return new ResultadoTurno(jugadorDelTurno, accion, terminada, ganador);
    }

    public boolean estaTerminada() {
        return terminada;
    }

    public Jugador ganador() {
        if (!terminada) throw new IllegalStateException("La partida no termino todavia");
        return ganador;
    }

    public List<Filtro> getHistorialFiltros(Jugador jugador) {
        if (jugador == jugadorA) return Collections.unmodifiableList(historialA);
        if (jugador == jugadorB) return Collections.unmodifiableList(historialB);
        throw new IllegalArgumentException("El jugador no pertenece a esta partida");
    }

    private record EstadoVisible(
            EspacioDeBusqueda espacioDeBusqueda,
            List<Filtro> historialFiltros,
            List<Filtro> historialFiltrosRival
    ) implements EstadoDePartidaVisible {
        @Override public EspacioDeBusqueda getEspacioDeBusqueda()    { return espacioDeBusqueda; }
        @Override public List<Filtro>       getHistorialFiltros()     { return historialFiltros; }
        @Override public List<Filtro>       getHistorialFiltrosRival(){ return historialFiltrosRival; }
    }
}
