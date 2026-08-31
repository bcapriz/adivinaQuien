package com.uade.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FiltroTest {

    private Personaje personaje;

    @BeforeEach
    void setUp() {
        // masculino, calvo, sin lentes, pelo colorado
        var registro = new RegistroDePersonajes();
        personaje = registro.agregar("Test", Genero.MASCULINO, true, false, ColorPelo.COLORADO);
    }

    @Test
    void cumpleGeneroCoincide() {
        assertTrue(new Filtro(Categoria.GENERO, Genero.MASCULINO).cumple(personaje));
    }

    @Test
    void cumpleGeneroNoCoincide() {
        assertFalse(new Filtro(Categoria.GENERO, Genero.FEMENINO).cumple(personaje));
    }

    @Test
    void cumpleCalvicieVerdaderoCoincide() {
        assertTrue(new Filtro(Categoria.CALVICIE, Boolean.TRUE).cumple(personaje));
    }

    @Test
    void cumpleCalvicieFalsoNoCoincide() {
        assertFalse(new Filtro(Categoria.CALVICIE, Boolean.FALSE).cumple(personaje));
    }

    @Test
    void cumpleLentesVerdaderoNoCoincide() {
        assertFalse(new Filtro(Categoria.LENTES, Boolean.TRUE).cumple(personaje));
    }

    @Test
    void cumpleLentesFalsoCoincide() {
        assertTrue(new Filtro(Categoria.LENTES, Boolean.FALSE).cumple(personaje));
    }

    @Test
    void cumpleColorPeloCoincide() {
        assertTrue(new Filtro(Categoria.COLOR_PELO, ColorPelo.COLORADO).cumple(personaje));
    }

    @Test
    void cumpleColorPeloNoCoincide() {
        assertFalse(new Filtro(Categoria.COLOR_PELO, ColorPelo.NEGRO).cumple(personaje));
    }
}
