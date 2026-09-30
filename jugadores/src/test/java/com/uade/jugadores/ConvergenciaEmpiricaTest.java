package com.uade.jugadores;

import com.uade.dominio.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Empírico (10, sección 4.5): mide cuántas preguntas necesita cada heurística
 * para aislar, en solitario, al personaje secreto de un rival "pasivo" que
 * nunca adivina (mismo patrón que
 * {@code MaquinaAsertivaTest.aislaAlSecretoDelRivalEnPocasPreguntas}, acá
 * generalizado a N corridas con el roster completo de 23 personajes y
 * comparado contra la cota inferior teórica ⌈log₂(23)⌉ = 5). Usar un rival
 * pasivo en vez de enfrentar Asertiva contra Básica es a propósito: si las
 * dos jugaran una contra la otra, el orden de turnos (una siempre arranca)
 * sesga el conteo crudo de turnos y no aísla la convergencia real de cada
 * heurística. Igual que el benchmark de ordenamiento (4.7.1), mide e imprime
 * — no falla por un umbral de promedio, que sería flaky; el assert solo
 * verifica que todas las corridas convergen de verdad.
 */
class ConvergenciaEmpiricaTest {

    private static final int CORRIDAS = 200;
    private static final int TOPE_DE_TURNOS = 200;

    @Test
    void asertivaConvergeMasCercaDeLaCotaTeoricaQueLaBasica() {
        double promedioAsertiva = promedioTurnosHastaConvergencia(MaquinaAsertiva::new);
        double promedioBasica = promedioTurnosHastaConvergencia(MaquinaBasica::new);
        double cotaTeorica = Math.ceil(Math.log(23) / Math.log(2));

        System.out.println("--- Convergencia empírica (n=23, " + CORRIDAS + " corridas por heurística, rival pasivo) ---");
        System.out.printf("Cota teórica ⌈log₂(23)⌉:      %.0f preguntas%n", cotaTeorica);
        System.out.printf("Promedio turnos Asertiva:      %.2f%n", promedioAsertiva);
        System.out.printf("Promedio turnos Básica:        %.2f%n", promedioBasica);

        assertTrue(promedioAsertiva <= promedioBasica,
                "se esperaba que la Asertiva (greedy real) convergiera en promedio en igual o menor "
                        + "cantidad de turnos que la Básica (greedy ciego)");
    }

    private interface FabricaDeMaquina {
        Jugador nueva();
    }

    private static double promedioTurnosHastaConvergencia(FabricaDeMaquina fabrica) {
        long turnosTotal = 0;
        for (int corrida = 0; corrida < CORRIDAS; corrida++) {
            RegistroDePersonajes registro = registroDe23Personajes();
            Jugador sujeto = fabrica.nueva();
            Jugador pasivo = jugadorPasivo();
            Partida partida = new Partida(sujeto, pasivo, registro);

            int turnosSujeto = 0;
            int turnos = 0;
            while (!partida.estaTerminada() && turnos++ < TOPE_DE_TURNOS) {
                if (partida.jugarTurno().getJugador() == sujeto) {
                    turnosSujeto++;
                }
            }

            assertTrue(partida.estaTerminada(),
                    "la corrida " + corrida + " no convergió en " + TOPE_DE_TURNOS + " turnos");
            turnosTotal += turnosSujeto;
        }
        return turnosTotal / (double) CORRIDAS;
    }

    /** Nunca adivina: aplica siempre el mismo filtro, así el rival converge por sus propios méritos. */
    private static Jugador jugadorPasivo() {
        return new Jugador() {
            private final Random random = new Random();

            @Override
            public Personaje elegirPersonaje(RegistroDePersonajes registro, List<Personaje> yaElegidos) {
                List<Personaje> disponibles = registro.listar().stream()
                        .filter(p -> !yaElegidos.contains(p)).toList();
                return disponibles.get(random.nextInt(disponibles.size()));
            }

            @Override
            public Accion decidirTurno(EstadoDePartidaVisible estado) {
                return new Accion.AplicarFiltro(new Filtro(Categoria.GENERO, Genero.MASCULINO));
            }
        };
    }

    /**
     * Roster con las 4 características asignadas al azar por personaje (no en un
     * patrón regular tipo "género alterna, calvicie cada 2, lentes cada 4..."):
     * un patrón regular queda accidentalmente pre-ordenado para calzar exacto
     * con ORDEN_FIJO de la Básica (género es el bit menos significativo, igual
     * que el primer eje que prueba la Básica) y le da una ventaja artificial que
     * no tendría con datos reales — el mismo motivo por el que el roster real
     * del juego ({@code PersonajesDeEjemplo}) no sigue ningún patrón así.
     */
    private static RegistroDePersonajes registroDe23Personajes() {
        Random random = new Random();
        RegistroDePersonajes registro = new RegistroDePersonajes();
        ColorPelo[] colores = ColorPelo.values();
        for (int i = 0; i < 23; i++) {
            Genero genero = random.nextBoolean() ? Genero.MASCULINO : Genero.FEMENINO;
            boolean calvo = random.nextBoolean();
            boolean usaLentes = random.nextBoolean();
            ColorPelo colorPelo = colores[random.nextInt(colores.length)];
            registro.agregar("P" + i, genero, calvo, usaLentes, colorPelo);
        }
        return registro;
    }
}
