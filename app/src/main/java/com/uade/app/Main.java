package com.uade.app;

import com.uade.dominio.MarcadorRepository;
import com.uade.dominio.RegistroDePersonajes;
import com.uade.persistencia.MarcadorRepositoryArchivo;

import javax.swing.SwingUtilities;
import java.nio.file.Path;

/**
 * Composition root. Arma las dependencias concretas y lanza el modo elegido.
 */
public class Main {

    public static void main(String[] args) {
        RegistroDePersonajes registro = PersonajesDeEjemplo.cargar();
        MarcadorRepository marcador = new MarcadorRepositoryArchivo(Path.of("marcador.properties"));

        if (args.length > 0 && args[0].equals("--gui")) {
            lanzarGui(registro, marcador);
            return;
        }

        Consola consola = new ConsolaEstandar();
        mostrarBienvenida(consola);

        if (consola.leerLinea().trim().equals("2")) {
            lanzarGui(registro, marcador);
            return;
        }

        consola.mostrar("\n1) Humano vs Maquina");
        consola.mostrar("2) Espectador: Maquina vs Maquina");
        consola.mostrar("Opcion: ");

        if (consola.leerLinea().trim().equals("2")) {
            new ModoMaquinaVsMaquina(consola, marcador).jugar(registro);
        } else {
            new ModoHumanoVsMaquina(consola, marcador).jugar(registro);
        }
    }

    private static void mostrarBienvenida(Consola consola) {
        consola.mostrar("======================================");
        consola.mostrar("            ADIVINA QUIEN");
        consola.mostrar("======================================");
        consola.mostrar("Bienvenido!");
        consola.mostrar("");
        consola.mostrar("¿Como queres jugar?");
        consola.mostrar("1) Por consola");
        consola.mostrar("2) Con interfaz grafica");
        consola.mostrar("Opcion: ");
    }

    private static void lanzarGui(RegistroDePersonajes registro, MarcadorRepository marcador) {
        SwingUtilities.invokeLater(() -> new VentanaJuego(registro, marcador).setVisible(true));
    }
}
