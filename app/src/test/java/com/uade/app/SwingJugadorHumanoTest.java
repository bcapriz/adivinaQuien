package com.uade.app;

import com.uade.dominio.*;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SwingJugadorHumanoTest {

    @Test
    void decidirTurnoDevuelveLaAdivinanzaQueElUsuarioArma() throws InterruptedException {
        var registro = new RegistroDePersonajes();
        Personaje ana = registro.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);
        registro.agregar("Bob", Genero.MASCULINO, true, false, ColorPelo.COLORADO);
        var estado = estadoCon(new EspacioDeBusqueda(registro.listar()));

        var panel = new PanelDeJugada();
        var humano = new SwingJugadorHumano(null, panel);
        AtomicReference<Accion> resultado = new AtomicReference<>();

        Thread hiloDeJuego = new Thread(() -> resultado.set(humano.decidirTurno(estado)));
        hiloDeJuego.start();

        clickearOpcion(panel, "Arriesgar un nombre");
        clickearOpcion(panel, "Ana");
        hiloDeJuego.join();

        assertEquals(new Accion.Adivinanza(ana), resultado.get());
    }

    @Test
    void decidirTurnoDevuelveElFiltroQueElUsuarioArma() throws InterruptedException {
        var registro = new RegistroDePersonajes();
        registro.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);
        var estado = estadoCon(new EspacioDeBusqueda(registro.listar()));

        var panel = new PanelDeJugada();
        var humano = new SwingJugadorHumano(null, panel);
        AtomicReference<Accion> resultado = new AtomicReference<>();

        Thread hiloDeJuego = new Thread(() -> resultado.set(humano.decidirTurno(estado)));
        hiloDeJuego.start();

        clickearOpcion(panel, "Preguntar algo");
        clickearOpcion(panel, "¿Es calvo/a?");
        hiloDeJuego.join();

        Filtro filtro = assertInstanceOf(Accion.AplicarFiltro.class, resultado.get()).filtro();
        assertEquals(Categoria.CALVICIE, filtro.getCategoria());
        assertEquals(Boolean.TRUE, filtro.getValorEsperado());
    }

    @Test
    void colorDePeloPreguntaDirectamenteElColorEspecifico() throws InterruptedException {
        var registro = new RegistroDePersonajes();
        registro.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);
        var estado = estadoCon(new EspacioDeBusqueda(registro.listar()));

        var panel = new PanelDeJugada();
        var humano = new SwingJugadorHumano(null, panel);
        AtomicReference<Accion> resultado = new AtomicReference<>();

        Thread hiloDeJuego = new Thread(() -> resultado.set(humano.decidirTurno(estado)));
        hiloDeJuego.start();

        clickearOpcion(panel, "Preguntar algo");
        clickearOpcion(panel, "¿De que color tiene el pelo?");
        clickearOpcion(panel, "Negro");
        hiloDeJuego.join();

        Filtro filtro = assertInstanceOf(Accion.AplicarFiltro.class, resultado.get()).filtro();
        assertEquals(Categoria.COLOR_PELO, filtro.getCategoria());
        assertEquals(ColorPelo.NEGRO, filtro.getValorEsperado());
    }

    @Test
    void preguntaDeGeneroPermiteElegirHombreOMujer() throws InterruptedException {
        var registro = new RegistroDePersonajes();
        registro.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);
        var estado = estadoCon(new EspacioDeBusqueda(registro.listar()));

        var panel = new PanelDeJugada();
        var humano = new SwingJugadorHumano(null, panel);
        AtomicReference<Accion> resultado = new AtomicReference<>();

        Thread hiloDeJuego = new Thread(() -> resultado.set(humano.decidirTurno(estado)));
        hiloDeJuego.start();

        clickearOpcion(panel, "Preguntar algo");
        clickearOpcion(panel, "¿Es hombre o mujer?");
        clickearOpcion(panel, "Mujer");
        hiloDeJuego.join();

        Filtro filtro = assertInstanceOf(Accion.AplicarFiltro.class, resultado.get()).filtro();
        assertEquals(Categoria.GENERO, filtro.getCategoria());
        assertEquals(Genero.FEMENINO, filtro.getValorEsperado());
    }

    @Test
    void confirmarRendirseDevuelveAccionRendirse() throws InterruptedException {
        var registro = new RegistroDePersonajes();
        registro.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);
        var estado = estadoCon(new EspacioDeBusqueda(registro.listar()));

        var panel = new PanelDeJugada();
        var humano = new SwingJugadorHumano(null, panel);
        AtomicReference<Accion> resultado = new AtomicReference<>();

        Thread hiloDeJuego = new Thread(() -> resultado.set(humano.decidirTurno(estado)));
        hiloDeJuego.start();

        clickearOpcion(panel, "Rendirme");
        clickearOpcion(panel, "Si, me rindo");
        hiloDeJuego.join();

        assertInstanceOf(Accion.Rendirse.class, resultado.get());
    }

    @Test
    void arrepentirseDeRendirseVuelveAMostrarLasOpcionesOriginales() throws InterruptedException {
        var registro = new RegistroDePersonajes();
        registro.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);
        var estado = estadoCon(new EspacioDeBusqueda(registro.listar()));

        var panel = new PanelDeJugada();
        var humano = new SwingJugadorHumano(null, panel);
        AtomicReference<Accion> resultado = new AtomicReference<>();

        Thread hiloDeJuego = new Thread(() -> resultado.set(humano.decidirTurno(estado)));
        hiloDeJuego.start();

        clickearOpcion(panel, "Rendirme");
        clickearOpcion(panel, "No, sigo jugando");
        clickearOpcion(panel, "Arriesgar un nombre");
        clickearOpcion(panel, "Ana");
        hiloDeJuego.join();

        assertInstanceOf(Accion.Adivinanza.class, resultado.get());
    }

    @Test
    void unaCategoriaYaPreguntadaApareceDeshabilitada() throws InterruptedException {
        var registro = new RegistroDePersonajes();
        registro.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);
        var historial = List.of(new Filtro(Categoria.LENTES, Boolean.TRUE));
        var estado = estadoCon(new EspacioDeBusqueda(registro.listar()), historial);

        var panel = new PanelDeJugada();
        var humano = new SwingJugadorHumano(null, panel);
        Thread hiloDeJuego = new Thread(() -> humano.decidirTurno(estado));
        hiloDeJuego.setDaemon(true); // se queda esperando una jugada que este test no envia
        hiloDeJuego.start();

        clickearOpcion(panel, "Preguntar algo");
        JButton lentes = esperarBoton(panel, "¿Usa lentes?");

        assertFalse(lentes.isEnabled());
    }

    private void clickearOpcion(PanelDeJugada panel, String texto) throws InterruptedException {
        JButton boton = esperarBoton(panel, texto);
        boton.doClick();
    }

    private JButton esperarBoton(PanelDeJugada panel, String texto) throws InterruptedException {
        for (int i = 0; i < 200; i++) {
            for (Component componente : panel.opciones.getComponents()) {
                if (componente instanceof JButton boton && texto.equals(boton.getText())) {
                    return boton;
                }
            }
            Thread.sleep(5);
        }
        throw new AssertionError("No aparecio un boton con texto '" + texto + "'");
    }

    private EstadoDePartidaVisible estadoCon(EspacioDeBusqueda espacio) {
        return estadoCon(espacio, List.of());
    }

    private EstadoDePartidaVisible estadoCon(EspacioDeBusqueda espacio, List<Filtro> historialPropio) {
        return new EstadoDePartidaVisible() {
            @Override public EspacioDeBusqueda getEspacioDeBusqueda()     { return espacio; }
            @Override public List<Filtro> getHistorialFiltros()            { return historialPropio; }
            @Override public List<Filtro> getHistorialFiltrosRival()       { return List.of(); }
        };
    }
}
