package com.uade.app;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** {@link Consola} de test: entrega lineas preprogramadas y guarda todo lo mostrado. */
class ConsolaFake implements Consola {

    private final Deque<String> entradas = new ArrayDeque<>();
    final List<String> salidas = new ArrayList<>();

    ConsolaFake(String... lineasDeEntrada) {
        for (String linea : lineasDeEntrada) {
            entradas.addLast(linea);
        }
    }

    @Override
    public void mostrar(String linea) {
        salidas.add(linea);
    }

    @Override
    public String leerLinea() {
        if (entradas.isEmpty()) {
            throw new IllegalStateException("El test no proveyo suficientes lineas de entrada");
        }
        return entradas.removeFirst();
    }

    boolean mostroAlgunaQueContiene(String fragmento) {
        return salidas.stream().anyMatch(s -> s.contains(fragmento));
    }
}
