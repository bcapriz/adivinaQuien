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
     *
     * Esta sobrecarga equivale a aplicarFiltro(filtro, true): conserva los
     * candidatos que cumplen el filtro. Es el caso que usa la simulacion
     * greedy de MaquinaAsertiva (SDD 4.3), donde se estima el subconjunto
     * resultante suponiendo respuesta afirmativa.
     */
    public EspacioDeBusqueda aplicarFiltro(Filtro filtro) {
        return aplicarFiltro(filtro, true);
    }

    /**
     * DIVIDE & CONQUISTA — paso de particion segun la respuesta real.
     *
     * Un filtro es una pregunta sobre el personaje secreto del rival. La
     * respuesta (si/no) la calcula Partida puertas adentro del dueno del
     * secreto (SDD constitucion punto 3, supuesto 2.3.1). Este metodo parte
     * el espacio quedandose con los candidatos consistentes con esa respuesta:
     *   - respuesta true  -> candidatos que cumplen el filtro
     *   - respuesta false -> candidatos que NO lo cumplen (subconjunto complementario)
     *
     * Sin esta particion por respuesta, un filtro cuya respuesta real es "no"
     * descartaria al propio personaje secreto del conjunto de candidatos y el
     * jugador nunca podria acertar por reduccion.
     *
     * Complejidad: Theta(n) sobre el subconjunto vigente (SDD 4.2).
     */
    public EspacioDeBusqueda aplicarFiltro(Filtro filtro, boolean respuesta) {
        List<Personaje> restantes = new ArrayList<>();
        for (Personaje candidato : candidatos) {
            if (filtro.cumple(candidato) == respuesta) {
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
