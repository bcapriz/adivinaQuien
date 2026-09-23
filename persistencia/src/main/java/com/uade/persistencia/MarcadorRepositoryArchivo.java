package com.uade.persistencia;

import com.uade.dominio.MarcadorRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Persiste el marcador en un archivo {@code .properties} (pares
 * {@code usuario=victorias}). El mapa se lee entero a memoria al crear el repo y
 * se reescribe entero en cada {@link #registrarVictoria}. Para el volumen
 * esperado (pocos usuarios) no hace falta una base de datos.
 */
public class MarcadorRepositoryArchivo implements MarcadorRepository {

    private final Path archivo;
    private final Map<String, Integer> victorias;

    public MarcadorRepositoryArchivo(Path archivo) {
        this.archivo = archivo;
        this.victorias = leerDesdeArchivo();
    }

    @Override
    public void registrarVictoria(String usuario) {
        victorias.merge(usuario, 1, Integer::sum);
        escribirEnArchivo();
    }

    @Override
    public List<Map.Entry<String, Integer>> top() {
        List<Map.Entry<String, Integer>> entradas = new ArrayList<>();
        victorias.forEach((usuario, cantidad) -> entradas.add(Map.entry(usuario, cantidad)));
        entradas.sort((a, b) -> {
            int porVictorias = Integer.compare(b.getValue(), a.getValue());
            return porVictorias != 0 ? porVictorias : a.getKey().compareTo(b.getKey());
        });
        return entradas;
    }

    private Map<String, Integer> leerDesdeArchivo() {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        if (!Files.exists(archivo)) {
            return mapa;
        }
        Properties propiedades = new Properties();
        try (var entrada = Files.newInputStream(archivo)) {
            propiedades.load(entrada);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el marcador: " + archivo, e);
        }
        for (String usuario : propiedades.stringPropertyNames()) {
            mapa.put(usuario, Integer.parseInt(propiedades.getProperty(usuario).trim()));
        }
        return mapa;
    }

    private void escribirEnArchivo() {
        Properties propiedades = new Properties();
        victorias.forEach((usuario, cantidad) -> propiedades.setProperty(usuario, cantidad.toString()));
        try {
            if (archivo.getParent() != null) {
                Files.createDirectories(archivo.getParent());
            }
            try (var salida = Files.newOutputStream(archivo)) {
                propiedades.store(salida, "Marcador Adivina Quien (usuario=victorias)");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar el marcador: " + archivo, e);
        }
    }
}
