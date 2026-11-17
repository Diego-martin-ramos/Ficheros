# XML DOM - Mi agenda deportiva

Actividad de XML DOM de la UT1 (páginas 58-63). Proyecto Java Maven para Apache NetBeans.
Reutiliza sin cambios las clases `PlanDeportivo`, `Deportista` y `Entrenamiento` de
`EntrenamientosCRUD`, el ejercicio 11 del repositorio. Los datos son ficticios.

## Abrir y ejecutar en Apache NetBeans

1. Descomprime `XMLDOM.zip` completo.
2. Selecciona **File > Open Project** y abre la carpeta `XMLDOM`, que contiene `pom.xml`.
3. Utiliza un **JDK 17 o superior**. Se ha probado con NetBeans IDE 27 y JDK Zulu 24.0.2.
4. Clic derecho en el proyecto: **Clean and Build**. Ejecuta también las seis pruebas JUnit.
5. Clic derecho en el proyecto: **Run**. La clase principal es `es.ficheros.xml.AplicacionXML`.
6. Revisa `datos/planes.xml` y la salida de NetBeans. Aparecen 2 planes, 2 deportistas y 3 sesiones.

La primera compilación necesita Internet para los plugins de Maven y JUnit.
El programa usa únicamente las bibliotecas XML incluidas en Java, sin dependencias de producción.
No hace falta ejecutar el CRUD ni disponer de su JSON: esta actividad crea objetos de sus mismas clases.

## Ejecutar desde consola

Desde la carpeta `XMLDOM`, con JDK 17+ y Maven disponibles:

```text
mvn clean package
java -jar target/xml-dom-1.0.0.jar
```

También se incluye un JAR ya compilado que no necesita Maven:

```text
java -jar dist/xml-dom-1.0.0.jar
java -jar dist/xml-dom-1.0.0.jar "datos/otra-salida.xml"
```

El destino predeterminado es `datos/planes.xml`, relativo al directorio de ejecución.
Cada ejecución regenera y sobrescribe el XML de destino con los mismos datos de ejemplo.
Se crean las carpetas que falten. Un error se muestra por la salida de errores y devuelve código 1.

## Clases y mapeo XML

| Clase | Responsabilidad |
| --- | --- |
| `PlanDeportivo` | Modelo del CRUD: ID, nombre, deporte, deportista y lista de sesiones. |
| `Deportista` | Nombre y edad de la persona asociada al plan. |
| `Entrenamiento` | Fecha ISO, actividad y duración en minutos. |
| `DatosEjemplo` | Crea dos planes y tres entrenamientos con datos ficticios. |
| `EscritorDOM` | Valida los objetos, construye el árbol DOM y lo escribe con Transformer. |
| `AplicacionXML` | Ejecuta la generación y vuelve a analizar el archivo obtenido con DOM. |
| `EscritorDOMTest` | Seis pruebas de persistencia, caracteres, campos temporales y errores. |

La raíz es `<planesDeportivos>`. Cada `<plan id="...">` contiene `<nombre>`, `<deporte>`,
un objeto `<deportista>` y una colección `<entrenamientos>`. Se conservan todos los
atributos de instancia persistentes y el orden de los objetos y sesiones.

Los atributos `static` (`NOMBRE_APLICACION`, `planesCreadosEnSesion`) y `transient`
(`notaPrivada`, `ultimaConsultaMillis`) permanecen en las clases, pero no se exportan.
Es una decisión explícita del mapeo para mantener el criterio del CRUD anterior.
**DOM no interpreta esos modificadores ni los excluye automáticamente.**
Los comentarios de los modelos que mencionan Gson se conservan del ejercicio original;
este proyecto no utiliza Gson ni serialización JSON.

El texto se incorpora con `createTextNode`, sin concatenar etiquetas. `Transformer`
escapa los caracteres reservados: `Series & técnica <avanzada>` se guarda como
`Series &amp; técnica &lt;avanzada&gt;`. La lectura DOM recupera el texto original.
La salida declara UTF-8 y se escribe mediante un `OutputStream` con esa codificación.
Se comprueban datos nulos e identificadores duplicados antes de abrir el destino.
No se valida contra DTD ni XSD; las comprobaciones cubren el XML bien formado y los datos del modelo.

## Archivos entregados

- `Informe_ejecucion.pdf`: explicación, ejecución real en NetBeans y resultados.
- `datos/planes.xml`: XML real obtenido al ejecutar la aplicación.
- `src/main/java/`: las seis clases Java de producción.
- `src/test/java/`: pruebas JUnit.
- `dist/xml-dom-1.0.0.jar`: ejecutable para Java 17+.
- `evidencias/`: salida de ejecución, compilación, pruebas y capturas de NetBeans.
- `pom.xml` y `nbactions.xml`: configuración Maven y acción Run de NetBeans.

Repositorio: https://github.com/Diego-martin-ramos/Ficheros/tree/main/XMLDOM

## Referencias

- Apuntes `UT1-Manejo-de-Ficheros-en-Java.pdf`, páginas 58-63; ejemplo guiado en la página 63.
- Modelos del ejercicio 11: `../EntrenamientosCRUD/src/main/java/es/ficheros/deportes/`,
  versión de origen `bd8c7c7965a9d80402d9b78a6ace4c32983ee05b`.
- Material de la tarea: https://www.w3schools.com/xml/xml_whatis.asp
- Material de la tarea: https://www.w3schools.com/xml/xml_dom.asp
- Vídeo de introducción indicado en la tarea: https://www.youtube.com/watch?v=wIIXGcaUv3c
  (no se ha utilizado su contenido para elaborar la solución).

Los enlaces de W3Schools son material didáctico; W3Schools y W3C son entidades distintas.
