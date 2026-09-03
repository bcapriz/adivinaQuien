package com.uade.app;

import com.uade.dominio.RegistroDePersonajes;

/**
 * Composition root. Arma las dependencias concretas y lanza el modo Humano vs
 * Maquina por consola.
 */
public class Main {

    public static void main(String[] args) {
        Consola consola = new ConsolaEstandar();
        RegistroDePersonajes registro = PersonajesDeEjemplo.cargar();
        new ModoHumanoVsMaquina(consola).jugar(registro);
    }
}
