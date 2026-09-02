package com.uade.jugadores;

import com.uade.dominio.*;

import java.util.List;
import java.util.Random;

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
        throw new UnsupportedOperationException("Sprint 3");
    }
}
