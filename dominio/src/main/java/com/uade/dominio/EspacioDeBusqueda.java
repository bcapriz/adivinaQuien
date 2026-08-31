package com.uade.dominio;

import java.util.ArrayList;
import java.util.List;

public class EspacioDeBusqueda {

    private final List<Personaje> candidatos;

    public EspacioDeBusqueda(List<Personaje> candidatos) {
        this.candidatos = List.copyOf(candidatos);
    }

    public EspacioDeBusqueda aplicarFiltro(Filtro filtro) {
        List<Personaje> restantes = new ArrayList<>();
        for (Personaje candidato : candidatos) {
            if (filtro.cumple(candidato)) {
                restantes.add(candidato);
            }
        }
        return new EspacioDeBusqueda(restantes);
    }

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
