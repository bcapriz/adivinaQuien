package com.uade.dominio;

public sealed interface Accion permits Accion.Adivinanza, Accion.AplicarFiltro {

    record Adivinanza(Personaje personaje) implements Accion {}

    record AplicarFiltro(Filtro filtro) implements Accion {}
}
