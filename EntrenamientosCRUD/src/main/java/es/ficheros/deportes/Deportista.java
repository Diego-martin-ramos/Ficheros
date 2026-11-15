package es.ficheros.deportes;

/** Objeto asociado a un plan. Los datos de ejemplo son ficticios. */
public class Deportista {
    private String nombre;
    private int edad;

    private Deportista() { // Constructor para la deserialización con Gson.
    }

    public Deportista(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
        validar();
    }

    public void validar() {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del deportista es obligatorio.");
        }
        if (edad < 1 || edad > 120) {
            throw new IllegalArgumentException("La edad debe estar entre 1 y 120.");
        }
    }

    public String getNombre() { return nombre; }
    public int getEdad() { return edad; }

    @Override
    public String toString() { return nombre + " (" + edad + " años)"; }
}
