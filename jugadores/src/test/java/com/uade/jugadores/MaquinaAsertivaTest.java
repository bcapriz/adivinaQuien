package com.uade.jugadores;

import com.uade.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MaquinaAsertivaTest {

    //  ID  Nombre  Género      Calvo  Lentes  Pelo
    //   1  Ana     FEMENINO    false  true    NEGRO
    //   2  Bob     MASCULINO   true   false   COLORADO
    //   3  Carla   FEMENINO    false  false   AMARILLO
    //   4  Diego   MASCULINO   false  true    NEGRO
    //   5  Elena   FEMENINO    true   false   COLORADO

    private RegistroDePersonajes registro;
    private EspacioDeBusqueda espacioCompleto;

    @BeforeEach
    void setUp() {
        registro = new RegistroDePersonajes();
        registro.agregar("Ana",   Genero.FEMENINO,  false, true,  ColorPelo.NEGRO);
        registro.agregar("Bob",   Genero.MASCULINO, true,  false, ColorPelo.COLORADO);
        registro.agregar("Carla", Genero.FEMENINO,  false, false, ColorPelo.AMARILLO);
        registro.agregar("Diego", Genero.MASCULINO, false, true,  ColorPelo.NEGRO);
        registro.agregar("Elena", Genero.FEMENINO,  true,  false, ColorPelo.COLORADO);
        espacioCompleto = new EspacioDeBusqueda(registro.listar());
    }

    // --- función de selección: minimiza el peor caso de respuesta ---

    @Test
    void elFiltroElegidoMinimizaElPeorCasoDeRespuestaFrenteATodasLasAlternativas() {
        Filtro elegido = filtroDe(new MaquinaAsertiva().decidirTurno(EstadoFake.de(espacioCompleto)));

        assertEquals(minPeorCasoFactible(espacioCompleto, Set.of()), peorCaso(espacioCompleto, elegido));
    }

    @Test
    void ignoraUnaCategoriaQueNoSeparaNada() {
        // registro donde todos son MASCULINO -> preguntar por GENERO no reduce
        var soloHombres = new RegistroDePersonajes();
        soloHombres.agregar("H1", Genero.MASCULINO, true,  true,  ColorPelo.NEGRO);
        soloHombres.agregar("H2", Genero.MASCULINO, true,  false, ColorPelo.COLORADO);
        soloHombres.agregar("H3", Genero.MASCULINO, false, true,  ColorPelo.AMARILLO);
        soloHombres.agregar("H4", Genero.MASCULINO, false, false, ColorPelo.NEGRO);
        var espacio = new EspacioDeBusqueda(soloHombres.listar());

        Filtro elegido = filtroDe(new MaquinaAsertiva().decidirTurno(EstadoFake.de(espacio)));

        assertNotEquals(Categoria.GENERO, elegido.getCategoria());
    }

    @Test
    void noEligeUnaCategoriaQueYaUso() {
        List<Filtro> historialPropio = List.of(
                new Filtro(Categoria.GENERO, Genero.MASCULINO),
                new Filtro(Categoria.CALVICIE, Boolean.TRUE));

        Filtro elegido = filtroDe(new MaquinaAsertiva()
                .decidirTurno(EstadoFake.conHistorialPropio(espacioCompleto, historialPropio)));

        assertTrue(elegido.getCategoria() == Categoria.LENTES || elegido.getCategoria() == Categoria.COLOR_PELO);
    }

    @Test
    void conUnUnicoCandidatoAdivina() {
        EspacioDeBusqueda unico = espacioCompleto
                .aplicarFiltro(new Filtro(Categoria.GENERO, Genero.MASCULINO), true)
                .aplicarFiltro(new Filtro(Categoria.LENTES, Boolean.TRUE), true); // solo Diego

        Accion accion = new MaquinaAsertiva().decidirTurno(EstadoFake.de(unico));

        assertEquals("Diego", assertInstanceOf(Accion.Adivinanza.class, accion).personaje().getNombre());
    }

    // --- ventaja informativa de la máquina "informada" ---

    @Test
    void informadaDesempataHaciaCategoriasQueElRivalNoExploro() {
        List<Filtro> historialRival = List.of(new Filtro(Categoria.GENERO, Genero.MASCULINO));
        var estado = new EstadoFake(espacioCompleto, List.of(), historialRival);

        Filtro sinInformar = filtroDe(new MaquinaAsertiva(false).decidirTurno(estado));
        Filtro informada   = filtroDe(new MaquinaAsertiva(true).decidirTurno(estado));

        assertEquals(Categoria.GENERO, sinInformar.getCategoria());       // la ciega al historial rival
        assertNotEquals(Categoria.GENERO, informada.getCategoria());       // evita repetir el eje de M1
        assertEquals(peorCaso(espacioCompleto, sinInformar),
                     peorCaso(espacioCompleto, informada));                // sin resignar reducción
    }

    @Test
    void informadaIgnoraElHistorialRivalCuandoUnaCategoriaReduceEstrictamenteMejor() {
        // LENTES parte 2/2 (peor caso 2); COLOR_PELO parte 3/1 (peor caso 3).
        var reg = new RegistroDePersonajes();
        reg.agregar("P1", Genero.MASCULINO, false, true,  ColorPelo.NEGRO);
        reg.agregar("P2", Genero.MASCULINO, false, true,  ColorPelo.NEGRO);
        reg.agregar("P3", Genero.MASCULINO, false, false, ColorPelo.NEGRO);
        reg.agregar("P4", Genero.MASCULINO, false, false, ColorPelo.COLORADO);
        var espacio = new EspacioDeBusqueda(reg.listar());
        List<Filtro> historialRival = List.of(new Filtro(Categoria.LENTES, Boolean.TRUE));
        var estado = new EstadoFake(espacio, List.of(), historialRival);

        Filtro sinInformar = filtroDe(new MaquinaAsertiva(false).decidirTurno(estado));
        Filtro informada   = filtroDe(new MaquinaAsertiva(true).decidirTurno(estado));

        assertEquals(Categoria.LENTES, sinInformar.getCategoria());
        assertEquals(Categoria.LENTES, informada.getCategoria()); // no resigna la mejor reducción por evitar redundancia
    }

    // --- convergencia ---

    @Test
    void aislaAlSecretoDelRivalEnPocasPreguntas() {
        Jugador asertiva = new MaquinaAsertiva();
        Jugador pasivo = new Jugador() {
            @Override public Personaje elegirPersonaje(RegistroDePersonajes r, List<Personaje> ya) {
                return r.listar().stream().filter(p -> !ya.contains(p)).findFirst().orElseThrow();
            }
            @Override public Accion decidirTurno(EstadoDePartidaVisible e) {
                return new Accion.AplicarFiltro(new Filtro(Categoria.GENERO, Genero.MASCULINO)); // nunca adivina
            }
        };
        var partida = new Partida(asertiva, pasivo, registro);

        int turnosAsertiva = 0;
        while (!partida.estaTerminada() && turnosAsertiva < 12) {
            if (partida.jugarTurno().getJugador() == asertiva) {
                turnosAsertiva++;
            }
        }

        assertTrue(partida.estaTerminada());
        assertEquals(asertiva, partida.ganador());
        assertTrue(turnosAsertiva <= 4, "esperaba <= 4 turnos para 5 candidatos, fueron " + turnosAsertiva);
    }

    // --- helpers ---

    private static Filtro filtroDe(Accion accion) {
        return assertInstanceOf(Accion.AplicarFiltro.class, accion).filtro();
    }

    private static int peorCaso(EspacioDeBusqueda espacio, Filtro filtro) {
        return Math.max(
                espacio.aplicarFiltro(filtro, true).tamanio(),
                espacio.aplicarFiltro(filtro, false).tamanio());
    }

    private static int minPeorCasoFactible(EspacioDeBusqueda espacio, Set<Categoria> excluidas) {
        int min = Integer.MAX_VALUE;
        for (Categoria categoria : Categoria.values()) {
            if (excluidas.contains(categoria)) continue;
            for (Object valor : categoria.valoresPosibles()) {
                Filtro filtro = new Filtro(categoria, valor);
                int si = espacio.aplicarFiltro(filtro, true).tamanio();
                int no = espacio.aplicarFiltro(filtro, false).tamanio();
                if (si == 0 || no == 0) continue;
                min = Math.min(min, Math.max(si, no));
            }
        }
        return min;
    }
}
