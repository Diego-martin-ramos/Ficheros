package es.ficheros.xml;

import es.ficheros.deportes.Deportista;
import es.ficheros.deportes.Entrenamiento;
import es.ficheros.deportes.PlanDeportivo;
import java.util.List;

/** Datos ficticios y repetibles; se reutilizan los modelos del ejercicio 11. */
public final class DatosEjemplo {
    private DatosEjemplo() { }

    public static List<PlanDeportivo> crearPlanes() {
        PlanDeportivo carrera = new PlanDeportivo(1, "Preparación 5 km", "Atletismo",
                new Deportista("Álex García", 22));
        carrera.agregarEntrenamiento(new Entrenamiento("2026-11-17", "Carrera suave", 30));
        carrera.agregarEntrenamiento(new Entrenamiento("2026-11-18", "Series & técnica <avanzada>", 45));
        carrera.setNotaPrivada("Dato temporal que no se exporta");
        carrera.registrarConsulta();

        PlanDeportivo piscina = new PlanDeportivo(2, "Técnica en piscina", "Natación",
                new Deportista("Lucía Pérez", 24));
        piscina.agregarEntrenamiento(new Entrenamiento("2026-11-19", "Crol y respiración", 40));
        return List.of(carrera, piscina);
    }
}
