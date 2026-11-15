# Mi agenda deportiva - CRUD con JSON

Ejercicio Java para Apache NetBeans. Proyecto Maven, Java 17 o superior.
Los deportistas y las sesiones de la demostración son datos ficticios.

## Abrir y ejecutar en Apache NetBeans

1. Descomprime el ZIP completo.
2. En NetBeans: **File > Open Project**, selecciona la carpeta `EntrenamientosCRUD` que contiene `pom.xml` y pulsa **Open Project**.
3. Usa un JDK 17 o superior. El proyecto se ha verificado con NetBeans 27 y JDK 24.0.2.
4. Clic derecho sobre **EntrenamientosCRUD > Clean and Build** para compilar y ejecutar las pruebas.
5. Clic derecho sobre **EntrenamientosCRUD > Run** para ejecutar la demostración. Si quieres usar F6, establece antes este proyecto como principal.
6. Para el menú: clic derecho en el proyecto, **Run Maven > Menu CRUD interactivo**. Si esa acción personalizada no aparece, en **Properties > Actions > Run project**, cambia el argumento final `--demo` por `--menu`.

La primera compilación necesita Internet para descargar las dependencias de Maven.
El JSON usa UTF-8 y se escribe en una ruta relativa a la carpeta del proyecto.
El modo `--demo` reinicia **solo** `datos/demo/planes.json` y regenera sus evidencias.
El menú trabaja con otro archivo: `datos/planes.json`, que se crea con la primera alta.
Cada operación del menú se guarda inmediatamente; no hay que pulsar una opción adicional para guardar.

## Ejecución por consola

Con JDK 17+ y Maven disponibles:

```text
mvn clean package
java -jar target/entrenamientos-crud-1.0.0-ejecutable.jar --demo
java -jar target/entrenamientos-crud-1.0.0-ejecutable.jar --menu
```

La entrega incluye una copia del JAR con Gson incorporado en `dist/`.
Ese JAR solo necesita Java 17+, sin Maven ni descargas adicionales:

```text
java -jar dist/entrenamientos-crud-1.0.0-ejecutable.jar --menu
```

Ejecuta los comandos desde la carpeta del proyecto.
El Java 8 instalado en algunos equipos no sirve para este proyecto.

## Requisitos del enunciado

| Requisito | Implementación en PlanDeportivo |
| --- | --- |
| Atributos static | `NOMBRE_APLICACION`, `planesCreadosEnSesion` |
| Atributos transient | `notaPrivada`, `ultimaConsultaMillis` |
| Atributos de instancia | `id`, `nombre`, `deporte` |
| Objeto de otra clase | `Deportista deportista` |
| Lista de objetos de otra clase | `List<Entrenamiento> entrenamientos` |

`planesCreadosEnSesion` cuenta los planes construidos con el constructor público en la JVM actual. No representa el número de registros del archivo: ese dato se obtiene con `repo.listar().size()`. Al leer JSON, Gson usa el constructor privado vacío y no incrementa el contador.

Gson excluye por defecto los atributos `static` y `transient`. Se comprueba tanto en la demostración como en las pruebas. `transient` por sí solo no obliga a cualquier biblioteca JSON a omitir un campo: aquí se usa el comportamiento de Gson. No se utiliza serialización binaria de Java ni es necesario implementar `Serializable`.

## CRUD y append

| Operación | Método de RepositorioPlanes | Menú |
| --- | --- | --- |
| Creación | `crear(plan)` | 1 |
| Lectura | `listar()` y `buscar(id)` | 2 y 3 |
| Actualización | `actualizar(id, nombre, deporte)` | 4 |
| Actualización append | `agregarEntrenamiento(id, sesion)` | 5 |
| Borrado | `borrar(id)` | 6 |

Hay dos añadidos: `crear` añade un plan conservando los anteriores y `agregarEntrenamiento` añade una sesión al final de su lista. El append es **lógico**, sobre los objetos. No se concatenan fragmentos al final del archivo, porque eso rompería un array JSON. Se lee la colección, se modifica y se vuelve a guardar como un único JSON válido.

El guardado escribe primero un temporal en el mismo directorio y después sustituye el destino. Se intenta un movimiento atómico; si el sistema de archivos no lo admite, se usa sustitución normal. Un archivo inexistente equivale a una colección vacía. Un JSON vacío, malformado o con objetos inválidos produce un error y se conserva para poder corregirlo. La aplicación está diseñada para un único proceso; no resuelve escrituras simultáneas de varios usuarios.

## Clases

- `PlanDeportivo`: modelo con todos los tipos de atributos solicitados.
- `Deportista`: objeto asociado al plan.
- `Entrenamiento`: objeto de cada sesión.
- `RepositorioPlanes`: lectura, escritura y operaciones CRUD del JSON.
- `Demostracion`: secuencia repetible y generación de evidencias.
- `Aplicacion`: entrada principal y menú interactivo.
- `RepositorioPlanesTest`: 12 pruebas JUnit, en `src/test/java`.

## Evidencias y entrega

- `datos/demo/01_creacion.json`: dos planes, una sesión por plan.
- `datos/demo/02_lectura.json`: estado leído del disco.
- `datos/demo/03_append.json`: segunda sesión añadida al plan 1.
- `datos/demo/04_actualizacion.json`: plan 1 renombrado y deporte actualizado.
- `datos/demo/05_borrado.json`: plan 2 eliminado.
- `datos/demo/planes.json`: JSON final; un plan y dos sesiones.
- `datos/demo/ejecucion.txt`: salida real de la demostración.
- `evidencias/`: compilación, pruebas y comprobación del menú.
- `Informe_ejecucion.pdf`: explicación y resultados de ejecución.
- `dist/`: JAR ejecutable que incluye las dependencias de producción.

Pruebas: `mvn test`. Resultado de esta entrega: 12 ejecutadas, 0 fallos, 0 errores, 0 omitidas.

Repositorio de destino: https://github.com/Diego-martin-ramos/Ficheros
Carpeta del ejercicio: `EntrenamientosCRUD/`.
