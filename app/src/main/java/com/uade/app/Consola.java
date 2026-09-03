package com.uade.app;

/**
 * Aisla la entrada/salida de consola detras de una interfaz para que
 * {@link ConsolaJugadorHumano} y {@link ModoHumanoVsMaquina} se puedan testear
 * con input simulado, sin acoplarse a {@code System.in}.
 */
public interface Consola {

    /** Muestra una linea de texto al usuario. */
    void mostrar(String linea);

    /** Lee la proxima linea ingresada por el usuario. */
    String leerLinea();
}
