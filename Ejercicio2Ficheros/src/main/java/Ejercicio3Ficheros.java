/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author diego.marram.1
 */
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Scanner;

public class Ejercicio3Ficheros {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Directorio a explorar: ");
        String ruta = sc.nextLine().trim();
        File dir = new File(ruta);

        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Error: el directorio no existe o no es un directorio.");
            return;
        }

        // Mejora opcional
        final String extension;
        if (args.length > 0) {
            extension = args[0].startsWith(".") ? args[0] : "." + args[0];
            System.out.println("Extension recibida por parametro: " + extension);
        } else {
            System.out.print("Extension (ej. .java): ");
            String e = sc.nextLine().trim();
            extension = e.startsWith(".") ? e : "." + e;
        }

        System.out.print("Prefijo para la forma 3 (ej. Fil): ");
        final String prefijo = sc.nextLine().trim();

        // ejemplo 1
        System.out.println("\n=== FORMA 1: Clase anonima (termina en " + extension + ") ===");
        File[] f1 = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File d, String nombre) {
                return nombre.endsWith(extension);
            }
        });
        mostrar(f1);

        // ejemplo 2
        System.out.println("\n=== FORMA 2: Expresion lambda (termina en " + extension + ") ===");
        File[] f2 = dir.listFiles((d, nombre) -> nombre.endsWith(extension));
        mostrar(f2);

        // ejemplo 3
        System.out.println("\n=== FORMA 3: Prefijo '" + prefijo + "' + extension '" + extension + "' ===");
        File[] f3 = dir.listFiles((d, nombre) -> nombre.startsWith(prefijo) && nombre.endsWith(extension));
        mostrar(f3);

        // ejemplo 4
        System.out.println("\n=== FORMA 4: Solo directorios (FileFilter) ===");
        File[] f4 = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }
        });
        if (f4 == null || f4.length == 0) {
            System.out.println("(ninguno)");
        } else {
            Arrays.sort(f4);
            for (File d : f4) {
                System.out.println("[DIR] " + d.getName());
            }
        }
    }

    private static void mostrar(File[] ficheros) {
        if (ficheros == null || ficheros.length == 0) {
            System.out.println("(ninguno)");
            return;
        }
        Arrays.sort(ficheros);
        for (File f : ficheros) {
            System.out.println(f.getName());
        }
    }
}
