package com.uade.dominio;

import java.util.List;

public interface Jugador {
    Personaje elegirPersonaje(RegistroDePersonajes registro, List<Personaje> yaElegidos);
    Accion decidirTurno(EstadoDePartidaVisible estado);
}
