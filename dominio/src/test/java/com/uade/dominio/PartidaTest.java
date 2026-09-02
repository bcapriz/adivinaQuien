package com.uade.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PartidaTest {

    private RegistroDePersonajes registro;
    private Personaje ana;
    private Personaje bob;
    private Personaje carla;

    @BeforeEach
    void setUp() {
        registro = new RegistroDePersonajes();
        ana   = registro.agregar("Ana",   Genero.FEMENINO,  false, true,  ColorPelo.NEGRO);
        bob   = registro.agregar("Bob",   Genero.MASCULINO, true,  false, ColorPelo.COLORADO);
        carla = registro.agregar("Carla", Genero.FEMENINO,  false, false, ColorPelo.AMARILLO);
              registro.agregar("Diego", Genero.MASCULINO, false, true,  ColorPelo.NEGRO);
    }

    private Jugador jugadorFijo(Personaje elegido, Accion accion) {
        return new Jugador() {
            @Override public Personaje elegirPersonaje(RegistroDePersonajes r, List<Personaje> ya) { return elegido; }
            @Override public Accion decidirTurno(EstadoDePartidaVisible e) { return accion; }
        };
    }

    private Jugador soloElije(Personaje elegido) {
        return jugadorFijo(elegido, null);
    }

    // --- encapsulamiento del secreto ---

    @Test
    void noHayMetodoPublicoQueDevuelvaPersonaje() {
        for (var metodo : Partida.class.getMethods()) {
            assertFalse(
                metodo.getReturnType().equals(Personaje.class),
                "Metodo publico expone Personaje: " + metodo.getName()
            );
        }
    }

    // --- adivinanza ---

    @Test
    void adivinanzaCorrectaTerminaLaPartida() {
        Jugador a = jugadorFijo(ana, new Accion.Adivinanza(bob));
        Jugador b = soloElije(bob);

        var partida = new Partida(a, b, registro);
        var resultado = partida.jugarTurno();

        assertTrue(resultado.isPartidaTerminada());
        assertTrue(partida.estaTerminada());
        assertEquals(a, partida.ganador());
    }

    @Test
    void adivinanzaIncorrectaNoTerminaLaPartida() {
        Jugador a = jugadorFijo(ana, new Accion.Adivinanza(carla)); // carla != bob (el secreto de B)
        Jugador b = soloElije(bob);

        var partida = new Partida(a, b, registro);
        var resultado = partida.jugarTurno();

        assertFalse(resultado.isPartidaTerminada());
        assertFalse(partida.estaTerminada());
    }

    // --- filtro ---

    @Test
    void aplicarFiltroRegistraEnHistorial() {
        Filtro filtro = new Filtro(Categoria.GENERO, Genero.MASCULINO);
        Jugador a = jugadorFijo(ana, new Accion.AplicarFiltro(filtro));
        Jugador b = soloElije(bob);

        var partida = new Partida(a, b, registro);
        partida.jugarTurno();

        var historial = partida.getHistorialFiltros(a);
        assertEquals(1, historial.size());
        assertEquals(Categoria.GENERO, historial.get(0).getCategoria());
    }

    @Test
    void historialEsInmodificableDesdeAfuera() {
        Jugador a = jugadorFijo(ana, new Accion.AplicarFiltro(new Filtro(Categoria.CALVICIE, Boolean.TRUE)));
        Jugador b = soloElije(bob);

        var partida = new Partida(a, b, registro);
        partida.jugarTurno();

        assertThrows(UnsupportedOperationException.class,
            () -> partida.getHistorialFiltros(a).add(new Filtro(Categoria.LENTES, Boolean.FALSE)));
    }

    // --- estado de la partida ---

    @Test
    void ganadorLanzaExcepcionSiNoTermino() {
        var partida = new Partida(soloElije(ana), soloElije(bob), registro);
        assertThrows(IllegalStateException.class, partida::ganador);
    }

    @Test
    void jugarTurnoLanzaExcepcionSiYaTermino() {
        Jugador a = jugadorFijo(ana, new Accion.Adivinanza(bob));
        Jugador b = soloElije(bob);

        var partida = new Partida(a, b, registro);
        partida.jugarTurno();

        assertThrows(IllegalStateException.class, partida::jugarTurno);
    }

    @Test
    void turnosAlternanEntreJugadores() {
        Filtro filtro = new Filtro(Categoria.CALVICIE, Boolean.TRUE);
        Jugador a = jugadorFijo(ana, new Accion.AplicarFiltro(filtro));
        Jugador b = jugadorFijo(bob, new Accion.AplicarFiltro(filtro));

        var partida = new Partida(a, b, registro);
        var r1 = partida.jugarTurno(); // turno de A
        var r2 = partida.jugarTurno(); // turno de B

        assertEquals(a, r1.getJugador());
        assertEquals(b, r2.getJugador());
    }
}
