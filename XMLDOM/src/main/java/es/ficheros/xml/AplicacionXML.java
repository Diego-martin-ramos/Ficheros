package es.ficheros.xml;

import es.ficheros.deportes.PlanDeportivo;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/** Entrada del proyecto: crear objetos, generar XML y volver a leerlo con DOM. */
public final class AplicacionXML {
    private AplicacionXML() { }

    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Uso: java -jar xml-dom-1.0.0.jar [ruta-del-xml]");
            System.exit(1);
        }
        try {
            Path destino = args.length == 0 ? Path.of("datos", "planes.xml") : Path.of(args[0]);
            List<PlanDeportivo> planes = DatosEjemplo.crearPlanes();
            new EscritorDOM().guardar(planes, destino);

            System.out.println("=== XML DOM - " + PlanDeportivo.NOMBRE_APLICACION + " ===");
            System.out.println("Java: " + System.getProperty("java.version"));
            System.out.println("Archivo: " + destino.toAbsolutePath().normalize());
            System.out.println("XML generado correctamente en UTF-8.");
            System.out.println();
            System.out.print(Files.readString(destino, StandardCharsets.UTF_8));

            // Se vuelve a analizar el fichero real, no el árbol que permanecía en memoria.
            Document leido = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(destino.toFile());
            System.out.println("LECTURA DOM: raíz = " + leido.getDocumentElement().getTagName());
            System.out.println("Planes leídos: " + leido.getElementsByTagName("plan").getLength());
            System.out.println("Deportistas leídos: " + leido.getElementsByTagName("deportista").getLength());
            System.out.println("Entrenamientos leídos: "
                    + leido.getElementsByTagName("entrenamiento").getLength());
            System.out.println("RESULTADO: escritura y lectura DOM completadas.");
        } catch (IOException | ParserConfigurationException | TransformerException
                | SAXException | IllegalArgumentException ex) {
            System.err.println("No se pudo generar o comprobar el XML: " + ex.getMessage());
            System.exit(1);
        }
    }
}
