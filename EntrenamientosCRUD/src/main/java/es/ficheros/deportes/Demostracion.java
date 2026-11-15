package es.ficheros.deportes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/** Ejecución repetible que guarda las evidencias de cada operación. */
public final class Demostracion {
    private Demostracion() { }

    public static void ejecutar() throws IOException {
        Path carpeta = Path.of("datos", "demo");
        Files.createDirectories(carpeta);
        // Este modo reinicia exclusivamente el archivo de demostración.
        Path archivo = carpeta.resolve("planes.json");
        Files.writeString(archivo, "[]\n", StandardCharsets.UTF_8);
        RepositorioPlanes repo = new RepositorioPlanes(archivo);
        StringBuilder salida = new StringBuilder();
        linea(salida, "=== " + PlanDeportivo.NOMBRE_APLICACION + " ===");
        linea(salida, "Modo demo: solo se reinicia datos/demo/planes.json");
        linea(salida, "Java: " + System.getProperty("java.version"));

        PlanDeportivo carrera = new PlanDeportivo(1, "Preparación 5 km", "Running",
                new Deportista("Álex García", 22));
        carrera.setNotaPrivada("Nota temporal de ejemplo");
        carrera.registrarConsulta();
        carrera.agregarEntrenamiento(new Entrenamiento("2026-11-16", "Carrera suave", 30));
        PlanDeportivo piscina = new PlanDeportivo(2, "Técnica en piscina", "Natación",
                new Deportista("Lucía Pérez", 24));
        piscina.agregarEntrenamiento(new Entrenamiento("2026-11-17", "Crol", 40));
        linea(salida, "STATIC: planes creados con constructor en esta JVM = "
                + PlanDeportivo.getPlanesCreadosEnSesion());
        linea(salida, "TRANSIENT en memoria: nota presente = " + (carrera.getNotaPrivada() != null)
                + "; consulta registrada = " + (carrera.getUltimaConsultaMillis() > 0));

        repo.crear(carrera);
        repo.crear(piscina);
        linea(salida, "CREATE: guardados los planes 1 y 2. Total = " + repo.listar().size());
        copia(archivo, carpeta.resolve("01_creacion.json"));

        List<PlanDeportivo> leidos = new RepositorioPlanes(archivo).listar();
        linea(salida, "READ: recuperados del disco " + leidos.size() + " planes.");
        for (PlanDeportivo p : leidos) linea(salida, "  " + p);
        linea(salida, "TRANSIENT tras leer: nota = " + leidos.get(0).getNotaPrivada()
                + "; ultimaConsultaMillis = " + leidos.get(0).getUltimaConsultaMillis());
        copia(archivo, carpeta.resolve("02_lectura.json"));

        repo.agregarEntrenamiento(1, new Entrenamiento("2026-11-18", "Series 6 x 400 m", 45));
        linea(salida, "APPEND: plan 1 pasa de 1 a " + repo.buscar(1).getEntrenamientos().size()
                + " sesiones; se conserva la primera.");
        copia(archivo, carpeta.resolve("03_append.json"));

        repo.actualizar(1, "Preparación 5 km - semana 2", "Atletismo");
        linea(salida, "UPDATE: " + repo.buscar(1));
        copia(archivo, carpeta.resolve("04_actualizacion.json"));

        linea(salida, "DELETE: plan 2 eliminado = " + repo.borrar(2));
        copia(archivo, carpeta.resolve("05_borrado.json"));
        PlanDeportivo finalPlan = new RepositorioPlanes(archivo).buscar(1);
        linea(salida, "READ FINAL: " + finalPlan);
        for (Entrenamiento e : finalPlan.getEntrenamientos()) linea(salida, "  " + e);
        String json = Files.readString(archivo, StandardCharsets.UTF_8);
        boolean excluidos = !json.contains("notaPrivada") && !json.contains("ultimaConsultaMillis")
                && !json.contains("planesCreadosEnSesion") && !json.contains("NOMBRE_APLICACION");
        if (!excluidos || repo.listar().size() != 1 || finalPlan.getEntrenamientos().size() != 2) {
            throw new IllegalStateException("La demostración no cumple el resultado esperado.");
        }
        linea(salida, "JSON: atributos static y transient ausentes = " + excluidos);
        linea(salida, "RESULTADO: CRUD completado; 1 plan persistido con 2 entrenamientos.");
        linea(salida, "JSON generado: datos/demo/planes.json");
        Files.writeString(carpeta.resolve("ejecucion.txt"), salida, StandardCharsets.UTF_8);
    }

    private static void copia(Path origen, Path destino) throws IOException {
        Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void linea(StringBuilder salida, String mensaje) {
        System.out.println(mensaje);
        salida.append(mensaje).append(System.lineSeparator());
    }
}
