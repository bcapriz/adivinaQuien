package com.uade.dominio;

public class Filtro {

    private final Categoria categoria;
    private final Object valorEsperado;

    public Filtro(Categoria categoria, Object valorEsperado) {
        this.categoria = categoria;
        this.valorEsperado = valorEsperado;
    }

    public Categoria getCategoria() { return categoria; }
    public Object getValorEsperado() { return valorEsperado; }

    public boolean cumple(Personaje personaje) {
        return switch (categoria) {
            case GENERO     -> personaje.getGenero()    == valorEsperado;
            case CALVICIE   -> personaje.isCalvo()      == (boolean) valorEsperado;
            case LENTES     -> personaje.isUsaLentes()  == (boolean) valorEsperado;
            case COLOR_PELO -> personaje.getColorPelo() == valorEsperado;
        };
    }

    @Override
    public String toString() {
        return categoria + "=" + valorEsperado;
    }
}
