package com.uade.app;

import com.uade.dominio.MarcadorRepository;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/** Encabezado compacto "A X - Y B" para la partida en curso. */
public class MarcadorSuperior extends JPanel {

    private final MarcadorRepository marcador;
    private final String nombreA;
    private final String nombreB;
    private final JLabel etiqueta = new JLabel();

    public MarcadorSuperior(MarcadorRepository marcador, String nombreA, String nombreB) {
        super(new FlowLayout(FlowLayout.CENTER));
        this.marcador = marcador;
        this.nombreA = nombreA;
        this.nombreB = nombreB;
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 16f));
        add(etiqueta);
        actualizar();
    }

    public void actualizar() {
        etiqueta.setText(nombreA + " " + victoriasDe(nombreA)
                + "   -   " + victoriasDe(nombreB) + " " + nombreB);
    }

    private int victoriasDe(String nombre) {
        return marcador.top().stream()
                .filter(entrada -> entrada.getKey().equals(nombre))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(0);
    }
}
