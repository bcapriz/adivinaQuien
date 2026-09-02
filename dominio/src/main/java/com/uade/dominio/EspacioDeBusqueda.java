package com.uade.dominio;

import java.util.ArrayList;
import java.util.List;

public class EspacioDeBusqueda {

    private final List<Personaje> candidatos;

    public EspacioDeBusqueda(List<Personaje> candidatos) {
        this.candidatos = List.copyOf(candidatos);
    }

    /**
     * DIVIDE & CONQUISTA — paso de particion
     *
     * Analogia con busqueda binaria: en vez de dividir un vector ordenado por la
     * mitad, dividimos el conjunto de candidatos segun una categoria (genero,
     * calvicie, lentes, color de pelo). El subconjunto que NO cumple el filtro
     * queda descartado; el que si cumple es el nuevo problema de menor tamano.
     *
     * Complejidad de este metodo: Theta(n) sobre el subconjunto vigente — recorrido
     * lineal sin estructura de orden previa que aprovechar (ver SDD 4.2, ultimo punto).
     *
     * Complejidad total de una partida (caso promedio, filtro equilibrado):
     *   T(n) = T(n/2) + Theta(n)  →  a=1, b=2, k=1, a < b^k  →  T(n) ∈ Theta(n)
     * Cota inferior de preguntas: ceil(log2(23)) = 5 (SDD 4.5).
     *
     * Caso base de la recursion: esUnico() — cuando tamanio()==1 se lanza
     * la adivinanza directa sin necesidad de mas filtros.
     * Caso de error: tamanio()==0 indica filtro inconsistente con respuesta
     * previa; se trata como excepcion de dominio en la capa de Partida.
     */
    public EspacioDeBusqueda aplicarFiltro(Filtro filtro) {
        List<Personaje> restantes = new ArrayList<>();
        for (Personaje candidato : candidatos) {
            if (filtro.cumple(candidato)) {
                restantes.add(candidato);
            }
        }
        return new EspacioDeBusqueda(restantes);
    }

    /** Caso base D&C: tamanio 1 → el candidato restante es el personaje objetivo. */
    public int tamanio() {
        return candidatos.size();
    }

    public boolean esUnico() {
        return candidatos.size() == 1;
    }

    public Personaje unico() {
        if (!esUnico()) {
            throw new IllegalStateException("El espacio tiene " + candidatos.size() + " candidatos, no uno");
        }
        return candidatos.get(0);
    }

    public List<Personaje> getCandidatos() {
        return candidatos;
    }
}
