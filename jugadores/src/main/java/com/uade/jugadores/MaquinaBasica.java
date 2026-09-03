package com.uade.jugadores;

import com.uade.dominio.*;

import java.util.List;

/**
 * Greedy ciego: recorre {@link Categoria#ORDEN_FIJO} (género -> calvicie ->
 * lentes -> color de pelo) y usa la primera categoría que todavía no preguntó,
 * sin simular cuánto reduce cada opción el espacio de búsqueda. Es la heurística
 * débil que sirve de contraste contra {@link MaquinaAsertiva}.
 */
public class MaquinaBasica extends MotorDeBusquedaDeMaquina {

    @Override
    protected Filtro elegirFiltro(EstadoDePartidaVisible estado) {
        List<Categoria> disponibles = categoriasSinUsar(estado.getHistorialFiltros());
        if (disponibles.isEmpty()) {
            return null; // el motor adivina a ciegas
        }
        Categoria categoria = disponibles.get(0); // primera libre del orden fijo
        Personaje referencia = estado.getEspacioDeBusqueda().getCandidatos().get(0);
        return construirFiltroCiego(categoria, referencia);
    }

    /**
     * Heurística ciega para el valor: pregunta por el valor que tiene el primer
     * candidato del espacio, sin comparar cuánto aísla cada alternativa.
     */
    private static Filtro construirFiltroCiego(Categoria categoria, Personaje referencia) {
        return switch (categoria) {
            case GENERO     -> new Filtro(categoria, referencia.getGenero());
            case CALVICIE   -> new Filtro(categoria, referencia.isCalvo());
            case LENTES     -> new Filtro(categoria, referencia.isUsaLentes());
            case COLOR_PELO -> new Filtro(categoria, referencia.getColorPelo());
        };
    }
}
