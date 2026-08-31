package com.uade.app;

import com.uade.dominio.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Adivina Quién — Demo Sprint 1: partición del espacio de búsqueda ===\n");

        RegistroDePersonajes registro = cargarPersonajes();

        System.out.println("--- Personajes cargados (" + registro.tamanio() + ") ---");
        for (Personaje p : registro.listar()) {
            System.out.printf("  [%2d] %-12s  %s  calvo=%-5b  lentes=%-5b  pelo=%s%n",
                    p.getId(), p.getNombre(), p.getGenero(),
                    p.isCalvo(), p.isUsaLentes(), p.getColorPelo());
        }

        System.out.println("\n--- Simulación: la máquina busca al personaje secreto ---");

        // El personaje secreto del rival es "Florencia" (id=19).
        // La máquina no lo sabe — solo puede aplicar filtros y observar el resultado.
        Personaje secreto = registro.listar().get(18); // índice 18 → id 19
        System.out.println("Personaje secreto (solo el rival lo sabe): " + secreto.getNombre() + "\n");

        EspacioDeBusqueda espacio = new EspacioDeBusqueda(registro.listar());
        aplicarFiltro(espacio, new Filtro(Categoria.GENERO, Genero.FEMENINO),    "¿Es mujer?",          true);
        espacio = espacio.aplicarFiltro(new Filtro(Categoria.GENERO, Genero.FEMENINO));

        aplicarFiltro(espacio, new Filtro(Categoria.CALVICIE, Boolean.FALSE),    "¿No es calva?",       true);
        espacio = espacio.aplicarFiltro(new Filtro(Categoria.CALVICIE, Boolean.FALSE));

        aplicarFiltro(espacio, new Filtro(Categoria.COLOR_PELO, ColorPelo.NEGRO), "¿Tiene pelo negro?",  true);
        espacio = espacio.aplicarFiltro(new Filtro(Categoria.COLOR_PELO, ColorPelo.NEGRO));

        aplicarFiltro(espacio, new Filtro(Categoria.LENTES, Boolean.FALSE),      "¿No usa lentes?",     true);
        espacio = espacio.aplicarFiltro(new Filtro(Categoria.LENTES, Boolean.FALSE));

        System.out.println("\n--- Resultado ---");
        if (espacio.esUnico()) {
            System.out.println("¡La máquina lo identificó! Es: " + espacio.unico().getNombre());
        } else {
            System.out.println("Candidatos restantes: " + espacio.tamanio());
            espacio.getCandidatos().forEach(p -> System.out.println("  - " + p.getNombre()));
        }
    }

    private static void aplicarFiltro(EspacioDeBusqueda espacio, Filtro filtro, String pregunta, boolean respuesta) {
        EspacioDeBusqueda resultado = espacio.aplicarFiltro(filtro);
        System.out.printf("Pregunta: %-28s → %s  |  candidatos: %d → %d%n",
                pregunta, respuesta ? "SÍ" : "NO", espacio.tamanio(), resultado.tamanio());
    }

    private static RegistroDePersonajes cargarPersonajes() {
        var r = new RegistroDePersonajes();
        // Masculinos primero (orden por género como pide la consigna)
        r.agregar("Alejandro",  Genero.MASCULINO, true,  false, ColorPelo.NEGRO);
        r.agregar("Bruno",      Genero.MASCULINO, false, true,  ColorPelo.COLORADO);
        r.agregar("Carlos",     Genero.MASCULINO, true,  false, ColorPelo.AMARILLO);
        r.agregar("Diego",      Genero.MASCULINO, false, false, ColorPelo.NEGRO);
        r.agregar("Ernesto",    Genero.MASCULINO, true,  true,  ColorPelo.COLORADO);
        r.agregar("Federico",   Genero.MASCULINO, false, false, ColorPelo.AMARILLO);
        r.agregar("Gonzalo",    Genero.MASCULINO, true,  false, ColorPelo.NEGRO);
        r.agregar("Hernán",     Genero.MASCULINO, false, true,  ColorPelo.COLORADO);
        r.agregar("Ignacio",    Genero.MASCULINO, false, false, ColorPelo.NEGRO);
        r.agregar("Javier",     Genero.MASCULINO, true,  false, ColorPelo.AMARILLO);
        r.agregar("Kevin",      Genero.MASCULINO, false, true,  ColorPelo.NEGRO);
        r.agregar("Lucas",      Genero.MASCULINO, true,  false, ColorPelo.COLORADO);
        r.agregar("Marcos",     Genero.MASCULINO, false, false, ColorPelo.AMARILLO);
        // Femeninas
        r.agregar("Ana",        Genero.FEMENINO,  false, true,  ColorPelo.NEGRO);
        r.agregar("Beatriz",    Genero.FEMENINO,  false, false, ColorPelo.COLORADO);
        r.agregar("Claudia",    Genero.FEMENINO,  true,  true,  ColorPelo.AMARILLO);
        r.agregar("Diana",      Genero.FEMENINO,  false, false, ColorPelo.NEGRO);
        r.agregar("Elena",      Genero.FEMENINO,  false, true,  ColorPelo.COLORADO);
        r.agregar("Florencia",  Genero.FEMENINO,  false, false, ColorPelo.NEGRO);
        r.agregar("Gabriela",   Genero.FEMENINO,  true,  false, ColorPelo.AMARILLO);
        r.agregar("Hernanda",   Genero.FEMENINO,  false, true,  ColorPelo.COLORADO);
        r.agregar("Irene",      Genero.FEMENINO,  false, false, ColorPelo.NEGRO);
        r.agregar("Julia",      Genero.FEMENINO,  false, true,  ColorPelo.AMARILLO);
        return r;
    }
}
