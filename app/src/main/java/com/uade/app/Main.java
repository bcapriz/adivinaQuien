package com.uade.app;

import com.uade.dominio.RegistroDePersonajes;

/**
 * Composition root. Por ahora solo lanza el modo Humano vs Maquina por consola
 * (Sprint 3). En el Sprint 7 pasa a elegir consola o GUI JavaFX segun un flag.
 */
public class Main {

    public static void main(String[] args) {
        Consola consola = new ConsolaEstandar();
        RegistroDePersonajes registro = PersonajesDeEjemplo.cargar();
        new ModoHumanoVsMaquina(consola).jugar(registro);
    }
}
