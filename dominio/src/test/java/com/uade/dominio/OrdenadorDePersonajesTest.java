package com.uade.dominio;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrdenadorDePersonajesTest {

    private RegistroDePersonajes registroDesordenado() {
        var r = new RegistroDePersonajes();
        r.agregar("Diego", Genero.MASCULINO, false, true, ColorPelo.NEGRO);
        r.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);
        r.agregar("Carla", Genero.FEMENINO, false, false, ColorPelo.AMARILLO);
        r.agregar("Bob", Genero.MASCULINO, true, false, ColorPelo.COLORADO);
        return r;
    }

    @Test
    void ordenaAlfabeticamentePorNombre() {
        var ordenado = OrdenadorDePersonajes.ordenarPorNombre(registroDesordenado().listar());

        assertEquals(List.of("Ana", "Bob", "Carla", "Diego"),
                ordenado.stream().map(Personaje::getNombre).toList());
    }

    @Test
    void esEstableConNombresRepetidos() {
        var r = new RegistroDePersonajes();
        Personaje anaUno = r.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);
        Personaje anaDos = r.agregar("Ana", Genero.FEMENINO, true, false, ColorPelo.COLORADO);

        assertEquals(List.of(anaUno, anaDos), OrdenadorDePersonajes.ordenarPorNombre(r.listar()));
    }

    @Test
    void casoBaseListaVacia() {
        assertTrue(OrdenadorDePersonajes.ordenarPorNombre(List.of()).isEmpty());
    }

    @Test
    void casoBaseUnElemento() {
        var r = new RegistroDePersonajes();
        Personaje unico = r.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);

        assertEquals(List.of(unico), OrdenadorDePersonajes.ordenarPorNombre(r.listar()));
    }

    @Test
    void noMutaLaListaDeEntrada() {
        List<Personaje> original = registroDesordenado().listar();
        List<Personaje> copia = List.copyOf(original);

        OrdenadorDePersonajes.ordenarPorNombre(original);

        assertEquals(copia, original);
    }

    @Test
    void burbujaProduceElMismoResultadoQueMergeSort() {
        List<Personaje> personajes = registroDesordenado().listar();

        assertEquals(
                OrdenadorDePersonajes.ordenarPorNombre(personajes),
                OrdenadorDePersonajes.ordenarPorNombreBurbuja(personajes));
    }

    @Test
    void burbujaNoMutaLaListaDeEntrada() {
        List<Personaje> original = registroDesordenado().listar();
        List<Personaje> copia = List.copyOf(original);

        OrdenadorDePersonajes.ordenarPorNombreBurbuja(original);

        assertEquals(copia, original);
    }
}
