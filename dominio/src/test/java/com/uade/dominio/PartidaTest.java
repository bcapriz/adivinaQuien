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

    // --- evaluacion del filtro contra el secreto del rival ---

    @Test
    void respuestaDelFiltroEsTrueCuandoElSecretoDelRivalLoCumple() {
        // A pregunta GENERO=MASCULINO; el secreto de B es Bob (MASCULINO) -> SI
        Jugador a = jugadorFijo(ana, new Accion.AplicarFiltro(new Filtro(Categoria.GENERO, Genero.MASCULINO)));
        Jugador b = soloElije(bob);

        var resultado = new Partida(a, b, registro).jugarTurno();

        assertEquals(Boolean.TRUE, resultado.getRespuestaFiltro());
    }

    @Test
    void respuestaDelFiltroEsFalseCuandoElSecretoDelRivalNoLoCumple() {
        // A pregunta GENERO=MASCULINO; el secreto de B es Ana (FEMENINO) -> NO
        Jugador a = jugadorFijo(bob, new Accion.AplicarFiltro(new Filtro(Categoria.GENERO, Genero.MASCULINO)));
        Jugador b = soloElije(ana);

        var resultado = new Partida(a, b, registro).jugarTurno();

        assertEquals(Boolean.FALSE, resultado.getRespuestaFiltro());
    }

    @Test
    void adivinanzaNoTieneRespuestaDeFiltro() {
        Jugador a = jugadorFijo(ana, new Accion.Adivinanza(carla));
        Jugador b = soloElije(bob);

        var resultado = new Partida(a, b, registro).jugarTurno();

        assertNull(resultado.getRespuestaFiltro());
    }

    @Test
    void unJugadorConvergeAlSecretoDelRivalAunConRespuestasNegativas() {
        // Secreto de B = Diego (id 4). El cazador filtra por categorias sucesivas
        // usando como referencia el primer candidato visible; la particion por
        // respuesta real (incluida "no") mantiene a Diego en el conjunto.
        Personaje diego = registro.listar().get(3);
        Jugador cazador = cazadorSecuencial(ana);
        Jugador b = soloElije(diego);

        var partida = new Partida(cazador, b, registro);
        Jugador ganador = null;
        for (int i = 0; i < 12 && !partida.estaTerminada(); i++) {
            ganador = partida.jugarTurno().getGanador();
        }

        assertTrue(partida.estaTerminada());
        assertEquals(cazador, ganador);
    }

    private Jugador cazadorSecuencial(Personaje propio) {
        return new Jugador() {
            private final java.util.List<Categoria> pendientes =
                    new java.util.ArrayList<>(Categoria.ORDEN_FIJO);

            @Override public Personaje elegirPersonaje(RegistroDePersonajes r, List<Personaje> ya) { return propio; }

            @Override public Accion decidirTurno(EstadoDePartidaVisible estado) {
                EspacioDeBusqueda espacio = estado.getEspacioDeBusqueda();
                if (espacio.esUnico()) {
                    return new Accion.Adivinanza(espacio.unico());
                }
                Categoria categoria = pendientes.remove(0);
                Personaje referencia = espacio.getCandidatos().get(0);
                return new Accion.AplicarFiltro(filtroDe(categoria, referencia));
            }
        };
    }

    private static Filtro filtroDe(Categoria categoria, Personaje p) {
        return switch (categoria) {
            case GENERO     -> new Filtro(categoria, p.getGenero());
            case CALVICIE   -> new Filtro(categoria, p.isCalvo());
            case LENTES     -> new Filtro(categoria, p.isUsaLentes());
            case COLOR_PELO -> new Filtro(categoria, p.getColorPelo());
        };
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
