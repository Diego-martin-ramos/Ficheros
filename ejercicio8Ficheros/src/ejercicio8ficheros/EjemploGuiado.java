/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ejercicio8ficheros;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

/**
 * Adaptación ejecutable del ejemplo de la página 20 de la UT1.
 * Trabaja en ejemplo_guiado para separar sus datos de los del gestor.
 * Cada ejecución añade dos nombres a su propio alumnos.txt.
 */
public class EjemploGuiado {
    public static void main(String[] args) {
        Path base = Paths.get("ejemplo_guiado");
        Path datos = base.resolve("datos");
        Path backup = base.resolve("backup");
        Path alumnos = datos.resolve("alumnos.txt");

        try {
            // 1. Primero se crean las carpetas y el fichero que se va a consultar.
            Files.createDirectories(datos);
            Files.createDirectories(backup);
            Files.createDirectories(datos.resolve(Paths.get("2024", "enero")));
            try {
                Files.createFile(alumnos);
                System.out.println("Fichero de alumnos creado.");
            } catch (FileAlreadyExistsException e) {
                if (!Files.isRegularFile(alumnos)) {
                    throw new IOException("La ruta de alumnos no es un fichero.");
                }
                System.out.println("El fichero ya existe; se conserva su contenido.");
            }

            System.out.println("\n1. RUTAS Y PROPIEDADES");
            System.out.println("Nombre: " + alumnos.getFileName());
            System.out.println("Padre: " + alumnos.getParent());
            System.out.println("Absoluta: " + alumnos.toAbsolutePath());
            Path conPuntos = datos.resolve("..").resolve("datos").resolve("alumnos.txt");
            System.out.println("Normalizada: " + conPuntos.normalize());
            System.out.println("Existe: " + Files.exists(alumnos));
            System.out.println("Es directorio: " + Files.isDirectory(alumnos));
            System.out.println("Es fichero: " + Files.isRegularFile(alumnos));
            System.out.println("Tamaño inicial: " + Files.size(alumnos) + " bytes");
            System.out.println("Última modificación: " + Files.getLastModifiedTime(alumnos));

            // 2. APPEND mantiene los registros anteriores; UTF-8 conserva las tildes.
            System.out.println("\n2. ESCRITURA Y LECTURA");
            Files.writeString(alumnos, "Ana García\nLuis Pérez\n",
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            List<String> lineas = Files.readAllLines(alumnos, StandardCharsets.UTF_8);
            for (String linea : lineas) {
                System.out.println(linea);
            }
            System.out.println("Registros del ejemplo: " + lineas.size());

            // Se muestra Files.write sobre OTRO fichero para no borrar alumnos.
            Files.write(datos.resolve("listado.txt"), lineas, StandardCharsets.UTF_8);
            System.out.println("Nombres que empiezan por A, en mayúsculas:");
            try (Stream<String> lineasStream = Files.lines(alumnos, StandardCharsets.UTF_8)) {
                lineasStream.filter(linea -> linea.startsWith("A"))
                        .map(String::toUpperCase)
                        .forEach(System.out::println);
            }

            // 3. Se copia el fichero y se mueve la copia, conservando el original.
            System.out.println("\n3. COPIA Y MOVIMIENTO");
            Path copia = backup.resolve("lista.txt");
            Path movida = backup.resolve("lista_movida.txt");
            Files.copy(alumnos, copia, StandardCopyOption.REPLACE_EXISTING);
            try {
                Files.copy(alumnos, copia);
            } catch (FileAlreadyExistsException e) {
                System.out.println("Copia sin reemplazo: el destino ya existe.");
            }
            Files.copy(alumnos, backup.resolve("lista_atributos.txt"),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            Files.move(copia, movida, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copia movida a: " + movida);
            System.out.println("El original se conserva: " + Files.exists(alumnos));

            // 4. El segundo borrado ilustra una excepción específica del PDF.
            System.out.println("\n4. CREACIÓN Y BORRADO DE UN FICHERO TEMPORAL");
            Path temporal = datos.resolve(Paths.get("2024", "enero", "registro.txt"));
            try {
                Files.createFile(temporal);
            } catch (FileAlreadyExistsException e) {
                System.out.println("El fichero temporal del ejemplo ya existía.");
            }
            System.out.println("Temporal borrado: " + Files.deleteIfExists(temporal));
            try {
                Files.delete(temporal);
            } catch (NoSuchFileException e) {
                System.out.println("Segundo borrado controlado: el fichero ya no existe.");
            }

            // 5. Los Stream que abren recursos se cierran con try-with-resources.
            System.out.println("\n5. LISTADO DIRECTO");
            try (Stream<Path> hijos = Files.list(datos)) {
                hijos.sorted().forEach(System.out::println);
            }
            System.out.println("Ficheros .txt del árbol completo:");
            try (Stream<Path> arbol = Files.walk(base)) {
                arbol.filter(Files::isRegularFile)
                        .filter(ruta -> ruta.toString().endsWith(".txt"))
                        .sorted().forEach(System.out::println);
            }
            System.out.println("Recorrido con profundidad máxima 2:");
            try (Stream<Path> arbol = Files.walk(base, 2)) {
                arbol.sorted().forEach(System.out::println);
            }
            System.out.println("Ejemplo guiado terminado correctamente.");
        } catch (IOException e) {
            System.out.println("No se pudo completar el ejemplo: " + e.getMessage());
        } catch (UncheckedIOException e) {
            System.out.println("Error al recorrer los ficheros: " + e.getCause().getMessage());
        }
    }
}
