package com.uade.app;

import com.uade.dominio.MarcadorRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** {@link MarcadorRepository} en memoria para los tests de los modos. */
class MarcadorFake implements MarcadorRepository {

    final Map<String, Integer> victorias = new LinkedHashMap<>();

    @Override
    public void registrarVictoria(String usuario) {
        victorias.merge(usuario, 1, Integer::sum);
    }

    @Override
    public List<Map.Entry<String, Integer>> top() {
        return new ArrayList<>(victorias.entrySet());
    }
}
