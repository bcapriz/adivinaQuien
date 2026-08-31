package com.uade.dominio;

import java.util.List;
import java.util.Map;

public interface MarcadorRepository {
    void registrarVictoria(String usuario);
    List<Map.Entry<String, Integer>> top();
}
