package com.uade.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EspacioDeBusquedaTest {

    //  ID  Nombre  Género      Calvo  Lentes  Pelo
    //   1  Ana     FEMENINO    false  true    NEGRO
    //   2  Bob     MASCULINO   true   false   COLORADO
    //   3  Carla   FEMENINO    false  false   AMARILLO
    //   4  Diego   MASCULINO   false  true    NEGRO
    //   5  Elena   FEMENINO    true   false   COLORADO

    private EspacioDeBusqueda espacioCompleto;

    @BeforeEach
    void setUp() {
        var registro = new RegistroDePersonajes();
        registro.agregar("Ana",   Genero.FEMENINO,  false, true,  ColorPelo.NEGRO);
        registro.agregar("Bob",   Genero.MASCULINO, true,  false, ColorPelo.COLORADO);
        registro.agregar("Carla", Genero.FEMENINO,  false, false, ColorPelo.AMARILLO);
        registro.agregar("Diego", Genero.MASCULINO, false, true,  ColorPelo.NEGRO);
        registro.agregar("Elena", Genero.FEMENINO,  true,  false, ColorPelo.COLORADO);
        espacioCompleto = new EspacioDeBusqueda(registro.listar());
    }

    // --- partición por cada categoría ---

    @Test
    void filtroGeneroFemeninoDevuelveSoloMujeres() {
        var resultado = espacioCompleto.aplicarFiltro(new Filtro(Categoria.GENERO, Genero.FEMENINO));

        assertEquals(3, resultado.tamanio()); // Ana, Carla, Elena
        assertTrue(resultado.getCandidatos().stream().allMatch(p -> p.getGenero() == Genero.FEMENINO));
    }

    @Test
    void filtroGeneroMasculinoDevuelveSoloHombres() {
        var resultado = espacioCompleto.aplicarFiltro(new Filtro(Categoria.GENERO, Genero.MASCULINO));

        assertEquals(2, resultado.tamanio()); // Bob, Diego
        assertTrue(resultado.getCandidatos().stream().allMatch(p -> p.getGenero() == Genero.MASCULINO));
    }

    @Test
    void filtroCalvicieVerdaderoDevuelveSoloCalvos() {
        var resultado = espacioCompleto.aplicarFiltro(new Filtro(Categoria.CALVICIE, Boolean.TRUE));

        assertEquals(2, resultado.tamanio()); // Bob, Elena
        assertTrue(resultado.getCandidatos().stream().allMatch(Personaje::isCalvo));
    }

    @Test
    void filtroLentesVerdaderoDevuelveSoloConLentes() {
        var resultado = espacioCompleto.aplicarFiltro(new Filtro(Categoria.LENTES, Boolean.TRUE));

        assertEquals(2, resultado.tamanio()); // Ana, Diego
        assertTrue(resultado.getCandidatos().stream().allMatch(Personaje::isUsaLentes));
    }

    @Test
    void filtroColorPeloNegroDevuelveSoloNegros() {
        var resultado = espacioCompleto.aplicarFiltro(new Filtro(Categoria.COLOR_PELO, ColorPelo.NEGRO));

        assertEquals(2, resultado.tamanio()); // Ana, Diego
        assertTrue(resultado.getCandidatos().stream().allMatch(p -> p.getColorPelo() == ColorPelo.NEGRO));
    }

    @Test
    void filtroColorPeloColoradoDevuelveDos() {
        var resultado = espacioCompleto.aplicarFiltro(new Filtro(Categoria.COLOR_PELO, ColorPelo.COLORADO));

        assertEquals(2, resultado.tamanio()); // Bob, Elena
    }

    // --- caso base: tamanio 1 ---

    @Test
    void filtrosEncadenadosReducenHastaCasoBase() {
        // FEMENINO (3) → NEGRO (Ana, Diego → solo Ana entre las femeninas)
        var porGenero = espacioCompleto.aplicarFiltro(new Filtro(Categoria.GENERO, Genero.FEMENINO));
        var porPelo   = porGenero.aplicarFiltro(new Filtro(Categoria.COLOR_PELO, ColorPelo.NEGRO));

        assertTrue(porPelo.esUnico());
        assertEquals("Ana", porPelo.unico().getNombre());
    }

    @Test
    void esUnicoEsFalsoConMasDeUnCandidato() {
        assertFalse(espacioCompleto.esUnico());
    }

    // --- caso de error: tamanio 0 ---

    @Test
    void filtroSinCoincidenciasDevuelveEspacioVacio() {
        // AMARILLO (Carla) → calvo=true → nadie
        var porAmarillo = espacioCompleto.aplicarFiltro(new Filtro(Categoria.COLOR_PELO, ColorPelo.AMARILLO));
        var resultado   = porAmarillo.aplicarFiltro(new Filtro(Categoria.CALVICIE, Boolean.TRUE));

        assertEquals(0, resultado.tamanio());
        assertFalse(resultado.esUnico());
    }

    @Test
    void unicoLanzaExcepcionSiEspacioVacio() {
        var vacio = new EspacioDeBusqueda(List.of());
        assertThrows(IllegalStateException.class, vacio::unico);
    }

    @Test
    void unicoLanzaExcepcionSiHayVariosCandidatos() {
        assertThrows(IllegalStateException.class, espacioCompleto::unico);
    }

    // --- inmutabilidad ---

    @Test
    void espacioOriginalNoCambiaTrasFiltro() {
        int tamanioOriginal = espacioCompleto.tamanio();
        espacioCompleto.aplicarFiltro(new Filtro(Categoria.GENERO, Genero.FEMENINO));

        assertEquals(tamanioOriginal, espacioCompleto.tamanio());
    }
}
