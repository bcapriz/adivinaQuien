package com.uade.dominio;

import java.util.Arrays;
import java.util.List;

public enum Categoria {
    GENERO, CALVICIE, LENTES, COLOR_PELO;

    public static final List<Categoria> ORDEN_FIJO = List.of(GENERO, CALVICIE, LENTES, COLOR_PELO);

    public List<?> valoresPosibles() {
        return switch (this) {
            case GENERO -> Arrays.asList(Genero.values());
            case CALVICIE, LENTES -> List.of(Boolean.TRUE, Boolean.FALSE);
            case COLOR_PELO -> Arrays.asList(ColorPelo.values());
        };
    }
}
