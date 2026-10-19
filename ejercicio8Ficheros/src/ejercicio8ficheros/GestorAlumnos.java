/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ejercicio8ficheros;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Gestor de alumnos en texto: un nombre completo por línea, codificado en UTF-8.
 * Usa rutas relativas y solo añade registros; no modifica ni elimina alumnos.
 * Requiere Java 11 o posterior por el uso de Files.writeString y String.strip.
 */
public class GestorAlumnos {
    private static final Path FICHERO = Paths.get("datos", "alumnos.txt");
    private static final int LONGITUD_MAXIMA = 100;

    /** Muestra el menú hasta elegir 0 o finalizar la entrada estándar.
     * @param args */
    public static void main(String[] args) {
        System.out.println("GESTOR DE ALUMNOS");
        System.out.println("Fichero: " + FICHERO.toAbsolutePath().normalize());

        try (Scanner teclado = new Scanner(System.in, StandardCharsets.UTF_8.name())) {
            boolean salir = false;
            while (!salir) {
                mostrarMenu();
                if (!teclado.hasNextLine()) {
                    System.out.println("Fin de la entrada. Programa terminado.");
                    break;
                }
                String opcion = teclado.nextLine().strip();
                try {
                    switch (opcion) {
                        case "1":
                            System.out.print("Nombre completo: ");
                            if (teclado.hasNextLine()) {
                                altaAlumno(teclado.nextLine());
                            } else {
                                System.out.println("Alta cancelada: no se recibió un nombre.");
                                salir = true;
                            }
                            break;
                        case "2":
                            mostrarAlumnos();
                            break;
                        case "3":
                            System.out.print("Nombre o parte del nombre: ");
                            if (teclado.hasNextLine()) {
                                buscarAlumnos(teclado.nextLine());
                            } else {
                                System.out.println("Búsqueda cancelada: no se recibió un nombre.");
                                salir = true;
                            }
                            break;
                        case "4":
                            contarAlumnos();
                            break;
                        case "0":
                            System.out.println("Hasta pronto.");
                            salir = true;
                            break;
                        default:
                            System.out.println("Opción no válida. Elige de 0 a 4.");
                    }
                } catch (FileAlreadyExistsException e) {
                    System.out.println("No se puede crear la carpeta: su ruta está ocupada por un fichero.");
                } catch (NoSuchFileException e) {
                    System.out.println("No se encuentra el fichero o su carpeta. Vuelve a intentarlo.");
                } catch (AccessDeniedException e) {
                    System.out.println("No hay permiso para acceder al fichero o a su carpeta.");
                } catch (IOException e) {
                    System.out.println("No se pudo completar la operación: " + e.getMessage());
                }
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n1. Alta de alumno");
        System.out.println("2. Mostrar alumnos");
        System.out.println("3. Buscar por nombre");
        System.out.println("4. Contar registros");
        System.out.println("0. Salir");
        System.out.print("Opción: ");
    }

    /** Elimina espacios exteriores y unifica los espacios entre palabras. */
    private static String normalizar(String texto) {
        return texto.strip().replaceAll("\\s+", " ");
    }

    /** Admite letras, tildes, espacios, guiones y apóstrofos; exige alguna letra. */
    private static boolean nombreValido(String nombre) {
        return !nombre.isEmpty()
                && nombre.length() <= LONGITUD_MAXIMA
                && nombre.matches("[\\p{L}\\p{M} '\u2019-]+")
                && nombre.matches(".*\\p{L}.*");
    }

    /**
     * Añade un nombre al final con CREATE y APPEND, conservando todos los bytes
     * anteriores. Si falta el salto de línea final, lo añade antes del nombre.
     */
    private static void altaAlumno(String entrada) throws IOException {
        String nombre = normalizar(entrada);
        if (!nombreValido(nombre)) {
            System.out.println("Nombre no válido: usa de 1 a 100 caracteres, con letras,");
            System.out.println("espacios, guiones o apóstrofos. No se admiten números.");
            return;
        }

        Files.createDirectories(FICHERO.getParent());
        comprobarTipo();
        String separador = "";
        if (Files.exists(FICHERO) && Files.size(FICHERO) > 0) {
            // El fichero es pequeño: esta lectura evita unir dos nombres si se
            // ha editado a mano y su última línea no termina en salto de línea.
            String contenido = Files.readString(FICHERO, StandardCharsets.UTF_8);
            if (!contenido.endsWith("\n") && !contenido.endsWith("\r")) {
                separador = System.lineSeparator();
            }
        }
        Files.writeString(FICHERO, separador + nombre + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        System.out.println("Alumno añadido: " + nombre);
    }

    /** Impide tratar como fichero una carpeta que se llame alumnos.txt. */
    private static void comprobarTipo() throws IOException {
        if (Files.exists(FICHERO) && !Files.isRegularFile(FICHERO)) {
            throw new IOException("La ruta datos/alumnos.txt no es un fichero regular.");
        }
    }

    /**
     * Devuelve los nombres no vacíos. Un fichero inexistente equivale a cero
     * registros y se informa de ello; las consultas no lo crean.
     * Files.readAllLines abre y cierra su recurso internamente.
     */
    private static List<String> leerAlumnos() throws IOException {
        comprobarTipo();
        List<String> alumnos = new ArrayList<>();
        if (Files.notExists(FICHERO)) {
            System.out.println("El fichero aún no existe; se creará con la primera alta.");
            return alumnos;
        }
        for (String linea : Files.readAllLines(FICHERO, StandardCharsets.UTF_8)) {
            String nombre = normalizar(linea);
            if (!nombre.isEmpty()) {
                alumnos.add(nombre);
            }
        }
        return alumnos;
    }

    /** Lista todos los registros en su orden de alta. */
    private static void mostrarAlumnos() throws IOException {
        List<String> alumnos = leerAlumnos();
        if (alumnos.isEmpty()) {
            System.out.println("No hay alumnos registrados.");
            return;
        }
        System.out.println("ALUMNOS REGISTRADOS");
        for (int i = 0; i < alumnos.size(); i++) {
            System.out.println((i + 1) + ". " + alumnos.get(i));
        }
    }

    /** Búsqueda parcial sin distinguir mayúsculas; las tildes sí se distinguen. */
    private static void buscarAlumnos(String entrada) throws IOException {
        String consulta = normalizar(entrada);
        if (!nombreValido(consulta)) {
            System.out.println("Búsqueda no válida: escribe un nombre o parte de él (1 a 100 caracteres).");
            return;
        }
        List<String> alumnos = leerAlumnos();
        String buscado = consulta.toLowerCase(Locale.ROOT);
        int coincidencias = 0;
        for (String alumno : alumnos) {
            if (alumno.toLowerCase(Locale.ROOT).contains(buscado)) {
                System.out.println("- " + alumno);
                coincidencias++;
            }
        }
        if (coincidencias == 0) {
            System.out.println("No se encontraron alumnos con ese nombre.");
        } else {
            System.out.println("Coincidencias: " + coincidencias);
        }
    }

    /** Cuenta registros no vacíos. Dos nombres iguales son dos registros. */
    private static void contarAlumnos() throws IOException {
        System.out.println("Total de registros: " + leerAlumnos().size());
    }
}
