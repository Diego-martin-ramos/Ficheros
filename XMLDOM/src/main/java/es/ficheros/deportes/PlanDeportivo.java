package es.ficheros.deportes;

import java.util.ArrayList;
import java.util.List;

/** Clase que reúne todos los tipos de atributos solicitados en el ejercicio. */
public class PlanDeportivo {
    // STATIC: pertenecen a la clase y se comparten entre todos los objetos.
    public static final String NOMBRE_APLICACION = "Mi agenda deportiva";
    private static int planesCreadosEnSesion = 0;

    // Atributos propios de cada objeto.
    private int id;
    private String nombre;
    private String deporte;

    // Referencia a un objeto de otra clase.
    private Deportista deportista;

    // Referencia a una lista de objetos de otra clase.
    private List<Entrenamiento> entrenamientos = new ArrayList<>();

    // TRANSIENT: información temporal que Gson no guarda ni recupera del JSON.
    private transient String notaPrivada;
    private transient long ultimaConsultaMillis;

    private PlanDeportivo() { // Gson no incrementa el contador al leer del disco.
    }

    public PlanDeportivo(int id, String nombre, String deporte, Deportista deportista) {
        this.id = id;
        this.nombre = nombre;
        this.deporte = deporte;
        this.deportista = deportista;
        validar();
        planesCreadosEnSesion++;
    }

    public void validar() {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del plan es obligatorio.");
        }
        if (deporte == null || deporte.isBlank()) {
            throw new IllegalArgumentException("El deporte es obligatorio.");
        }
        if (deportista == null) throw new IllegalArgumentException("Falta el deportista.");
        deportista.validar();
        if (entrenamientos == null) throw new IllegalArgumentException("Falta la lista de entrenamientos.");
        for (Entrenamiento entrenamiento : entrenamientos) {
            if (entrenamiento == null) throw new IllegalArgumentException("Entrenamiento nulo.");
            entrenamiento.validar();
        }
    }

    public void actualizar(String nombre, String deporte) {
        if (nombre == null || nombre.isBlank() || deporte == null || deporte.isBlank()) {
            throw new IllegalArgumentException("El nombre y el deporte son obligatorios.");
        }
        this.nombre = nombre;
        this.deporte = deporte;
    }

    /** Append lógico: añade una sesión al final sin eliminar las anteriores. */
    public void agregarEntrenamiento(Entrenamiento entrenamiento) {
        if (entrenamiento == null) throw new IllegalArgumentException("Entrenamiento nulo.");
        entrenamiento.validar();
        entrenamientos.add(entrenamiento);
    }

    public void registrarConsulta() { ultimaConsultaMillis = System.currentTimeMillis(); }
    public void setNotaPrivada(String notaPrivada) { this.notaPrivada = notaPrivada; }
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDeporte() { return deporte; }
    public Deportista getDeportista() { return deportista; }
    public List<Entrenamiento> getEntrenamientos() { return List.copyOf(entrenamientos); }
    public String getNotaPrivada() { return notaPrivada; }
    public long getUltimaConsultaMillis() { return ultimaConsultaMillis; }
    public static int getPlanesCreadosEnSesion() { return planesCreadosEnSesion; }

    @Override
    public String toString() {
        return "Plan " + id + " | " + nombre + " | " + deporte + " | "
                + deportista + " | sesiones: " + entrenamientos.size();
    }
}
