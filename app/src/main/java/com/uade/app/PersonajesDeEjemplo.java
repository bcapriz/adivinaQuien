package com.uade.app;

import com.uade.dominio.ColorPelo;
import com.uade.dominio.Genero;
import com.uade.dominio.RegistroDePersonajes;

/**
 * Roster fijo de 23 personajes para jugar por consola. Se cargan agrupados por
 * genero (masculinos y luego femeninas); el ID autoincremental lo asigna
 * {@link RegistroDePersonajes} en el alta.
 */
public final class PersonajesDeEjemplo {

    private PersonajesDeEjemplo() {
    }

    public static RegistroDePersonajes cargar() {
        RegistroDePersonajes r = new RegistroDePersonajes();

        r.agregar("Alejandro", Genero.MASCULINO, true,  false, ColorPelo.NEGRO);
        r.agregar("Bruno",     Genero.MASCULINO, false, true,  ColorPelo.COLORADO);
        r.agregar("Carlos",    Genero.MASCULINO, true,  false, ColorPelo.AMARILLO);
        r.agregar("Diego",     Genero.MASCULINO, false, false, ColorPelo.NEGRO);
        r.agregar("Ernesto",   Genero.MASCULINO, true,  true,  ColorPelo.COLORADO);
        r.agregar("Federico",  Genero.MASCULINO, false, false, ColorPelo.AMARILLO);
        r.agregar("Gonzalo",   Genero.MASCULINO, true,  false, ColorPelo.NEGRO);
        r.agregar("Hernan",    Genero.MASCULINO, false, true,  ColorPelo.COLORADO);
        r.agregar("Ignacio",   Genero.MASCULINO, false, false, ColorPelo.NEGRO);
        r.agregar("Javier",    Genero.MASCULINO, true,  false, ColorPelo.AMARILLO);
        r.agregar("Kevin",     Genero.MASCULINO, false, true,  ColorPelo.NEGRO);
        r.agregar("Lucas",     Genero.MASCULINO, true,  false, ColorPelo.COLORADO);
        r.agregar("Marcos",    Genero.MASCULINO, false, false, ColorPelo.AMARILLO);

        r.agregar("Ana",       Genero.FEMENINO,  false, true,  ColorPelo.NEGRO);
        r.agregar("Beatriz",   Genero.FEMENINO,  false, false, ColorPelo.COLORADO);
        r.agregar("Claudia",   Genero.FEMENINO,  true,  true,  ColorPelo.AMARILLO);
        r.agregar("Diana",     Genero.FEMENINO,  false, false, ColorPelo.NEGRO);
        r.agregar("Elena",     Genero.FEMENINO,  false, true,  ColorPelo.COLORADO);
        r.agregar("Florencia", Genero.FEMENINO,  false, false, ColorPelo.NEGRO);
        r.agregar("Gabriela",  Genero.FEMENINO,  true,  false, ColorPelo.AMARILLO);
        r.agregar("Hernanda",  Genero.FEMENINO,  false, true,  ColorPelo.COLORADO);
        r.agregar("Irene",     Genero.FEMENINO,  false, false, ColorPelo.NEGRO);
        r.agregar("Julia",     Genero.FEMENINO,  false, true,  ColorPelo.AMARILLO);

        return r;
    }
}
