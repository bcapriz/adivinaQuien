package com.uade.dominio;

import java.util.List;

public interface EstadoDePartidaVisible {
    EspacioDeBusqueda getEspacioDeBusqueda();
    List<Filtro> getHistorialFiltros();
    List<Filtro> getHistorialFiltrosRival();
}
