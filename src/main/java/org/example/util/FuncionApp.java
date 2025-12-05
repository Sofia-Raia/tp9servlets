package org.example.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FuncionApp {
    /**
     * Convierte una fecha de tipo LocalDate a String con formato dd/MM/yyyy.
     * Si la fecha es null, devuelve una cadena vacía.
     */
    public static String getFechaString(LocalDate fecha) {
        if (fecha == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha.format(formatter);
    }
}
