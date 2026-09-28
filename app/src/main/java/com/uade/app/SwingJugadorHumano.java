package com.uade.app;

import com.uade.dominio.*;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SwingJugadorHumano implements Jugador {

    private final Component parent;
    private final PanelDeJugada panelDeJugada;
    private final BlockingQueue<Accion> jugadas = new ArrayBlockingQueue<>(1);

    public SwingJugadorHumano(Component parent, PanelDeJugada panelDeJugada) {
        this.parent = parent;
        this.panelDeJugada = panelDeJugada;
        panelDeJugada.alJugar(jugadas::offer);
    }

    @Override
    public Personaje elegirPersonaje(RegistroDePersonajes registro, List<Personaje> yaElegidos) {
        List<Personaje> disponibles = OrdenadorDePersonajes.ordenarPorNombre(registro.listar().stream()
                .filter(p -> !yaElegidos.contains(p))
                .toList());
        Personaje[] opciones = disponibles.toArray(new Personaje[0]);
        Personaje[] elegido = new Personaje[1];
        ejecutarEnEdt(() -> elegido[0] = (Personaje) JOptionPane.showInputDialog(
                parent, "Elegi tu personaje secreto", "Adivina Quien",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]));
        return elegido[0];
    }

    @Override
    public Accion decidirTurno(EstadoDePartidaVisible estado) {
        ejecutarEnEdt(() -> panelDeJugada.habilitarPara(estado));
        try {
            return jugadas.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    private void ejecutarEnEdt(Runnable accion) {
        try {
            SwingUtilities.invokeAndWait(accion);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
