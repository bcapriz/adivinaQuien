package com.uade.app;

import com.uade.dominio.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PanelDeJugada extends JPanel {

    private final JTextArea transcripcion = new JTextArea();
    final JPanel opciones = new JPanel(new GridLayout(0, 1, 4, 4));

    private Consumer<Accion> alJugar = accion -> { };
    private EstadoDePartidaVisible estadoActual;
    private Set<Categoria> categoriasYaPreguntadas = Set.of();

    public PanelDeJugada() {
        super(new BorderLayout(0, 8));
        transcripcion.setEditable(false);
        transcripcion.setLineWrap(true);
        transcripcion.setWrapStyleWord(true);

        JScrollPane scrollOpciones = new JScrollPane(opciones);
        scrollOpciones.setPreferredSize(new Dimension(220, 220));

        add(new JScrollPane(transcripcion), BorderLayout.CENTER);
        add(scrollOpciones, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(240, 0));
    }

    public void alJugar(Consumer<Accion> callback) {
        this.alJugar = callback;
    }

    public void habilitarPara(EstadoDePartidaVisible estado) {
        this.estadoActual = estado;
        this.categoriasYaPreguntadas = estado.getHistorialFiltros().stream()
                .map(Filtro::getCategoria)
                .collect(Collectors.toSet());
        agregarMensaje("Sistema", "Es tu turno. ¿Que hacemos?");
        mostrarOpcionesDeTurno();
    }

    private void mostrarOpcionesDeTurno() {
        mostrarOpciones(
                opcion("Preguntar algo", this::preguntarQue),
                opcion("Arriesgar un nombre", this::preguntarPersonaje),
                opcion("Rendirme", this::confirmarRendirse));
    }

    private void confirmarRendirse() {
        agregarMensaje("Vos", "Me quiero rendir");
        agregarMensaje("Sistema", "¿Seguro? No hay vuelta atras.");
        mostrarOpciones(
                opcion("Si, me rindo", () -> jugar(new Accion.Rendirse())),
                opcion("No, sigo jugando", () -> {
                    agregarMensaje("Vos", "No, mejor sigo jugando");
                    mostrarOpcionesDeTurno();
                }));
    }

    // Cada boton ya es la pregunta en si misma. La respuesta real (Si/No, o el
    // resultado de la adivinanza) la agrega despues agregarLinea(), que viene
    // de evaluar la Accion contra el secreto del rival — nunca se muestra acá
    // una respuesta antes de tiempo. Las categorias ya preguntadas se
    // deshabilitan: volver a preguntarlas no da informacion nueva.
    private void preguntarQue() {
        agregarMensaje("Vos", "Quiero preguntar algo");
        agregarMensaje("Sistema", "¿Que le preguntamos?");
        mostrarOpciones(
                opcionCategoria("¿Es hombre o mujer?", Categoria.GENERO, this::preguntarGenero),
                opcionCategoria("¿Es calvo/a?", Categoria.CALVICIE, () -> jugar(
                        new Accion.AplicarFiltro(new Filtro(Categoria.CALVICIE, Boolean.TRUE)))),
                opcionCategoria("¿Usa lentes?", Categoria.LENTES, () -> jugar(
                        new Accion.AplicarFiltro(new Filtro(Categoria.LENTES, Boolean.TRUE)))),
                opcionCategoria("¿De que color tiene el pelo?", Categoria.COLOR_PELO, this::preguntarColor));
    }

    private void preguntarGenero() {
        agregarMensaje("Vos", "¿Es hombre o mujer?");
        agregarMensaje("Sistema", "¿Cual de los dos te parece?");
        mostrarOpciones(
                opcion("Hombre", () -> jugar(new Accion.AplicarFiltro(new Filtro(Categoria.GENERO, Genero.MASCULINO)))),
                opcion("Mujer", () -> jugar(new Accion.AplicarFiltro(new Filtro(Categoria.GENERO, Genero.FEMENINO)))));
    }

    private void preguntarColor() {
        agregarMensaje("Vos", "¿De que color tiene el pelo?");
        agregarMensaje("Sistema", "¿Que color decis?");
        List<?> valores = Categoria.COLOR_PELO.valoresPosibles();
        JButton[] botones = new JButton[valores.size()];
        for (int i = 0; i < valores.size(); i++) {
            Object valor = valores.get(i);
            botones[i] = opcion(etiqueta(valor), () -> jugar(new Accion.AplicarFiltro(new Filtro(Categoria.COLOR_PELO, valor))));
        }
        mostrarOpciones(botones);
    }

    private void preguntarPersonaje() {
        agregarMensaje("Vos", "Quiero arriesgar un nombre");
        agregarMensaje("Sistema", "¿Quien creés que es?");
        List<Personaje> candidatos = OrdenadorDePersonajes.ordenarPorNombre(
                estadoActual.getEspacioDeBusqueda().getCandidatos());
        JButton[] botones = new JButton[candidatos.size()];
        for (int i = 0; i < candidatos.size(); i++) {
            Personaje personaje = candidatos.get(i);
            botones[i] = opcion(personaje.getNombre(), () -> jugar(new Accion.Adivinanza(personaje)));
        }
        mostrarOpciones(botones);
    }

    /** Envia la jugada. La linea "pregunta -> respuesta" la agrega agregarLinea() cuando llega el resultado real. */
    private void jugar(Accion accion) {
        mostrarOpciones();
        alJugar.accept(accion);
    }

    private JButton opcion(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> accion.run());
        return boton;
    }

    private JButton opcionCategoria(String texto, Categoria categoria, Runnable accion) {
        JButton boton = opcion(texto, accion);
        boton.setEnabled(!categoriasYaPreguntadas.contains(categoria));
        if (!boton.isEnabled()) {
            boton.setToolTipText("Ya preguntaste sobre esto");
        }
        return boton;
    }

    private void mostrarOpciones(JButton... botones) {
        opciones.removeAll();
        for (JButton boton : botones) {
            opciones.add(boton);
        }
        opciones.revalidate();
        opciones.repaint();
    }

    /** Punto de entrada para narración externa (PartidaObserverSwing): filtros y adivinanzas de ambos jugadores. */
    public void agregarLinea(String texto) {
        transcripcion.append(texto + "\n");
        agregarSeparador();
        transcripcion.setCaretPosition(transcripcion.getDocument().getLength());
    }

    private void agregarMensaje(String quien, String texto) {
        transcripcion.append(quien + ": " + texto + "\n");
        transcripcion.setCaretPosition(transcripcion.getDocument().getLength());
    }

    private void agregarSeparador() {
        transcripcion.append("- - - - - - - - - -\n");
        transcripcion.setCaretPosition(transcripcion.getDocument().getLength());
    }

    private static String etiqueta(Object valor) {
        if (valor instanceof ColorPelo colorPelo) {
            return switch (colorPelo) {
                case NEGRO -> "Negro";
                case COLORADO -> "Colorado";
                case AMARILLO -> "Rubio";
            };
        }
        return valor.toString();
    }
}
