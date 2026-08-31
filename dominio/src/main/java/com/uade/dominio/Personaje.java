package com.uade.dominio;

public class Personaje {

    private final int id;
    private final String nombre;
    private final Genero genero;
    private final boolean calvo;
    private final boolean usaLentes;
    private final ColorPelo colorPelo;

    Personaje(int id, String nombre, Genero genero, boolean calvo, boolean usaLentes, ColorPelo colorPelo) {
        this.id = id;
        this.nombre = nombre;
        this.genero = genero;
        this.calvo = calvo;
        this.usaLentes = usaLentes;
        this.colorPelo = colorPelo;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public Genero getGenero() { return genero; }
    public boolean isCalvo() { return calvo; }
    public boolean isUsaLentes() { return usaLentes; }
    public ColorPelo getColorPelo() { return colorPelo; }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre;
    }
}
