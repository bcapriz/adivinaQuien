package com.uade.app;

import com.uade.dominio.*;

import java.util.List;

/**
 * Adapter (SDD 5.3) del port {@link Jugador}: traduce la interaccion por consola
 * en una {@link Accion} del dominio. No contiene reglas de juego — solo lee
 * opciones del usuario y construye objetos de dominio. Toda la E/S pasa por
 * {@link Consola}, asi las maquinas nunca dependen de metodos de consola (ISP).
 */
public class ConsolaJugadorHumano implements Jugador {

    private final Consola consola;

    public ConsolaJugadorHumano(Consola consola) {
        this.consola = consola;
    }

    @Override
    public Personaje elegirPersonaje(RegistroDePersonajes registro, List<Personaje> yaElegidos) {
        List<Personaje> disponibles = registro.listar().stream()
                .filter(p -> !yaElegidos.contains(p))
                .toList();

        consola.mostrar("\n== Elegi tu personaje secreto ==");
        for (Personaje p : disponibles) {
            consola.mostrar("  " + descripcion(p));
        }
        while (true) {
            int id = leerEntero("Numero de tu personaje: ");
            for (Personaje p : disponibles) {
                if (p.getId() == id) {
                    return p;
                }
            }
            consola.mostrar("Ese numero no esta disponible. Proba de nuevo.");
        }
    }

    @Override
    public Accion decidirTurno(EstadoDePartidaVisible estado) {
        EspacioDeBusqueda espacio = estado.getEspacioDeBusqueda();

        consola.mostrar("\n-- Tu turno --");
        consola.mostrar("Candidatos para el personaje del rival (" + espacio.tamanio() + "):");
        for (Personaje p : espacio.getCandidatos()) {
            consola.mostrar("  " + descripcion(p));
        }
        consola.mostrar("1) Aplicar un filtro");
        consola.mostrar("2) Lanzar una adivinanza");

        if (leerEntero("Opcion: ") == 2) {
            return leerAdivinanza(espacio);
        }
        return leerFiltro();
    }

    private Accion leerAdivinanza(EspacioDeBusqueda espacio) {
        while (true) {
            int id = leerEntero("Numero del personaje que adivinas: ");
            for (Personaje p : espacio.getCandidatos()) {
                if (p.getId() == id) {
                    return new Accion.Adivinanza(p);
                }
            }
            consola.mostrar("Ese personaje no esta entre los candidatos.");
        }
    }

    private Accion leerFiltro() {
        consola.mostrar("Categoria:  1) Genero  2) Calvicie  3) Lentes  4) Color de pelo");
        Categoria categoria = switch (leerEntero("Categoria: ")) {
            case 2 -> Categoria.CALVICIE;
            case 3 -> Categoria.LENTES;
            case 4 -> Categoria.COLOR_PELO;
            default -> Categoria.GENERO;
        };
        return new Accion.AplicarFiltro(new Filtro(categoria, leerValor(categoria)));
    }

    private Object leerValor(Categoria categoria) {
        return switch (categoria) {
            case GENERO -> {
                consola.mostrar("1) Masculino  2) Femenino");
                yield leerEntero("Valor: ") == 2 ? Genero.FEMENINO : Genero.MASCULINO;
            }
            case CALVICIE -> {
                consola.mostrar("1) Si  2) No");
                yield leerEntero("Es calvo? ") == 1;
            }
            case LENTES -> {
                consola.mostrar("1) Si  2) No");
                yield leerEntero("Usa lentes? ") == 1;
            }
            case COLOR_PELO -> {
                consola.mostrar("1) Colorado  2) Negro  3) Amarillo");
                yield switch (leerEntero("Valor: ")) {
                    case 1 -> ColorPelo.COLORADO;
                    case 3 -> ColorPelo.AMARILLO;
                    default -> ColorPelo.NEGRO;
                };
            }
        };
    }

    private int leerEntero(String prompt) {
        while (true) {
            consola.mostrar(prompt);
            try {
                return Integer.parseInt(consola.leerLinea().trim());
            } catch (NumberFormatException e) {
                consola.mostrar("Ingresa un numero.");
            }
        }
    }

    private static String descripcion(Personaje p) {
        return "[" + p.getId() + "] " + p.getNombre()
                + " - " + p.getGenero()
                + (p.isCalvo() ? ", calvo" : "")
                + (p.isUsaLentes() ? ", con lentes" : "")
                + ", pelo " + p.getColorPelo();
    }
}
