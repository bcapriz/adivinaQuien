package com.uade.dominio;

import java.util.ArrayList;
import java.util.List;

public class EspacioDeBusqueda {

    private final List<Personaje> candidatos;

    public EspacioDeBusqueda(List<Personaje> candidatos) {
        this.candidatos = List.copyOf(candidatos);
    }

    /**
     * Divide &amp; conquer — paso de partición.
     *
     * En vez de dividir un vector ordenado por la mitad, dividimos el conjunto de
     * candidatos según una categoría (género, calvicie, lentes, color de pelo).
     * El subconjunto que no cumple el filtro queda descartado; el que cumple es
     * el nuevo problema, de menor tamaño.
     *
     * Complejidad: Θ(n) sobre el subconjunto vigente (recorrido lineal, sin
     * estructura de orden previa que aprovechar).
     *
     * Esta sobrecarga equivale a {@code aplicarFiltro(filtro, true)}: conserva los
     * candidatos que cumplen el filtro. La usa la simulación greedy de
     * {@code MaquinaAsertiva} para estimar el subconjunto suponiendo respuesta "sí".
     */
    public EspacioDeBusqueda aplicarFiltro(Filtro filtro) {
        return aplicarFiltro(filtro, true);
    }

    /**
     * Divide &amp; conquer — paso de partición según la respuesta real.
     *
     * Un filtro es una pregunta sobre el personaje secreto del rival; la respuesta
     * (sí/no) la calcula {@link Partida} puertas adentro del dueño del secreto.
     * Este método parte el espacio quedándose con los candidatos consistentes con
     * esa respuesta:
     *   - respuesta true  -> candidatos que cumplen el filtro
     *   - respuesta false -> candidatos que NO lo cumplen (subconjunto complementario)
     *
     * Sin esta partición por respuesta, un filtro cuya respuesta real es "no"
     * descartaría al propio personaje secreto del conjunto de candidatos y el
     * jugador nunca podría acertar por reducción.
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

    public int tamanio() {
        return candidatos.size();
    }

    /** Caso base de la recursión: con un único candidato, ese es el personaje objetivo. */
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
