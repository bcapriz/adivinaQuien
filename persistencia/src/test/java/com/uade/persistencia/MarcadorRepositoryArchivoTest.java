package com.uade.persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MarcadorRepositoryArchivoTest {

    @TempDir
    Path directorio;
    private Path archivo;

    @BeforeEach
    void setUp() {
        archivo = directorio.resolve("marcador.properties");
    }

    @Test
    void arrancaVacioSiNoExisteElArchivo() {
        assertTrue(new MarcadorRepositoryArchivo(archivo).top().isEmpty());
    }

    @Test
    void registrarVictoriaIncrementaElContador() {
        var repo = new MarcadorRepositoryArchivo(archivo);
        repo.registrarVictoria("Ana");
        repo.registrarVictoria("Ana");
        repo.registrarVictoria("Bob");

        var top = repo.top();
        assertEquals("Ana", top.get(0).getKey());
        assertEquals(2, top.get(0).getValue());
        assertEquals("Bob", top.get(1).getKey());
        assertEquals(1, top.get(1).getValue());
    }

    @Test
    void persisteEntreInstancias() {
        new MarcadorRepositoryArchivo(archivo).registrarVictoria("Ana");

        // "reinicio del proceso": otra instancia sobre el mismo archivo
        var repo = new MarcadorRepositoryArchivo(archivo);
        repo.registrarVictoria("Ana");

        assertEquals(2, repo.top().get(0).getValue());
    }

    @Test
    void topOrdenaPorVictoriasDescendenteYLuegoPorNombre() {
        var repo = new MarcadorRepositoryArchivo(archivo);
        repo.registrarVictoria("Ana");
        repo.registrarVictoria("Bob");
        repo.registrarVictoria("Bob");
        repo.registrarVictoria("Bob");
        repo.registrarVictoria("Carla");
        repo.registrarVictoria("Carla");

        assertEquals(List.of("Bob", "Carla", "Ana"),
                repo.top().stream().map(Map.Entry::getKey).toList());
    }

    @Test
    void soportaNombresConEspacios() {
        new MarcadorRepositoryArchivo(archivo).registrarVictoria("Maria Jose");

        assertEquals("Maria Jose", new MarcadorRepositoryArchivo(archivo).top().get(0).getKey());
    }
}
