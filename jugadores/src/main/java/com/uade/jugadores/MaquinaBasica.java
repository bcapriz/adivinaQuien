package com.uade.jugadores;

import com.uade.dominio.*;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Primer Strategy concreto del port {@link Jugador} (SDD 5.3).
 *
 * GREEDY CIEGO (SDD 4.3): en cada turno elige la primera categoria de
 * {@link Categoria#ORDEN_FIJO} (genero -> calvicie -> lentes -> color de pelo)
 * que todavia no pregunto, sin simular cuanto reduce cada opcion el espacio de
 * busqueda. Es la heuristica debil que sirve de contraste contra
 * {@link MaquinaAsertiva} (que si implementa la funcion de seleccion real).
 *
 * El recorrido Divide & Conquer (caso base -> elegir filtro -> particionar ->
 * recursar) lo maneja {@link Partida}: esta clase solo aporta el "que filtro
 * elegir" en cada paso.
 */
public class MaquinaBasica implements Jugador {

    private final Random random = new Random();

    @Override
    public Personaje elegirPersonaje(RegistroDePersonajes registro, List<Personaje> yaElegidos) {
        List<Personaje> disponibles = registro.listar().stream()
                .filter(p -> !yaElegidos.contains(p))
                .toList();
        if (disponibles.isEmpty()) {
            throw new IllegalStateException("No hay personajes disponibles para elegir");
        }
        return disponibles.get(random.nextInt(disponibles.size()));
    }

    @Override
    public Accion decidirTurno(EstadoDePartidaVisible estado) {
        EspacioDeBusqueda espacio = estado.getEspacioDeBusqueda();

        // Caso base D&C: un solo candidato -> adivinanza directa (SDD 4.2).
        if (espacio.esUnico()) {
            return new Accion.Adivinanza(espacio.unico());
        }

        Categoria siguiente = primeraCategoriaSinUsar(estado.getHistorialFiltros());
        if (siguiente == null) {
            // Se agotaron las 4 categorias y todavia hay mas de un candidato
            // (personajes indistinguibles por sus atributos): se adivina a ciegas.
            return new Accion.Adivinanza(espacio.getCandidatos().get(0));
        }
        return new Accion.AplicarFiltro(construirFiltro(siguiente, espacio));
    }

    private Categoria primeraCategoriaSinUsar(List<Filtro> historialPropio) {
        Set<Categoria> usadas = historialPropio.stream()
                .map(Filtro::getCategoria)
                .collect(Collectors.toSet());
        for (Categoria categoria : Categoria.ORDEN_FIJO) {
            if (!usadas.contains(categoria)) {
                return categoria;
            }
        }
        return null;
    }

    /**
     * Heuristica ciega para el valor del filtro: pregunta por el valor que tiene
     * el primer candidato del espacio, sin comparar cuanto aisla cada
     * alternativa (eso es trabajo de {@link MaquinaAsertiva}).
     */
    private Filtro construirFiltro(Categoria categoria, EspacioDeBusqueda espacio) {
        Personaje referencia = espacio.getCandidatos().get(0);
        return switch (categoria) {
            case GENERO     -> new Filtro(categoria, referencia.getGenero());
            case CALVICIE   -> new Filtro(categoria, referencia.isCalvo());
            case LENTES     -> new Filtro(categoria, referencia.isUsaLentes());
            case COLOR_PELO -> new Filtro(categoria, referencia.getColorPelo());
        };
    }
}
