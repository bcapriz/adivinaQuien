package com.uade.jugadores;

import com.uade.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MaquinaBasicaTest {

    //  ID  Nombre  Género      Calvo  Lentes  Pelo
    //   1  Ana     FEMENINO    false  true    NEGRO
    //   2  Bob     MASCULINO   true   false   COLORADO
    //   3  Carla   FEMENINO    false  false   AMARILLO
    //   4  Diego   MASCULINO   false  true    NEGRO
    //   5  Elena   FEMENINO    true   false   COLORADO

    private RegistroDePersonajes registro;
    private EspacioDeBusqueda espacioCompleto;
    private final MaquinaBasica maquina = new MaquinaBasica();

    @BeforeEach
    void setUp() {
        registro = new RegistroDePersonajes();
        registro.agregar("Ana",   Genero.FEMENINO,  false, true,  ColorPelo.NEGRO);
        registro.agregar("Bob",   Genero.MASCULINO, true,  false, ColorPelo.COLORADO);
        registro.agregar("Carla", Genero.FEMENINO,  false, false, ColorPelo.AMARILLO);
        registro.agregar("Diego", Genero.MASCULINO, false, true,  ColorPelo.NEGRO);
        registro.agregar("Elena", Genero.FEMENINO,  true,  false, ColorPelo.COLORADO);
        espacioCompleto = new EspacioDeBusqueda(registro.listar());
    }

    private EstadoDePartidaVisible estado(EspacioDeBusqueda espacio, List<Filtro> historialPropio) {
        return new EstadoDePartidaVisible() {
            @Override public EspacioDeBusqueda getEspacioDeBusqueda()       { return espacio; }
            @Override public List<Filtro>       getHistorialFiltros()        { return historialPropio; }
            @Override public List<Filtro>       getHistorialFiltrosRival()   { return List.of(); }
        };
    }

    private Categoria categoriaDe(Accion accion) {
        return assertInstanceOf(Accion.AplicarFiltro.class, accion).filtro().getCategoria();
    }

    @Test
    void primerTurnoPreguntaPorGenero() {
        Accion accion = maquina.decidirTurno(estado(espacioCompleto, List.of()));

        assertEquals(Categoria.GENERO, categoriaDe(accion));
    }

    @Test
    void avanzaPorLasCategoriasEnElOrdenFijo() {
        assertEquals(Categoria.CALVICIE, categoriaDe(maquina.decidirTurno(
                estado(espacioCompleto, List.of(f(Categoria.GENERO))))));
        assertEquals(Categoria.LENTES, categoriaDe(maquina.decidirTurno(
                estado(espacioCompleto, List.of(f(Categoria.GENERO), f(Categoria.CALVICIE))))));
        assertEquals(Categoria.COLOR_PELO, categoriaDe(maquina.decidirTurno(
                estado(espacioCompleto, List.of(f(Categoria.GENERO), f(Categoria.CALVICIE), f(Categoria.LENTES))))));
    }

    @Test
    void nunca_repite_una_categoria_ya_usada() {
        List<Filtro> historial = List.of(f(Categoria.GENERO), f(Categoria.LENTES));

        Categoria elegida = categoriaDe(maquina.decidirTurno(estado(espacioCompleto, historial)));

        assertEquals(Categoria.CALVICIE, elegida); // primera del ORDEN_FIJO que queda libre
    }

    @Test
    void elFiltroUsaElValorDelPrimerCandidato() {
        // primer candidato = Ana (FEMENINO)
        Filtro filtro = assertInstanceOf(Accion.AplicarFiltro.class,
                maquina.decidirTurno(estado(espacioCompleto, List.of()))).filtro();

        assertEquals(Genero.FEMENINO, filtro.getValorEsperado());
    }

    @Test
    void conUnUnicoCandidatoLanzaAdivinanza() {
        EspacioDeBusqueda unico = espacioCompleto
                .aplicarFiltro(new Filtro(Categoria.GENERO, Genero.MASCULINO), true)
                .aplicarFiltro(new Filtro(Categoria.LENTES, Boolean.TRUE), true); // solo Diego

        Accion accion = maquina.decidirTurno(estado(unico, List.of()));

        Personaje adivinado = assertInstanceOf(Accion.Adivinanza.class, accion).personaje();
        assertEquals("Diego", adivinado.getNombre());
    }

    @Test
    void sinCategoriasNuevasYVariosCandidatosAdivinaACiegas() {
        List<Filtro> todas = List.of(
                f(Categoria.GENERO), f(Categoria.CALVICIE), f(Categoria.LENTES), f(Categoria.COLOR_PELO));

        Accion accion = maquina.decidirTurno(estado(espacioCompleto, todas));

        assertInstanceOf(Accion.Adivinanza.class, accion);
    }

    private static Filtro f(Categoria categoria) {
        return new Filtro(categoria, "irrelevante-para-el-test");
    }
}
