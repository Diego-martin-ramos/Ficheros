package es.ficheros.deportes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Punto de entrada: sin argumentos ejecuta la demo; --menu abre el CRUD. */
public final class Aplicacion {
    private Aplicacion() { }

    public static void main(String[] args) {
        try {
            if (args.length == 0 || (args.length == 1 && "--demo".equals(args[0]))) {
                Demostracion.ejecutar();
            } else if (args.length == 1 && "--menu".equals(args[0])) {
                menu();
            } else {
                throw new IllegalArgumentException("Uso: --demo o --menu");
            }
        } catch (IOException | IllegalArgumentException ex) {
            System.err.println("ERROR: " + ex.getMessage());
            System.exit(1);
        }
    }

    private static void menu() {
        RepositorioPlanes repo = new RepositorioPlanes(Path.of("datos", "planes.json"));
        Scanner entrada = new Scanner(System.in);
        System.out.println(PlanDeportivo.NOMBRE_APLICACION);
        System.out.println("Archivo: " + repo.getArchivo());
        while (true) {
            System.out.println("\n1 Crear | 2 Listar | 3 Buscar | 4 Actualizar | 5 Append sesión | 6 Borrar | 0 Salir");
            if (!entrada.hasNextLine()) return;
            String opcion = entrada.nextLine().trim();
            try {
                switch (opcion) {
                    case "0": return;
                    case "1": {
                        int id = numero(entrada, "ID: ");
                        String nombre = texto(entrada, "Nombre del plan: ");
                        String deporte = texto(entrada, "Deporte: ");
                        String persona = texto(entrada, "Nombre del deportista: ");
                        int edad = numero(entrada, "Edad: ");
                        repo.crear(new PlanDeportivo(id, nombre, deporte, new Deportista(persona, edad)));
                        System.out.println("Plan creado y guardado.");
                        break;
                    }
                    case "2": {
                        var planes = repo.listar();
                        if (planes.isEmpty()) System.out.println("No hay planes.");
                        planes.forEach(System.out::println);
                        break;
                    }
                    case "3": {
                        PlanDeportivo plan = repo.buscar(numero(entrada, "ID: "));
                        System.out.println(plan);
                        plan.getEntrenamientos().forEach(System.out::println);
                        break;
                    }
                    case "4": {
                        int id = numero(entrada, "ID: ");
                        String nombre = texto(entrada, "Nuevo nombre: ");
                        String deporte = texto(entrada, "Nuevo deporte: ");
                        repo.actualizar(id, nombre, deporte);
                        System.out.println("Plan actualizado y guardado.");
                        break;
                    }
                    case "5": {
                        int id = numero(entrada, "ID del plan: ");
                        String fecha = texto(entrada, "Fecha (yyyy-MM-dd): ");
                        String actividad = texto(entrada, "Actividad: ");
                        int minutos = numero(entrada, "Minutos: ");
                        repo.agregarEntrenamiento(id, new Entrenamiento(fecha, actividad, minutos));
                        System.out.println("Sesión añadida y guardada (append).");
                        break;
                    }
                    case "6":
                        System.out.println(repo.borrar(numero(entrada, "ID: "))
                                ? "Plan eliminado." : "No existe ese plan.");
                        break;
                    default: System.out.println("Opción incorrecta.");
                }
            } catch (IOException | IllegalArgumentException ex) {
                System.out.println("No se pudo completar la operación: " + ex.getMessage());
            } catch (NoSuchElementException ex) {
                if (!entrada.hasNextLine()) return;
                System.out.println(ex.getMessage());
            }
        }
    }

    private static String texto(Scanner entrada, String mensaje) {
        System.out.print(mensaje);
        return entrada.nextLine().trim();
    }

    private static int numero(Scanner entrada, String mensaje) {
        try {
            return Integer.parseInt(texto(entrada, mensaje));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Introduce un número entero.");
        }
    }
}
