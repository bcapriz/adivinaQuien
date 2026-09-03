package com.uade.app;

import com.uade.dominio.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConsolaJugadorHumanoTest {

    private final RegistroDePersonajes registro = PersonajesDeEjemplo.cargar();

    private EstadoDePartidaVisible estadoConEspacioCompleto() {
        EspacioDeBusqueda espacio = new EspacioDeBusqueda(registro.listar());
        return new EstadoDePartidaVisible() {
            @Override public EspacioDeBusqueda getEspacioDeBusqueda()     { return espacio; }
            @Override public List<Filtro>       getHistorialFiltros()      { return List.of(); }
            @Override public List<Filtro>       getHistorialFiltrosRival() { return List.of(); }
        };
    }

    @Test
    void elegirPersonajeDevuelveElDelNumeroIngresado() {
        var humano = new ConsolaJugadorHumano(new ConsolaFake("5"));

        Personaje elegido = humano.elegirPersonaje(registro, List.of());

        assertEquals(5, elegido.getId());
    }

    @Test
    void elegirPersonajeRechazaUnNumeroYaTomadoYReintenta() {
        Personaje yaTomado = registro.listar().get(4); // id 5
        var humano = new ConsolaJugadorHumano(new ConsolaFake("5", "6"));

        Personaje elegido = humano.elegirPersonaje(registro, List.of(yaTomado));

        assertEquals(6, elegido.getId());
    }

    @Test
    void decidirTurnoConstruyeUnFiltroDeGeneroMasculino() {
        // 1 = aplicar filtro, 1 = categoria GENERO, 1 = MASCULINO
        var humano = new ConsolaJugadorHumano(new ConsolaFake("1", "1", "1"));

        Accion accion = humano.decidirTurno(estadoConEspacioCompleto());

        Filtro filtro = assertInstanceOf(Accion.AplicarFiltro.class, accion).filtro();
        assertEquals(Categoria.GENERO, filtro.getCategoria());
        assertEquals(Genero.MASCULINO, filtro.getValorEsperado());
    }

    @Test
    void decidirTurnoConstruyeUnFiltroDeColorPelo() {
        // 1 = filtro, 4 = COLOR_PELO, 3 = AMARILLO
        var humano = new ConsolaJugadorHumano(new ConsolaFake("1", "4", "3"));

        Filtro filtro = assertInstanceOf(Accion.AplicarFiltro.class,
                humano.decidirTurno(estadoConEspacioCompleto())).filtro();

        assertEquals(Categoria.COLOR_PELO, filtro.getCategoria());
        assertEquals(ColorPelo.AMARILLO, filtro.getValorEsperado());
    }

    @Test
    void decidirTurnoLanzaUnaAdivinanza() {
        // 2 = adivinar, 7 = personaje id 7
        var humano = new ConsolaJugadorHumano(new ConsolaFake("2", "7"));

        Accion accion = humano.decidirTurno(estadoConEspacioCompleto());

        Personaje adivinado = assertInstanceOf(Accion.Adivinanza.class, accion).personaje();
        assertEquals(7, adivinado.getId());
    }

    @Test
    void reintentaAnteEntradaNoNumerica() {
        var humano = new ConsolaJugadorHumano(new ConsolaFake("no-numero", "2", "7"));

        Accion accion = humano.decidirTurno(estadoConEspacioCompleto());

        assertInstanceOf(Accion.Adivinanza.class, accion);
    }
}
