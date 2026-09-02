package com.uade.persistencia;

import com.uade.dominio.MarcadorRepository;

import java.util.List;
import java.util.Map;

public class MarcadorRepositoryArchivo implements MarcadorRepository {

    @Override
    public void registrarVictoria(String usuario) {
        throw new UnsupportedOperationException("Sprint 6");
    }

    @Override
    public List<Map.Entry<String, Integer>> top() {
        throw new UnsupportedOperationException("Sprint 6");
    }
}
