package com.uade.app;

import java.io.PrintStream;
import java.util.Scanner;

/** Implementacion de {@link Consola} sobre la entrada/salida estandar del proceso. */
public class ConsolaEstandar implements Consola {

    private final Scanner entrada;
    private final PrintStream salida;

    public ConsolaEstandar() {
        this(new Scanner(System.in), System.out);
    }

    public ConsolaEstandar(Scanner entrada, PrintStream salida) {
        this.entrada = entrada;
        this.salida = salida;
    }

    @Override
    public void mostrar(String linea) {
        salida.println(linea);
    }

    @Override
    public String leerLinea() {
        return entrada.nextLine();
    }
}
