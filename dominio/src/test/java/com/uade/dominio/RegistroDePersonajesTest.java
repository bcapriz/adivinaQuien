package com.uade.dominio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistroDePersonajesTest {

    @Test
    void agregarAsignaIdAutoincremental() {
        var registro = new RegistroDePersonajes();
        var p1 = registro.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);
        var p2 = registro.agregar("Bob", Genero.MASCULINO, true, false, ColorPelo.COLORADO);

        assertEquals(1, p1.getId());
        assertEquals(2, p2.getId());
    }

    @Test
    void agregarPreservaAtributosDelPersonaje() {
        var registro = new RegistroDePersonajes();
        var p = registro.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);

        assertEquals("Ana", p.getNombre());
        assertEquals(Genero.FEMENINO, p.getGenero());
        assertFalse(p.isCalvo());
        assertTrue(p.isUsaLentes());
        assertEquals(ColorPelo.NEGRO, p.getColorPelo());
    }

    @Test
    void listarDevuelvePersonajesEnOrdenDeAlta() {
        var registro = new RegistroDePersonajes();
        registro.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);
        registro.agregar("Bob", Genero.MASCULINO, true, false, ColorPelo.COLORADO);

        var lista = registro.listar();
        assertEquals(2, lista.size());
        assertEquals("Ana", lista.get(0).getNombre());
        assertEquals("Bob", lista.get(1).getNombre());
    }

    @Test
    void listarDevuelveCopiaInmodificable() {
        var registro = new RegistroDePersonajes();
        registro.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);

        var lista = registro.listar();
        assertThrows(UnsupportedOperationException.class, () -> lista.add(null));
    }

    @Test
    void tamanioRefleja23PersonajesCargados() {
        var registro = new RegistroDePersonajes();
        cargar23Personajes(registro);

        assertEquals(23, registro.tamanio());
    }

    @Test
    void idsConsecutivosSinImportarGenero() {
        var registro = new RegistroDePersonajes();
        var f1 = registro.agregar("Ana",    Genero.FEMENINO,  false, false, ColorPelo.NEGRO);
        var m1 = registro.agregar("Bruno",  Genero.MASCULINO, true,  false, ColorPelo.COLORADO);
        var f2 = registro.agregar("Carla",  Genero.FEMENINO,  false, true,  ColorPelo.AMARILLO);

        assertEquals(1, f1.getId());
        assertEquals(2, m1.getId());
        assertEquals(3, f2.getId());
    }

    private void cargar23Personajes(RegistroDePersonajes registro) {
        // Cargados agrupados por género (masculino primero), luego femenino
        registro.agregar("Alejandro",  Genero.MASCULINO, true,  false, ColorPelo.NEGRO);
        registro.agregar("Bruno",      Genero.MASCULINO, false, true,  ColorPelo.COLORADO);
        registro.agregar("Carlos",     Genero.MASCULINO, true,  false, ColorPelo.AMARILLO);
        registro.agregar("Diego",      Genero.MASCULINO, false, false, ColorPelo.NEGRO);
        registro.agregar("Ernesto",    Genero.MASCULINO, true,  true,  ColorPelo.COLORADO);
        registro.agregar("Federico",   Genero.MASCULINO, false, false, ColorPelo.AMARILLO);
        registro.agregar("Gonzalo",    Genero.MASCULINO, true,  false, ColorPelo.NEGRO);
        registro.agregar("Hernán",     Genero.MASCULINO, false, true,  ColorPelo.COLORADO);
        registro.agregar("Ignacio",    Genero.MASCULINO, false, false, ColorPelo.NEGRO);
        registro.agregar("Javier",     Genero.MASCULINO, true,  false, ColorPelo.AMARILLO);
        registro.agregar("Kevin",      Genero.MASCULINO, false, true,  ColorPelo.NEGRO);
        registro.agregar("Lucas",      Genero.MASCULINO, true,  false, ColorPelo.COLORADO);
        registro.agregar("Marcos",     Genero.MASCULINO, false, false, ColorPelo.AMARILLO);
        registro.agregar("Ana",        Genero.FEMENINO,  false, true,  ColorPelo.NEGRO);
        registro.agregar("Beatriz",    Genero.FEMENINO,  false, false, ColorPelo.COLORADO);
        registro.agregar("Claudia",    Genero.FEMENINO,  true,  true,  ColorPelo.AMARILLO);
        registro.agregar("Diana",      Genero.FEMENINO,  false, false, ColorPelo.NEGRO);
        registro.agregar("Elena",      Genero.FEMENINO,  false, true,  ColorPelo.COLORADO);
        registro.agregar("Florencia",  Genero.FEMENINO,  false, false, ColorPelo.NEGRO);
        registro.agregar("Gabriela",   Genero.FEMENINO,  true,  false, ColorPelo.AMARILLO);
        registro.agregar("Hernanda",   Genero.FEMENINO,  false, true,  ColorPelo.COLORADO);
        registro.agregar("Irene",      Genero.FEMENINO,  false, false, ColorPelo.NEGRO);
        registro.agregar("Julia",      Genero.FEMENINO,  false, true,  ColorPelo.AMARILLO);
    }
}
