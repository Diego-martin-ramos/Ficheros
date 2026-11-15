package es.ficheros.deportes;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Cada objeto representa una sesión de entrenamiento. */
public class Entrenamiento {
    private String fecha; // ISO yyyy-MM-dd; representación JSON sencilla.
    private String actividad;
    private int minutos;

    private Entrenamiento() {
    }

    public Entrenamiento(String fecha, String actividad, int minutos) {
        this.fecha = fecha;
        this.actividad = actividad;
        this.minutos = minutos;
        validar();
    }

    public void validar() {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
        try {
            LocalDate.parse(fecha);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Fecha incorrecta. Usa yyyy-MM-dd.", ex);
        }
        if (actividad == null || actividad.isBlank()) {
            throw new IllegalArgumentException("La actividad es obligatoria.");
        }
        if (minutos <= 0) {
            throw new IllegalArgumentException("La duración debe ser positiva.");
        }
    }

    public String getFecha() { return fecha; }
    public String getActividad() { return actividad; }
    public int getMinutos() { return minutos; }

    @Override
    public String toString() { return fecha + " | " + actividad + " | " + minutos + " min"; }
}
