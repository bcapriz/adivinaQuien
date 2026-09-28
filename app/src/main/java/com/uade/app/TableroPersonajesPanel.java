package com.uade.app;

import com.uade.dominio.OrdenadorDePersonajes;
import com.uade.dominio.Personaje;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TableroPersonajesPanel extends JPanel {

    private static final int TAMANIO_ICONO = 80;

    private final Map<Personaje, JLabel> etiquetas = new LinkedHashMap<>();
    private final Map<Personaje, Icon> iconosNormales = new LinkedHashMap<>();
    private final Map<Personaje, Icon> iconosAtenuados = new LinkedHashMap<>();

    public TableroPersonajesPanel(List<Personaje> personajes) {
        super(new GridLayout(0, 4, 6, 6));
        for (Personaje personaje : OrdenadorDePersonajes.ordenarPorNombre(personajes)) {
            BufferedImage imagen = cargarImagen(personaje);
            Icon normal = aIcono(imagen);
            Icon atenuado = aIcono(aGrises(imagen));
            iconosNormales.put(personaje, normal);
            iconosAtenuados.put(personaje, atenuado);

            JLabel etiqueta = new JLabel(personaje.getNombre(), normal, SwingConstants.CENTER);
            etiqueta.setHorizontalTextPosition(SwingConstants.CENTER);
            etiqueta.setVerticalTextPosition(SwingConstants.BOTTOM);
            etiqueta.setVerticalAlignment(SwingConstants.TOP);
            etiqueta.setPreferredSize(new Dimension(TAMANIO_ICONO + 24, TAMANIO_ICONO + 34));
            etiquetas.put(personaje, etiqueta);
            add(etiqueta);
        }
    }

    public void actualizar(List<Personaje> candidatos) {
        Set<Personaje> vigentes = new HashSet<>(candidatos);
        etiquetas.forEach((personaje, etiqueta) -> etiqueta.setIcon(
                vigentes.contains(personaje) ? iconosNormales.get(personaje) : iconosAtenuados.get(personaje)));
    }

    private static Icon aIcono(BufferedImage imagen) {
        if (imagen == null) {
            return null;
        }
        return new ImageIcon(imagen.getScaledInstance(TAMANIO_ICONO, TAMANIO_ICONO, Image.SCALE_SMOOTH));
    }

    private static BufferedImage aGrises(BufferedImage imagen) {
        if (imagen == null) {
            return null;
        }
        BufferedImage gris = new BufferedImage(imagen.getWidth(), imagen.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null).filter(imagen, gris);
        return gris;
    }

    private static BufferedImage cargarImagen(Personaje personaje) {
        String recurso = "/personajes/" + normalizar(personaje.getNombre()) + ".jpeg";
        try (InputStream entrada = TableroPersonajesPanel.class.getResourceAsStream(recurso)) {
            return entrada == null ? null : ImageIO.read(entrada);
        } catch (IOException e) {
            return null;
        }
    }

    private static String normalizar(String nombre) {
        return Normalizer.normalize(nombre, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase();
    }
}
