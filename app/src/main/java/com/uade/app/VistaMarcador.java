package com.uade.app;

import com.uade.dominio.MarcadorRepository;

/** Imprime el marcador por consola. */
final class VistaMarcador {

    private VistaMarcador() {
    }

    static void imprimir(Consola consola, MarcadorRepository marcador) {
        consola.mostrar("\n--- Marcador ---");
        var top = marcador.top();
        if (top.isEmpty()) {
            consola.mostrar("  (vacio)");
            return;
        }
        top.forEach(entrada -> consola.mostrar("  " + entrada.getKey() + ": " + entrada.getValue()));
    }
}
