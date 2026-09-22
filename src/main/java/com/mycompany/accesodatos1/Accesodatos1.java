/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.accesodatos1;
import java.io.File;
import java.io.IOException;

/**
 * crear la estructura dentro de src bin para los binarios, otro directorio para imagenes, pruebas unitarias en una carpeta que se llame junit y los ficheros contacto.txt, y imagen en la carpeta imagen, readme.md
 * recorre el contenido y muestralo por pantalla 
 * @author diego.marram.1
 */
public class Accesodatos1 {

    public static void main(String[] args) {
        // Directorio raíz del proyecto
        File root = new File("MiProyecto");

        // 1. Definición de directorios
        File src = new File(root, "src");
        File bin = new File(src, "bin");
        File imagenes = new File(root, "imagenes");
        File junit = new File(root, "junit");

        // 2. Definición de archivos
        File contacto = new File(root, "contacto.txt");
        File imagen = new File(imagenes, "imagen.png");
        File readme = new File(root, "readme.md");

        // Crear las carpetas necesarias
        bin.mkdirs();
        imagenes.mkdirs();
        junit.mkdirs();

        // Crear los archivos vacíos
        try {
            contacto.createNewFile();
            imagen.createNewFile();
            readme.createNewFile();
            System.out.println("Estructura creada correctamente.\n");
        } catch (IOException e) {
            System.err.println("Error al crear los archivos: " + e.getMessage());
        }

        // 3. Recorrer y mostrar la estructura por pantalla
        System.out.println("Contenido del proyecto:");
        mostrarEstructura(root, "");
    }

    // Método recursivo para listar archivos y carpetas en formato árbol
    private static void mostrarEstructura(File dir, String indent) {
        if (!dir.exists()) return;

        System.out.println(indent + "├── " + dir.getName() + (dir.isDirectory() ? "/" : ""));

        if (dir.isDirectory()) {
            File[] ficheros = dir.listFiles();
            if (ficheros != null) {
                for (File f : ficheros) {
                    mostrarEstructura(f, indent + "│   ");
                }
            }
        }
    }
}

