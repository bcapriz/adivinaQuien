package com.uade.app;

import com.uade.dominio.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModoMaquinaVsMaquinaTest {

    private final RegistroDePersonajes registro = PersonajesDeEjemplo.cargar();
    private final MarcadorFake marcador = new MarcadorFake();

    private Jugador conSecreto(Personaje secreto, Accion... jugadasPorTurno) {
        return new Jugador() {
            private int turno = 0;

            @Override public Personaje elegirPersonaje(RegistroDePersonajes r, List<Personaje> ya) { return secreto; }

            @Override public Accion decidirTurno(EstadoDePartidaVisible estado) {
                Accion jugada = jugadasPorTurno[Math.min(turno, jugadasPorTurno.length - 1)];
                turno++;
                return jugada;
            }
        };
    }

    @Test
    void relataCadaFiltroConRespuestaTamanioYElGanador() {
        Personaje alejandro = registro.listar().get(0); // id 1
        Personaje gonzalo = registro.listar().get(6);   // id 7, MASCULINO

        Jugador m1 = conSecreto(alejandro,
                new Accion.AplicarFiltro(new Filtro(Categoria.GENERO, Genero.MASCULINO)), // turno 1
                new Accion.Adivinanza(gonzalo));                                          // turno 2: acierta
        Jugador m2 = conSecreto(gonzalo,
                new Accion.AplicarFiltro(new Filtro(Categoria.LENTES, Boolean.FALSE)));   // nunca adivina

        var consola = new ConsolaFake();
        new ModoMaquinaVsMaquina(consola, marcador, m1, m2).jugar(registro);

        assertTrue(consola.salidas.stream().anyMatch(
                s -> s.startsWith("M1") && s.contains("GENERO=MASCULINO") && s.contains("SI") && s.contains("13 candidatos")));
        assertTrue(consola.salidas.stream().anyMatch(s -> s.startsWith("M2") && s.contains("filtro")));
        assertTrue(consola.mostroAlgunaQueContiene("adivina Gonzalo"));
        assertTrue(consola.mostroAlgunaQueContiene("Gana M1"));
        assertEquals(1, marcador.victorias.get("Maquina Basica"));
    }

    @Test
    void seDetieneYAvisaSiLasMaquinasNoLoganAdivinar() {
        // ambas máquinas sólo aplican un filtro inocuo: nunca hay ganador
        Personaje a = registro.listar().get(0);
        Personaje b = registro.listar().get(1);
        Accion filtroInocuo = new Accion.AplicarFiltro(new Filtro(Categoria.GENERO, Genero.MASCULINO));

        var consola = new ConsolaFake();
        new ModoMaquinaVsMaquina(consola, marcador, conSecreto(a, filtroInocuo), conSecreto(b, filtroInocuo)).jugar(registro);

        assertTrue(consola.mostroAlgunaQueContiene("Sin ganador"));
        assertTrue(marcador.victorias.isEmpty());
    }
}
