package com.uade.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegistroDePersonajes {

    private final List<Personaje> personajes = new ArrayList<>();
    private int proximoId = 1;

    public Personaje agregar(String nombre, Genero genero, boolean calvo, boolean usaLentes, ColorPelo colorPelo) {
        Personaje p = new Personaje(proximoId++, nombre, genero, calvo, usaLentes, colorPelo);
        personajes.add(p);
        return p;
    }

    public List<Personaje> listar() {
        return Collections.unmodifiableList(personajes);
    }

    public int tamanio() {
        return personajes.size();
    }
}
