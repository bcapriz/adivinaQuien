package com.uade.dominio;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Mide MergeSort vs Bubble Sort sobre 23 personajes; no falla por tiempo, solo por correctitud. */
class OrdenadorDePersonajesBenchmarkTest {

    private static final int REPETICIONES = 10_000;

    @Test
    void compararMergeSortConBubbleSort() {
        List<Personaje> personajes = roster23();

        assertEquals(
                OrdenadorDePersonajes.ordenarPorNombre(personajes),
                OrdenadorDePersonajes.ordenarPorNombreBurbuja(personajes));

        long nsMerge = medir(() -> OrdenadorDePersonajes.ordenarPorNombre(personajes));
        long nsBurbuja = medir(() -> OrdenadorDePersonajes.ordenarPorNombreBurbuja(personajes));

        System.out.printf("%n--- Benchmark ordenamiento (n=%d, %d repeticiones) ---%n",
                personajes.size(), REPETICIONES);
        System.out.printf("MergeSort:  %.3f ms total (%.5f ms/corrida)%n",
                nsMerge / 1_000_000.0, nsMerge / 1_000_000.0 / REPETICIONES);
        System.out.printf("BubbleSort: %.3f ms total (%.5f ms/corrida)%n",
                nsBurbuja / 1_000_000.0, nsBurbuja / 1_000_000.0 / REPETICIONES);
    }

    private long medir(Runnable accion) {
        long inicio = System.nanoTime();
        for (int i = 0; i < REPETICIONES; i++) {
            accion.run();
        }
        return System.nanoTime() - inicio;
    }

    private List<Personaje> roster23() {
        var r = new RegistroDePersonajes();
        r.agregar("Alejandro", Genero.MASCULINO, true, false, ColorPelo.NEGRO);
        r.agregar("Bruno", Genero.MASCULINO, false, true, ColorPelo.COLORADO);
        r.agregar("Carlos", Genero.MASCULINO, true, false, ColorPelo.AMARILLO);
        r.agregar("Diego", Genero.MASCULINO, false, false, ColorPelo.NEGRO);
        r.agregar("Ernesto", Genero.MASCULINO, true, true, ColorPelo.COLORADO);
        r.agregar("Federico", Genero.MASCULINO, false, false, ColorPelo.AMARILLO);
        r.agregar("Gonzalo", Genero.MASCULINO, true, false, ColorPelo.NEGRO);
        r.agregar("Hernan", Genero.MASCULINO, false, true, ColorPelo.COLORADO);
        r.agregar("Ignacio", Genero.MASCULINO, false, false, ColorPelo.NEGRO);
        r.agregar("Javier", Genero.MASCULINO, true, false, ColorPelo.AMARILLO);
        r.agregar("Kevin", Genero.MASCULINO, false, true, ColorPelo.NEGRO);
        r.agregar("Lucas", Genero.MASCULINO, true, false, ColorPelo.COLORADO);
        r.agregar("Marcos", Genero.MASCULINO, false, false, ColorPelo.AMARILLO);
        r.agregar("Ana", Genero.FEMENINO, false, true, ColorPelo.NEGRO);
        r.agregar("Beatriz", Genero.FEMENINO, false, false, ColorPelo.COLORADO);
        r.agregar("Claudia", Genero.FEMENINO, true, true, ColorPelo.AMARILLO);
        r.agregar("Diana", Genero.FEMENINO, false, false, ColorPelo.NEGRO);
        r.agregar("Elena", Genero.FEMENINO, false, true, ColorPelo.COLORADO);
        r.agregar("Florencia", Genero.FEMENINO, false, false, ColorPelo.NEGRO);
        r.agregar("Gabriela", Genero.FEMENINO, true, false, ColorPelo.AMARILLO);
        r.agregar("Hernanda", Genero.FEMENINO, false, true, ColorPelo.COLORADO);
        r.agregar("Irene", Genero.FEMENINO, false, false, ColorPelo.NEGRO);
        r.agregar("Julia", Genero.FEMENINO, false, true, ColorPelo.AMARILLO);
        return r.listar();
    }
}
