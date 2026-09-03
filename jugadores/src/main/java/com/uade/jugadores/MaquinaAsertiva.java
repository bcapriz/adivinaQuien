package com.uade.jugadores;

import com.uade.dominio.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Función de selección greedy real. Para cada filtro factible (categoría no
 * usada por mí) simula las dos ramas de respuesta y se queda con el que deja el
 * subconjunto restante más chico en el peor caso de respuesta, es decir el que
 * más reduce en el peor caso. Decide localmente, sin backtracking ni garantía de
 * óptimo global.
 *
 * Con {@code informada = true} además lee el historial de filtros del rival
 * ({@link EstadoDePartidaVisible#getHistorialFiltrosRival()}) y, ante empate en
 * la reducción del peor caso, desempata hacia una categoría que el rival todavía
 * no exploró — evita redundancia sin resignar reducción.
 */
public class MaquinaAsertiva extends MotorDeBusquedaDeMaquina {

    private final boolean informada;

    public MaquinaAsertiva() {
        this(false);
    }

    public MaquinaAsertiva(boolean informada) {
        this.informada = informada;
    }

    @Override
    protected Filtro elegirFiltro(EstadoDePartidaVisible estado) {
        EspacioDeBusqueda espacio = estado.getEspacioDeBusqueda();
        List<Categoria> disponibles = categoriasSinUsar(estado.getHistorialFiltros());
        Set<Categoria> usadasPorElRival = informada
                ? estado.getHistorialFiltrosRival().stream().map(Filtro::getCategoria).collect(Collectors.toSet())
                : Set.of();

        Filtro mejor = null;
        int mejorPeorCaso = Integer.MAX_VALUE;
        int mejorRedundancia = Integer.MAX_VALUE;

        for (Categoria categoria : disponibles) {
            int redundancia = usadasPorElRival.contains(categoria) ? 1 : 0;
            for (Object valor : categoria.valoresPosibles()) {
                Filtro candidato = new Filtro(categoria, valor);
                int siRespondeSi = espacio.aplicarFiltro(candidato, true).tamanio();
                int siRespondeNo = espacio.aplicarFiltro(candidato, false).tamanio();
                if (siRespondeSi == 0 || siRespondeNo == 0) {
                    continue; // no separa nada: su respuesta ya está determinada
                }
                int peorCaso = Math.max(siRespondeSi, siRespondeNo);
                if (peorCaso < mejorPeorCaso
                        || (peorCaso == mejorPeorCaso && redundancia < mejorRedundancia)) {
                    mejor = candidato;
                    mejorPeorCaso = peorCaso;
                    mejorRedundancia = redundancia;
                }
            }
        }
        return mejor; // null si ninguna categoría disponible separa el espacio
    }
}
