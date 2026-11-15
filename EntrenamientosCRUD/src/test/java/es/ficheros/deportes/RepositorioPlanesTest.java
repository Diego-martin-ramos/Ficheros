package es.ficheros.deportes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class RepositorioPlanesTest {
    @TempDir Path temporal;
    private Path archivo;
    private RepositorioPlanes repo;

    @BeforeEach
    void preparar() {
        archivo = temporal.resolve("planes.json");
        repo = new RepositorioPlanes(archivo);
    }

    private PlanDeportivo plan(int id) {
        return new PlanDeportivo(id, "Preparación 5 km", "Running", new Deportista("Álex", 22));
    }

    @Test void archivoInexistenteDevuelveListaVacia() throws IOException {
        assertTrue(repo.listar().isEmpty());
    }

    @Test void crearYLeerObjetosAnidadosDesdeOtroRepositorio() throws IOException {
        PlanDeportivo p = plan(1);
        p.agregarEntrenamiento(new Entrenamiento("2026-11-16", "Carrera", 30));
        repo.crear(p);
        var recuperado = new RepositorioPlanes(archivo).buscar(1);
        assertEquals("Álex", recuperado.getDeportista().getNombre());
        assertEquals(30, recuperado.getEntrenamientos().get(0).getMinutos());
    }

    @Test void crearHaceAppendSinPerderPlanesAnteriores() throws IOException {
        repo.crear(plan(1));
        repo.crear(plan(2));
        assertEquals(2, repo.listar().size());
        assertEquals(1, repo.listar().get(0).getId());
    }

    @Test void actualizarPersisteYRespetaElOtroPlan() throws IOException {
        repo.crear(plan(1));
        repo.crear(plan(2));
        repo.actualizar(1, "Semana 2", "Atletismo");
        assertEquals("Semana 2", repo.buscar(1).getNombre());
        assertEquals("Atletismo", repo.buscar(1).getDeporte());
        assertEquals("Running", repo.buscar(2).getDeporte());
    }

    @Test void appendConservaOrdenYSesionesPrevias() throws IOException {
        repo.crear(plan(1));
        repo.agregarEntrenamiento(1, new Entrenamiento("2026-11-16", "Primera", 30));
        repo.agregarEntrenamiento(1, new Entrenamiento("2026-11-18", "Segunda", 45));
        var sesiones = new RepositorioPlanes(archivo).buscar(1).getEntrenamientos();
        assertEquals(2, sesiones.size());
        assertEquals("Primera", sesiones.get(0).getActividad());
        assertEquals("Segunda", sesiones.get(1).getActividad());
    }

    @Test void borrarEsPersistenteYPermiteBorrarElUltimo() throws IOException {
        repo.crear(plan(1));
        repo.crear(plan(2));
        assertTrue(repo.borrar(2));
        assertEquals(1, new RepositorioPlanes(archivo).listar().size());
        assertFalse(repo.borrar(99));
        assertTrue(repo.borrar(1));
        assertTrue(new RepositorioPlanes(archivo).listar().isEmpty());
    }

    @Test void staticYTransientNoSePersisten() throws IOException {
        int antes = PlanDeportivo.getPlanesCreadosEnSesion();
        PlanDeportivo p = plan(1);
        assertEquals(antes + 1, PlanDeportivo.getPlanesCreadosEnSesion());
        p.setNotaPrivada("nota temporal");
        p.registrarConsulta();
        repo.crear(p);
        String json = Files.readString(archivo);
        for (String nombre : new String[]{"notaPrivada", "ultimaConsultaMillis",
                "NOMBRE_APLICACION", "planesCreadosEnSesion"}) assertFalse(json.contains(nombre));
        PlanDeportivo leido = repo.listar().get(0);
        assertNull(leido.getNotaPrivada());
        assertEquals(0, leido.getUltimaConsultaMillis());
        assertEquals(antes + 1, PlanDeportivo.getPlanesCreadosEnSesion());
    }

    @Test void duplicadoYActualizacionInvalidaNoAlteranArchivo() throws IOException {
        repo.crear(plan(1));
        String original = Files.readString(archivo);
        assertThrows(IllegalArgumentException.class, () -> repo.crear(plan(1)));
        assertThrows(IllegalArgumentException.class, () -> repo.actualizar(1, "", "Running"));
        assertEquals(original, Files.readString(archivo));
    }

    @Test void idInexistenteProduceErrorSinModificarArchivo() throws IOException {
        repo.crear(plan(1));
        String original = Files.readString(archivo);
        assertThrows(NoSuchElementException.class, () -> repo.buscar(99));
        assertThrows(NoSuchElementException.class, () -> repo.actualizar(99, "Otro", "Running"));
        assertThrows(NoSuchElementException.class, () -> repo.agregarEntrenamiento(99,
                new Entrenamiento("2026-11-16", "Carrera", 30)));
        assertEquals(original, Files.readString(archivo));
    }

    @Test void jsonMalformadoSeConservaAlIntentarCrear() throws IOException {
        Files.writeString(archivo, "{roto");
        assertThrows(IOException.class, () -> repo.crear(plan(1)));
        assertEquals("{roto", Files.readString(archivo));
    }

    @Test void jsonConEstructuraInvalidaSeRechaza() throws IOException {
        for (String json : new String[]{"", "null", "{}", "[null]", "[{\"id\":1}]"}) {
            Files.writeString(archivo, json);
            assertThrows(IOException.class, () -> repo.listar());
        }
        repo = new RepositorioPlanes(temporal.resolve("valido.json"));
        repo.crear(plan(1));
        String p = Files.readString(repo.getArchivo()).trim();
        String objeto = p.substring(1, p.length() - 1);
        Files.writeString(archivo, "[" + objeto + "," + objeto + "]");
        assertThrows(IOException.class, () -> new RepositorioPlanes(archivo).listar());
    }

    @Test void validaDuracionFechaYCamposObligatorios() {
        assertThrows(IllegalArgumentException.class, () -> new Entrenamiento("2026-11-16", "Correr", 0));
        assertThrows(IllegalArgumentException.class, () -> new Entrenamiento("2026-02-30", "Correr", 30));
        assertThrows(IllegalArgumentException.class, () -> new Deportista("", 22));
        assertThrows(IllegalArgumentException.class, () -> new Deportista("Álex", -1));
        assertThrows(IllegalArgumentException.class, () -> plan(0));
    }
}
