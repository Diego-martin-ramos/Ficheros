/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.ejercicio2ficheros;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;



/**
 *
 * @author diego.marram.1
 */
public class Ejercicio2Ficherosio {


public class EjemploJavaIO {
    public static void main(String[] args) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("datos_io.txt"))) {

            writer.println("Diego Martin Ramos");
            writer.println("20");
            writer.println("Salamanca");
            
            System.out.println("Fichero guardado con java.io");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
    

public class EjemploJavaNIO {
    public static void main(String[] args) {
        // Los 3 datos a guardar
        List<String> datos = List.of("Diego Martín Ramos", "20", "Salamanca");
        Path ruta = Paths.get("datos_nio.txt");

        try {
            Files.write(ruta, datos);
            System.out.println("Fichero guardado con java.nio");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
}
