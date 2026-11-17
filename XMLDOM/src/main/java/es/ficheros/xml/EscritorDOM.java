package es.ficheros.xml;

import es.ficheros.deportes.Entrenamiento;
import es.ficheros.deportes.PlanDeportivo;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Construye un árbol DOM a partir de los objetos del ejercicio CRUD. */
public final class EscritorDOM {

    public void guardar(List<PlanDeportivo> planes, Path destino)
            throws ParserConfigurationException, TransformerException, IOException {
        if (destino == null) {
            throw new IllegalArgumentException("La ruta de destino es obligatoria.");
        }
        // Construir y validar antes de abrir el fichero evita truncarlo por datos inválidos.
        Document documento = crearDocumento(planes);
        Path archivo = destino.toAbsolutePath();
        Files.createDirectories(archivo.getParent());

        Transformer transformador = TransformerFactory.newInstance().newTransformer();
        transformador.setOutputProperty(OutputKeys.METHOD, "xml");
        transformador.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformador.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformador.setOutputProperty(OutputKeys.INDENT, "yes");
        transformador.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        // Se usa OutputStream para que Transformer aplique la codificación declarada.
        try (OutputStream salida = Files.newOutputStream(archivo)) {
            transformador.transform(new DOMSource(documento), new StreamResult(salida));
        }
    }

    public Document crearDocumento(List<PlanDeportivo> planes)
            throws ParserConfigurationException {
        if (planes == null) {
            throw new IllegalArgumentException("La lista de planes es obligatoria.");
        }
        Set<Integer> identificadores = new HashSet<>();
        for (PlanDeportivo plan : planes) {
            if (plan == null) throw new IllegalArgumentException("No se admiten planes nulos.");
            plan.validar();
            if (!identificadores.add(plan.getId())) {
                throw new IllegalArgumentException("ID de plan duplicado: " + plan.getId());
            }
        }

        Document documento = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element raiz = documento.createElement("planesDeportivos");
        documento.appendChild(raiz);

        for (PlanDeportivo plan : planes) {
            Element elementoPlan = documento.createElement("plan");
            elementoPlan.setAttribute("id", Integer.toString(plan.getId()));
            raiz.appendChild(elementoPlan);
            agregarTexto(documento, elementoPlan, "nombre", plan.getNombre());
            agregarTexto(documento, elementoPlan, "deporte", plan.getDeporte());

            Element deportista = documento.createElement("deportista");
            elementoPlan.appendChild(deportista);
            agregarTexto(documento, deportista, "nombre", plan.getDeportista().getNombre());
            agregarTexto(documento, deportista, "edad",
                    Integer.toString(plan.getDeportista().getEdad()));

            Element entrenamientos = documento.createElement("entrenamientos");
            elementoPlan.appendChild(entrenamientos);
            for (Entrenamiento sesion : plan.getEntrenamientos()) {
                Element entrenamiento = documento.createElement("entrenamiento");
                entrenamientos.appendChild(entrenamiento);
                agregarTexto(documento, entrenamiento, "fecha", sesion.getFecha());
                agregarTexto(documento, entrenamiento, "actividad", sesion.getActividad());
                agregarTexto(documento, entrenamiento, "minutos",
                        Integer.toString(sesion.getMinutos()));
            }
        }
        // El mapeo es explícito: static y transient no se exportan por decisión del programa.
        // DOM no los excluye automáticamente, como sí hacía Gson en el ejercicio anterior.
        return documento;
    }

    private void agregarTexto(Document documento, Element padre, String etiqueta, String valor) {
        Element hijo = documento.createElement(etiqueta);
        hijo.appendChild(documento.createTextNode(valor));
        padre.appendChild(hijo);
    }
}
