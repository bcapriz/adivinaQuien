package com.uade.jugadores;

import com.uade.dominio.*;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Recorrido divide &amp; conquer que comparten todas las máquinas.
 *
 * El esqueleto de {@link #decidirTurno} es fijo:
 *   caso base (espacio único) -> adivinanza directa
 *   si no -> elegir filtro (greedy) -> aplicar filtro (lo hace Partida) -> recursar
 *
 * Lo único que varía entre heurísticas es {@link #elegirFiltro}: orden fijo
 * ciego en {@link MaquinaBasica}, función de selección por peor caso en
 * {@link MaquinaAsertiva}. La elección del personaje propio es azar uniforme
 * para todas y también vive acá.
 */
public abstract class MotorDeBusquedaDeMaquina implements Jugador {

    private final Random random = new Random();

    @Override
    public final Personaje elegirPersonaje(RegistroDePersonajes registro, List<Personaje> yaElegidos) {
        List<Personaje> disponibles = registro.listar().stream()
                .filter(p -> !yaElegidos.contains(p))
                .toList();
        if (disponibles.isEmpty()) {
            throw new IllegalStateException("No hay personajes disponibles para elegir");
        }
        return disponibles.get(random.nextInt(disponibles.size()));
    }

    @Override
    public final Accion decidirTurno(EstadoDePartidaVisible estado) {
        EspacioDeBusqueda espacio = estado.getEspacioDeBusqueda();

        // Caso base: un solo candidato -> adivinanza directa.
        if (espacio.esUnico()) {
            return new Accion.Adivinanza(espacio.unico());
        }

        Filtro filtro = elegirFiltro(estado);
        if (filtro == null) {
            // Ninguna categoría disponible separa el espacio (personajes
            // indistinguibles o categorías agotadas): se adivina a ciegas.
            return new Accion.Adivinanza(espacio.getCandidatos().get(0));
        }
        return new Accion.AplicarFiltro(filtro);
    }

    /**
     * Única pieza que varía entre heurísticas. Devuelve el filtro a aplicar este
     * turno, o {@code null} si no hay ninguno útil disponible.
     */
    protected abstract Filtro elegirFiltro(EstadoDePartidaVisible estado);

    /** Helper compartido: categorías que el propio jugador todavía no usó, en {@link Categoria#ORDEN_FIJO}. */
    protected List<Categoria> categoriasSinUsar(List<Filtro> historialPropio) {
        Set<Categoria> usadas = historialPropio.stream()
                .map(Filtro::getCategoria)
                .collect(Collectors.toSet());
        return Categoria.ORDEN_FIJO.stream()
                .filter(categoria -> !usadas.contains(categoria))
                .toList();
    }
}
