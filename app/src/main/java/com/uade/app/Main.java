package com.uade.app;

import com.uade.dominio.RegistroDePersonajes;

/**
 * Composition root. Arma las dependencias concretas y lanza el modo elegido.
 */
public class Main {

    public static void main(String[] args) {
        Consola consola = new ConsolaEstandar();
        RegistroDePersonajes registro = PersonajesDeEjemplo.cargar();

        consola.mostrar("=== Adivina Quien ===");
        consola.mostrar("1) Humano vs Maquina");
        consola.mostrar("2) Espectador: Maquina vs Maquina");
        consola.mostrar("Opcion: ");

        if (consola.leerLinea().trim().equals("2")) {
            new ModoMaquinaVsMaquina(consola).jugar(registro);
        } else {
            new ModoHumanoVsMaquina(consola).jugar(registro);
        }
    }
}
