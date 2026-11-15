package es.ficheros.deportes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.Strictness;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/** CRUD de una colección persistente. Aplicación local de un único usuario. */
public class RepositorioPlanes {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting().setStrictness(Strictness.STRICT).create();
    private static final Type TIPO_LISTA = new TypeToken<List<PlanDeportivo>>() { }.getType();
    private final Path archivo;

    public RepositorioPlanes(Path archivo) { this.archivo = archivo.toAbsolutePath(); }
    public Path getArchivo() { return archivo; }

    // READ: recupera objetos reales del JSON en cada operación.
    public List<PlanDeportivo> listar() throws IOException {
        if (!Files.exists(archivo)) return new ArrayList<>();
        try {
            List<PlanDeportivo> planes = GSON.fromJson(
                    Files.readString(archivo, StandardCharsets.UTF_8), TIPO_LISTA);
            validarColeccion(planes);
            return planes;
        } catch (JsonParseException | IllegalArgumentException ex) {
            throw new IOException("JSON inválido en " + archivo + ": " + ex.getMessage(), ex);
        }
    }

    public PlanDeportivo buscar(int id) throws IOException {
        PlanDeportivo plan = localizar(listar(), id);
        plan.registrarConsulta();
        return plan;
    }

    // CREATE / APPEND de un registro: conserva los registros existentes.
    public void crear(PlanDeportivo nuevo) throws IOException {
        if (nuevo == null) throw new IllegalArgumentException("Plan nulo.");
        nuevo.validar();
        List<PlanDeportivo> planes = listar();
        if (planes.stream().anyMatch(p -> p.getId() == nuevo.getId())) {
            throw new IllegalArgumentException("Ya existe el ID " + nuevo.getId());
        }
        planes.add(nuevo);
        guardar(planes);
    }

    // UPDATE: modifica los atributos de un registro identificado por su ID.
    public void actualizar(int id, String nombre, String deporte) throws IOException {
        List<PlanDeportivo> planes = listar();
        localizar(planes, id).actualizar(nombre, deporte);
        guardar(planes);
    }

    // UPDATE [APPEND]: añade un objeto a la lista de entrenamientos del plan.
    public void agregarEntrenamiento(int id, Entrenamiento entrenamiento) throws IOException {
        List<PlanDeportivo> planes = listar();
        localizar(planes, id).agregarEntrenamiento(entrenamiento);
        guardar(planes);
    }

    // DELETE: borra únicamente el plan seleccionado.
    public boolean borrar(int id) throws IOException {
        List<PlanDeportivo> planes = listar();
        boolean borrado = planes.removeIf(p -> p.getId() == id);
        if (borrado) guardar(planes);
        return borrado;
    }

    private PlanDeportivo localizar(List<PlanDeportivo> planes, int id) {
        return planes.stream().filter(p -> p.getId() == id).findFirst()
                .orElseThrow(() -> new NoSuchElementException("No existe el plan " + id));
    }

    private void validarColeccion(List<PlanDeportivo> planes) {
        if (planes == null) throw new IllegalArgumentException("Se esperaba un array JSON.");
        Set<Integer> ids = new HashSet<>();
        for (PlanDeportivo plan : planes) {
            if (plan == null) throw new IllegalArgumentException("Plan nulo en el archivo.");
            plan.validar();
            if (!ids.add(plan.getId())) throw new IllegalArgumentException("ID duplicado en el archivo.");
        }
    }

    private void guardar(List<PlanDeportivo> planes) throws IOException {
        validarColeccion(planes);
        Files.createDirectories(archivo.getParent());
        Path temporal = Files.createTempFile(archivo.getParent(), "planes-", ".tmp");
        try {
            Files.writeString(temporal, GSON.toJson(planes, TIPO_LISTA) + System.lineSeparator(),
                    StandardCharsets.UTF_8);
            try {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }
}
