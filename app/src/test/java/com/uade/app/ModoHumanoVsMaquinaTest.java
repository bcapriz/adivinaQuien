package com.uade.app;

import com.uade.dominio.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModoHumanoVsMaquinaTest {

    private final RegistroDePersonajes registro = PersonajesDeEjemplo.cargar();
    private final MarcadorFake marcador = new MarcadorFake();

    /** Maquina de test: personaje secreto fijo y una jugada fija por turno. */
    private Jugador maquinaFija(Personaje secreto, Accion jugada) {
        return new Jugador() {
            @Override public Personaje elegirPersonaje(RegistroDePersonajes r, List<Personaje> ya) { return secreto; }
            @Override public Accion decidirTurno(EstadoDePartidaVisible estado) { return jugada; }
        };
    }

    @Test
    void elHumanoGanaAdivinandoElPersonajeDeLaMaquina() {
        Personaje secretoMaquina = registro.listar().get(6); // id 7
        Accion jugadaInocua = new Accion.AplicarFiltro(new Filtro(Categoria.GENERO, Genero.MASCULINO));
        var consola = new ConsolaFake(
                "Bruno", // nombre
                "1",     // elegir personaje propio (id 1)
                "2",     // opcion: adivinar
                "7");    // adivina id 7

        new ModoHumanoVsMaquina(consola, marcador, maquinaFija(secretoMaquina, jugadaInocua)).jugar(registro);

        assertTrue(consola.mostroAlgunaQueContiene("Ganaste!"));
        assertTrue(consola.mostroAlgunaQueContiene("ACERTO"));
        assertEquals(1, marcador.victorias.get("Bruno"));
    }

    @Test
    void laMaquinaGanaAdivinandoEnSuTurno() {
        Personaje secretoHumano = registro.listar().get(0); // id 1, el que elige el humano abajo
        Personaje secretoMaquina = registro.listar().get(6);
        Accion maquinaAdivina = new Accion.Adivinanza(secretoHumano);
        var consola = new ConsolaFake(
                "Bruno",           // nombre
                "1",               // elegir personaje propio (id 1)
                "1", "1", "1");    // turno 1: filtro -> genero -> masculino (no termina)

        new ModoHumanoVsMaquina(consola, marcador, maquinaFija(secretoMaquina, maquinaAdivina)).jugar(registro);

        assertTrue(consola.mostroAlgunaQueContiene("Gano la maquina."));
        assertEquals(1, marcador.victorias.get("Maquina Basica"));
        assertNull(marcador.victorias.get("Bruno"));
    }

    @Test
    void laNarracionMuestraLaRespuestaSiONoDelFiltro() {
        Personaje secretoMaquina = registro.listar().get(0); // id 1, MASCULINO
        Accion maquinaAdivina = new Accion.Adivinanza(registro.listar().get(1));
        var consola = new ConsolaFake("Bruno", "2", "1", "1", "1"); // nombre; elige id 2; filtro -> genero -> masculino

        new ModoHumanoVsMaquina(consola, marcador, maquinaFija(secretoMaquina, maquinaAdivina)).jugar(registro);

        // el secreto de la maquina (id 1) es MASCULINO -> la respuesta al filtro es SI
        assertTrue(consola.salidas.stream().anyMatch(s -> s.contains("filtro") && s.contains("SI")));
    }
}
