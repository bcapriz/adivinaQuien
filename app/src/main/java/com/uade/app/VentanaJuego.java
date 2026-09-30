package com.uade.app;

import com.uade.dominio.*;
import com.uade.jugadores.MaquinaAsertiva;
import com.uade.jugadores.MaquinaBasica;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class VentanaJuego extends JFrame {

    private static final String NOMBRE_HUMANO = "Vos";
    private static final String NOMBRE_MAQUINA_BASICA = "Maquina Basica";
    private static final String NOMBRE_MAQUINA_ASERTIVA = "Maquina Asertiva";
    private static final int PAUSA_ENTRE_TURNOS_MS = 1500;

    private final RegistroDePersonajes registro;
    private final MarcadorRepository marcador;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel raiz = new JPanel(cardLayout);
    private JComponent pantallaJuegoActual;

    public VentanaJuego(RegistroDePersonajes registro, MarcadorRepository marcador) {
        super("Adivina Quien");
        this.registro = registro;
        this.marcador = marcador;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        raiz.add(pantallaInicio(), "inicio");
        add(raiz);
        setSize(900, 760);
        setLocationRelativeTo(null);
    }

    private JPanel pantallaInicio() {
        JPanel panel = new JPanel(new BorderLayout(10, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));

        JLabel titulo = new JLabel("Adivina Quien", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 34f));

        JLabel bienvenida = new JLabel("Bienvenido! ¿Con que modo queres jugar?", SwingConstants.CENTER);
        bienvenida.setFont(bienvenida.getFont().deriveFont(Font.PLAIN, 15f));

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 8));
        encabezado.add(titulo);
        encabezado.add(bienvenida);

        JButton humanoVsMaquina = new JButton("Humano vs Maquina");
        JButton espectador = new JButton("Espectador: Maquina vs Maquina");
        humanoVsMaquina.setFont(humanoVsMaquina.getFont().deriveFont(15f));
        espectador.setFont(espectador.getFont().deriveFont(15f));
        humanoVsMaquina.addActionListener(e -> iniciarHumanoVsMaquina());
        espectador.addActionListener(e -> iniciarEspectador());

        JPanel botones = new JPanel(new GridLayout(0, 1, 10, 10));
        botones.add(humanoVsMaquina);
        botones.add(espectador);

        panel.add(encabezado, BorderLayout.NORTH);
        panel.add(botones, BorderLayout.CENTER);
        return panel;
    }

    private void iniciarHumanoVsMaquina() {
        Jugador maquina = elegirMaquina();
        if (maquina == null) {
            return; // cancelo el dialogo, me quedo en la pantalla de inicio
        }
        String nombreMaquina = maquina instanceof MaquinaAsertiva ? NOMBRE_MAQUINA_ASERTIVA : NOMBRE_MAQUINA_BASICA;

        TableroPersonajesPanel tablero = new TableroPersonajesPanel(registro.listar());
        PanelDeJugada panelDeJugada = new PanelDeJugada();
        MarcadorSuperior encabezado = new MarcadorSuperior(marcador, NOMBRE_HUMANO, nombreMaquina);
        SwingJugadorHumano humano = new SwingJugadorHumano(this, panelDeJugada);

        JPanel juego = new JPanel(new BorderLayout());
        JScrollPane scrollTablero = new JScrollPane(tablero);
        scrollTablero.getVerticalScrollBar().setUnitIncrement(16);
        juego.add(encabezado, BorderLayout.NORTH);
        juego.add(scrollTablero, BorderLayout.CENTER);
        juego.add(panelDeJugada, BorderLayout.EAST);
        mostrarPantalla(juego);

        Map<Jugador, String> nombres = Map.of(humano, NOMBRE_HUMANO, maquina, nombreMaquina);
        ObservadorDePartida observer = new PartidaObserverSwing(
                panelDeJugada::agregarLinea, Map.of(humano, tablero), nombres);

        new Thread(() -> {
            Partida partida = new Partida(humano, maquina, registro, List.of(observer));
            while (!partida.estaTerminada()) {
                partida.jugarTurno();
            }
            boolean ganoHumano = partida.ganador() == humano;
            marcador.registrarVictoria(ganoHumano ? NOMBRE_HUMANO : nombreMaquina);
            SwingUtilities.invokeLater(() -> {
                encabezado.actualizar();
                preguntarSiJuegaDeNuevo(ganoHumano);
            });
        }, "hilo-partida").start();
    }

    /** HU-9: el jugador elige contra cual de las dos maquinas juega esa partida. */
    private Jugador elegirMaquina() {
        Object[] opciones = {NOMBRE_MAQUINA_BASICA, NOMBRE_MAQUINA_ASERTIVA};
        int eleccion = JOptionPane.showOptionDialog(this,
                "¿Contra que maquina queres jugar?", "Elegi tu rival",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);
        if (eleccion == JOptionPane.CLOSED_OPTION) {
            return null;
        }
        return eleccion == 1 ? new MaquinaAsertiva() : new MaquinaBasica();
    }

    private void preguntarSiJuegaDeNuevo(boolean ganoHumano) {
        String mensaje = ganoHumano ? "Ganaste! ¿Jugamos otra?" : "Gano la maquina. ¿Jugamos otra?";
        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Fin de la partida", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            iniciarHumanoVsMaquina();
        } else {
            cardLayout.show(raiz, "inicio");
        }
    }

    private void iniciarEspectador() {
        String nombreM1 = NOMBRE_MAQUINA_BASICA;
        String nombreM2 = NOMBRE_MAQUINA_ASERTIVA;
        Jugador m1 = new MaquinaBasica();
        Jugador m2 = new MaquinaAsertiva(true);

        TableroPersonajesPanel tableroM1 = new TableroPersonajesPanel(registro.listar());
        TableroPersonajesPanel tableroM2 = new TableroPersonajesPanel(registro.listar());
        MarcadorSuperior encabezado = new MarcadorSuperior(marcador, nombreM1, nombreM2);

        JPanel tableros = new JPanel(new GridLayout(1, 2, 12, 0));
        tableros.add(panelConTitulo(nombreM1, tableroM1));
        tableros.add(panelConTitulo(nombreM2, tableroM2));

        JTextArea log = new JTextArea();
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        JScrollPane scrollLog = new JScrollPane(log);
        scrollLog.setPreferredSize(new Dimension(0, 180));

        JPanel juego = new JPanel(new BorderLayout(0, 8));
        juego.add(encabezado, BorderLayout.NORTH);
        juego.add(tableros, BorderLayout.CENTER);
        juego.add(scrollLog, BorderLayout.SOUTH);
        mostrarPantalla(juego);

        Map<Jugador, String> nombres = Map.of(m1, nombreM1, m2, nombreM2);
        Map<Jugador, TableroPersonajesPanel> tablerosPorJugador = Map.of(m1, tableroM1, m2, tableroM2);
        ObservadorDePartida observer = new PartidaObserverSwing(
                linea -> log.append(linea + "\n"), tablerosPorJugador, nombres);

        new Thread(() -> {
            Partida partida = new Partida(m1, m2, registro, List.of(observer));
            int turnos = 0;
            while (!partida.estaTerminada() && turnos++ < 500) {
                partida.jugarTurno();
                pausar();
            }
            if (partida.estaTerminada()) {
                marcador.registrarVictoria(partida.ganador() == m1 ? nombreM1 : nombreM2);
                SwingUtilities.invokeLater(() -> {
                    encabezado.actualizar();
                    mostrarMarcador();
                    cardLayout.show(raiz, "inicio");
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Sin ganador tras 500 turnos (personajes indistinguibles).",
                            "Fin de la partida", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(raiz, "inicio");
                });
            }
        }, "hilo-partida").start();
    }

    private void pausar() {
        try {
            Thread.sleep(PAUSA_ENTRE_TURNOS_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private JPanel panelConTitulo(String titulo, TableroPersonajesPanel tablero) {
        JLabel etiqueta = new JLabel(titulo, SwingConstants.CENTER);
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 14f));

        JScrollPane scroll = new JScrollPane(tablero);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void mostrarPantalla(JComponent pantalla) {
        if (pantallaJuegoActual != null) {
            raiz.remove(pantallaJuegoActual);
        }
        pantallaJuegoActual = pantalla;
        raiz.add(pantalla, "juego");
        cardLayout.show(raiz, "juego");
        revalidate();
        repaint();
    }

    private void mostrarMarcador() {
        JOptionPane.showMessageDialog(this, new PanelMarcador(marcador), "Marcador", JOptionPane.PLAIN_MESSAGE);
    }
}
