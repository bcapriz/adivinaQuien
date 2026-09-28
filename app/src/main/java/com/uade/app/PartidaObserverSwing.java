package com.uade.app;

import com.uade.dominio.*;

import javax.swing.*;
import java.util.Map;
import java.util.function.Consumer;

public class PartidaObserverSwing implements ObservadorDePartida {

    private final Consumer<String> narrador;
    private final Map<Jugador, TableroPersonajesPanel> tableros; // por jugador; un jugador sin tablero no se refleja
    private final Map<Jugador, String> nombres;

    public PartidaObserverSwing(Consumer<String> narrador, Map<Jugador, TableroPersonajesPanel> tableros,
                                Map<Jugador, String> nombres) {
        this.narrador = narrador;
        this.tableros = tableros;
        this.nombres = nombres;
    }

    @Override
    public void onTurnoJugado(ResultadoTurno resultado) {
        String linea = construirLinea(resultado);
        TableroPersonajesPanel tablero = tableros.get(resultado.getJugador());
        SwingUtilities.invokeLater(() -> {
            narrador.accept(linea);
            if (tablero != null) {
                tablero.actualizar(resultado.getCandidatos());
            }
        });
    }

    @Override
    public void onPartidaTerminada(Jugador ganador) {
        String quien = nombres.get(ganador);
        String mensaje = "Vos".equals(quien) ? "¡Ganaste la partida!" : quien + " se llevo la partida.";
        SwingUtilities.invokeLater(() -> narrador.accept(mensaje));
    }

    private String construirLinea(ResultadoTurno resultado) {
        String quien = nombres.get(resultado.getJugador());
        boolean esVos = "Vos".equals(quien);
        Accion accion = resultado.getAccion();

        if (accion instanceof Accion.AplicarFiltro f) {
            String pregunta = preguntaNatural(f.filtro());
            String respuesta = Boolean.TRUE.equals(resultado.getRespuestaFiltro()) ? "Si" : "No";
            String verbo = esVos ? "preguntaste" : "pregunto";
            return quien + " " + verbo + ": " + pregunta + " -> " + respuesta
                    + " (quedan " + resultado.getCandidatosRestantes() + " candidatos)";
        }

        if (accion instanceof Accion.Rendirse) {
            return quien + " " + (esVos ? "te rendiste." : "se rindio.");
        }

        Accion.Adivinanza adivinanza = (Accion.Adivinanza) accion;
        String verbo = esVos ? "arriesgaste el nombre de" : "arriesgo el nombre de";
        String desenlace = resultado.fueAdivinanzaAcertada()
                ? (esVos ? "y le diste!" : "y le dio!")
                : (esVos ? "pero no era." : "pero no era.");
        return quien + " " + verbo + " " + adivinanza.personaje().getNombre() + "... " + desenlace;
    }

    private static String preguntaNatural(Filtro filtro) {
        Object valor = filtro.getValorEsperado();
        return switch (filtro.getCategoria()) {
            case GENERO -> valor == Genero.MASCULINO ? "¿Es hombre?" : "¿Es mujer?";
            case CALVICIE -> Boolean.TRUE.equals(valor) ? "¿Es calvo/a?" : "¿Tiene pelo?";
            case LENTES -> Boolean.TRUE.equals(valor) ? "¿Usa lentes?" : "¿No usa lentes?";
            case COLOR_PELO -> switch ((ColorPelo) valor) {
                case NEGRO -> "¿Tiene el pelo negro?";
                case COLORADO -> "¿Tiene el pelo colorado?";
                case AMARILLO -> "¿Tiene el pelo rubio?";
            };
        };
    }
}
