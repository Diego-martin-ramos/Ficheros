package es.ficheros.xml;

import es.ficheros.deportes.Deportista;
import es.ficheros.deportes.Entrenamiento;
import es.ficheros.deportes.PlanDeportivo;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class EscritorDOMTest {
    @TempDir Path temporal;
    private final EscritorDOM escritor = new EscritorDOM();

    private Document leer(Path archivo) throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(archivo.toFile());
    }

    private String texto(Element padre, String etiqueta) {
        return padre.getElementsByTagName(etiqueta).item(0).getTextContent();
    }

    @Test void recuperaTodosLosDatosYElOrdenDesdeElFichero() throws Exception {
        List<PlanDeportivo> originales = DatosEjemplo.crearPlanes();
        Path archivo = temporal.resolve("subcarpeta/planes.xml");
        escritor.guardar(originales, archivo);
        Document documento = leer(archivo);
        assertEquals("planesDeportivos", documento.getDocumentElement().getTagName());
        NodeList planes = documento.getElementsByTagName("plan");
        assertEquals(originales.size(), planes.getLength());
        for (int i = 0; i < originales.size(); i++) {
            PlanDeportivo original = originales.get(i);
            Element plan = (Element) planes.item(i);
            assertEquals(Integer.toString(original.getId()), plan.getAttribute("id"));
            assertEquals(original.getNombre(), texto(plan, "nombre"));
            assertEquals(original.getDeporte(), texto(plan, "deporte"));
            Element deportista = (Element) plan.getElementsByTagName("deportista").item(0);
            assertEquals(original.getDeportista().getNombre(), texto(deportista, "nombre"));
            assertEquals(original.getDeportista().getEdad(), Integer.parseInt(texto(deportista, "edad")));
            NodeList sesiones = plan.getElementsByTagName("entrenamiento");
            assertEquals(original.getEntrenamientos().size(), sesiones.getLength());
            for (int j = 0; j < sesiones.getLength(); j++) {
                Entrenamiento originalSesion = original.getEntrenamientos().get(j);
                Element sesion = (Element) sesiones.item(j);
                assertEquals(originalSesion.getFecha(), texto(sesion, "fecha"));
                assertEquals(originalSesion.getActividad(), texto(sesion, "actividad"));
                assertEquals(originalSesion.getMinutos(), Integer.parseInt(texto(sesion, "minutos")));
            }
        }
    }

    @Test void conservaUtf8YEscapaCaracteresReservados() throws Exception {
        Path archivo = temporal.resolve("especiales.xml");
        PlanDeportivo plan = new PlanDeportivo(7, "Árbol & <piscina> \"ñ\"", "Natación",
                new Deportista("Lucía Pérez", 24));
        escritor.guardar(List.of(plan), archivo);
        String xml = Files.readString(archivo, StandardCharsets.UTF_8);
        assertTrue(xml.contains("encoding=\"UTF-8\""));
        assertTrue(xml.contains("&amp;"));
        assertTrue(xml.contains("&lt;piscina&gt;"));
        assertEquals(plan.getNombre(), texto((Element) leer(archivo)
                .getElementsByTagName("plan").item(0), "nombre"));
    }

    @Test void noExportaLosCamposStaticNiTransient() throws Exception {
        List<PlanDeportivo> planes = DatosEjemplo.crearPlanes();
        assertNotNull(planes.get(0).getNotaPrivada());
        assertTrue(planes.get(0).getUltimaConsultaMillis() > 0);
        Path archivo = temporal.resolve("persistencia.xml");
        escritor.guardar(planes, archivo);
        String xml = Files.readString(archivo);
        for (String campo : List.of("NOMBRE_APLICACION", "planesCreadosEnSesion",
                "notaPrivada", "ultimaConsultaMillis", planes.get(0).getNotaPrivada())) {
            assertFalse(xml.contains(campo), "No debe persistirse: " + campo);
        }
    }

    @Test void admiteColeccionVaciaYPlanSinSesiones() throws Exception {
        Path archivo = temporal.resolve("vacio.xml");
        escritor.guardar(List.of(), archivo);
        assertEquals(0, leer(archivo).getElementsByTagName("plan").getLength());
        PlanDeportivo plan = new PlanDeportivo(3, "Inicio", "Atletismo", new Deportista("Ana", 18));
        escritor.guardar(List.of(plan), archivo);
        Document documento = leer(archivo);
        assertEquals(1, documento.getElementsByTagName("entrenamientos").getLength());
        assertEquals(0, documento.getElementsByTagName("entrenamiento").getLength());
    }

    @Test void rechazaDatosNulosYDuplicadosSinTruncarElDestino() throws Exception {
        Path archivo = temporal.resolve("conservar.xml");
        Files.writeString(archivo, "contenido anterior", StandardCharsets.UTF_8);
        PlanDeportivo plan = DatosEjemplo.crearPlanes().get(0);
        assertThrows(IllegalArgumentException.class, () -> escritor.guardar(null, archivo));
        assertThrows(IllegalArgumentException.class, () -> escritor.guardar(Arrays.asList(plan, null), archivo));
        assertThrows(IllegalArgumentException.class, () -> escritor.guardar(List.of(plan, plan), archivo));
        assertEquals("contenido anterior", Files.readString(archivo));
    }

    @Test void comunicaUnDestinoQueNoEsUnFichero() {
        assertThrows(java.io.IOException.class,
                () -> escritor.guardar(DatosEjemplo.crearPlanes(), temporal));
    }
}
