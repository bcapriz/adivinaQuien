package com.uade.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class OrdenadorDePersonajes {

    private OrdenadorDePersonajes() {
    }

    public static List<Personaje> ordenarPorNombre(List<Personaje> personajes) {
        if (personajes.size() <= 1) {
            return personajes;
        }
        int medio = personajes.size() / 2;
        List<Personaje> izquierda = ordenarPorNombre(personajes.subList(0, medio));
        List<Personaje> derecha = ordenarPorNombre(personajes.subList(medio, personajes.size()));
        return combinar(izquierda, derecha);
    }

    private static List<Personaje> combinar(List<Personaje> izquierda, List<Personaje> derecha) {
        List<Personaje> resultado = new ArrayList<>(izquierda.size() + derecha.size());
        int i = 0;
        int j = 0;
        while (i < izquierda.size() && j < derecha.size()) {
            Personaje siguiente = izquierda.get(i).getNombre().compareTo(derecha.get(j).getNombre()) <= 0
                    ? izquierda.get(i++)
                    : derecha.get(j++);
            resultado.add(siguiente);
        }
        resultado.addAll(izquierda.subList(i, izquierda.size()));
        resultado.addAll(derecha.subList(j, derecha.size()));
        return resultado;
    }

    /** Θ(n²), solo para la comparacion empirica de 4.7.1 — nunca para uso real. */
    public static List<Personaje> ordenarPorNombreBurbuja(List<Personaje> personajes) {
        List<Personaje> copia = new ArrayList<>(personajes);
        for (int i = 0; i < copia.size() - 1; i++) {
            for (int j = 0; j < copia.size() - 1 - i; j++) {
                if (copia.get(j).getNombre().compareTo(copia.get(j + 1).getNombre()) > 0) {
                    Collections.swap(copia, j, j + 1);
                }
            }
        }
        return copia;
    }
}
