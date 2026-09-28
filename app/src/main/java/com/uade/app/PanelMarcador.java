package com.uade.app;

import com.uade.dominio.MarcadorRepository;

import javax.swing.*;
import java.awt.*;

public class PanelMarcador extends JPanel {

    public PanelMarcador(MarcadorRepository marcador) {
        super(new BorderLayout());
        String[] columnas = {"Usuario", "Victorias"};
        Object[][] filas = marcador.top().stream()
                .map(entrada -> new Object[]{entrada.getKey(), entrada.getValue()})
                .toArray(Object[][]::new);
        JTable tabla = new JTable(filas, columnas);
        tabla.setEnabled(false);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }
}
