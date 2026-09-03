package com.uade.jugadores;

import com.uade.dominio.EspacioDeBusqueda;
import com.uade.dominio.EstadoDePartidaVisible;
import com.uade.dominio.Filtro;

import java.util.List;

/** {@link EstadoDePartidaVisible} de test: valores fijos, sin depender de una Partida real. */
class EstadoFake implements EstadoDePartidaVisible {

    private final EspacioDeBusqueda espacio;
    private final List<Filtro> historialPropio;
    private final List<Filtro> historialRival;

    EstadoFake(EspacioDeBusqueda espacio, List<Filtro> historialPropio, List<Filtro> historialRival) {
        this.espacio = espacio;
        this.historialPropio = historialPropio;
        this.historialRival = historialRival;
    }

    static EstadoFake de(EspacioDeBusqueda espacio) {
        return new EstadoFake(espacio, List.of(), List.of());
    }

    static EstadoFake conHistorialPropio(EspacioDeBusqueda espacio, List<Filtro> historialPropio) {
        return new EstadoFake(espacio, historialPropio, List.of());
    }

    @Override public EspacioDeBusqueda getEspacioDeBusqueda()   { return espacio; }
    @Override public List<Filtro>       getHistorialFiltros()      { return historialPropio; }
    @Override public List<Filtro>       getHistorialFiltrosRival() { return historialRival; }
}
