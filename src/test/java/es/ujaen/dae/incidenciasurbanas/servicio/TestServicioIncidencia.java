package es.ujaen.dae.incidenciasurbanas.servicio;

import es.ujaen.dae.indicenciasurbanas.entidades.TipoIncidencia;
import es.ujaen.dae.indicenciasurbanas.utils.EstadoIncidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Incidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import es.ujaen.dae.indicenciasurbanas.excepciones.*;
import es.ujaen.dae.indicenciasurbanas.servicios.ServicioIncidencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = es.ujaen.dae.indicenciasurbanas.servicios.ServicioIncidencia.class)
public class TestServicioIncidencia {
    @Autowired
    ServicioIncidencia servicioIncidencia;

    @BeforeEach
    void setUp() {
        servicioIncidencia = new ServicioIncidencia(); // o con mocks si tiene dependencias
    }

    @Test
    @DirtiesContext
    public void testNuevaIncidencia() {

        //Crear Incidencia
        LocalDateTime fecha = LocalDateTime.of(2025,1,1,0,0);
        servicioIncidencia.nuevaIncidencia(fecha, "tipo" , "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");
    }

    @Test
    @DirtiesContext
    public void testNuevoUsuario(){

        //Crear Usuario normal
        LocalDate fecha = LocalDate.of(2000, 1, 1);
        Usuario usuario=new Usuario("nombre", "apellido", fecha, "direccion", 777123456, "email@gmail.com", "clave");
        servicioIncidencia.nuevoUsuario(usuario);

        //Repetir usuario
        assertThatThrownBy(() -> servicioIncidencia.nuevoUsuario(usuario)).isInstanceOf(UsuarioYaRegistrado.class);

        //Introducir nuevo administrador
        Usuario admin = new Usuario("administrador","administrador",
                LocalDate.of(1995,1,1),"-",661030462,"admin.dae@ujaen.es","admin");
        assertThatThrownBy(() -> servicioIncidencia.nuevoUsuario(admin)).isInstanceOf(UsuarioYaRegistrado.class);

    }

    @Test
    @DirtiesContext
    public void testLogin(){
        //Obtener admin
        Optional<Usuario> resultado = servicioIncidencia.login("admin.dae@ujaen.es", "admin");
        assertThat(resultado).isPresent();
        assertThat(resultado.get().email()).isEqualTo("admin.dae@ujaen.es");

        //Obtener usuario que no existe
        resultado = servicioIncidencia.login("email@gmail.com", "clave");
        assertThat(resultado).isEmpty();

        //Obtener usuario que existe
        LocalDate fecha = LocalDate.of(2000, 1, 1);
        Usuario usuario=new Usuario("nombre", "apellido", fecha, "direccion", 777123456, "email@gmail.com", "clave");
        servicioIncidencia.nuevoUsuario(usuario);
        resultado = servicioIncidencia.login("email@gmail.com", "clave");
        assertThat(resultado).isPresent();
        assertThat(resultado.get().email()).isEqualTo("email@gmail.com");

    }

    @Test
    @DirtiesContext
    public void testObtenerIncidenciasUsuario(){
        // Probar que se obtiene la lista de incidencias del usuario correcto
        LocalDateTime fecha = LocalDateTime.now();

        int id1=servicioIncidencia.nuevaIncidencia(fecha, "tipo1", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");
        int id2=servicioIncidencia.nuevaIncidencia(fecha, "tipo2", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "usuario@gmail.com");
        int id3=servicioIncidencia.nuevaIncidencia(fecha, "tipo3","desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");

        LocalDate fechanac = LocalDate.of(2000, 1, 1);

        List<Incidencia> incidencias=servicioIncidencia.obtenerListaIncidenciasUsuario("email@gmail.com");

        assertThat(incidencias).hasSize(2);
        assertThat(incidencias.get(0).id()).isEqualTo(id1);
        assertThat(incidencias.get(1).id()).isEqualTo(id3);

        incidencias=servicioIncidencia.obtenerListaIncidenciasUsuario("usuario@gmail.com");
        assertThat(incidencias).hasSize(1);
        assertThat(incidencias.getFirst().id()).isEqualTo(id2);

        // Probar obtener lista de usuario sin incidencias

        incidencias=servicioIncidencia.obtenerListaIncidenciasUsuario("cliente@gmail.com");
        assertThat(incidencias).hasSize(0);

    }

    @Test
    @DirtiesContext
    public void testBuscarIncidencias(){
        LocalDateTime fecha = LocalDateTime.now();

        int id1=servicioIncidencia.nuevaIncidencia(fecha, "Suciedad","desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");
        int id2=servicioIncidencia.nuevaIncidencia(fecha, "Rotura en parque","desc", "loc", (float) 1.0, (float) 1.0, "dpt", "usuario@gmail.com");
        int id3=servicioIncidencia.nuevaIncidencia(fecha, "Rotura en mobiliario urbano","desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");

        // Probar a buscar por solo tipo
        TipoIncidencia tipo = new TipoIncidencia(0,"Suciedad");
        List<Incidencia> incidencias=servicioIncidencia.buscarIncidenciasTipoEstado(tipo, null);
        assertThat(incidencias).hasSize(1);
        assertThat(incidencias.getFirst().id()).isEqualTo(id1);

        // Probar a buscar por solo estado
        incidencias=servicioIncidencia.buscarIncidenciasTipoEstado(null, EstadoIncidencia.PENDIENTE);
        assertThat(incidencias).hasSize(3);
        assertThat(incidencias.getFirst().id()).isEqualTo(id1);

        // Probar a buscar por tipo y estado
        tipo.nombre("Rotura en parque");
        incidencias=servicioIncidencia.buscarIncidenciasTipoEstado(tipo, EstadoIncidencia.PENDIENTE);
        assertThat(incidencias).hasSize(1);
        assertThat(incidencias.getFirst().id()).isEqualTo(id2);

        // Porbar a buscar sin especificaciones
        incidencias=servicioIncidencia.buscarIncidenciasTipoEstado(null, null);
        assertThat(incidencias).hasSize(3);
        assertThat(incidencias.getFirst().id()).isEqualTo(id1);

    }

    @Test
    @DirtiesContext
    public void testBorrarIncidencia(){
        //Probar a borrar incidencia inexistente
        assertThatThrownBy(() -> servicioIncidencia.borrarIncidencia("admin", 1)).isInstanceOf(IncidenciaNoExiste.class);

        LocalDateTime fecha = LocalDateTime.now();

        int id1=servicioIncidencia.nuevaIncidencia(fecha, "tipo1","desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");
        int id2=servicioIncidencia.nuevaIncidencia(fecha, "tipo2","desc", "loc", (float) 1.0, (float) 1.0, "dpt", "usuario@gmail.com");

        // Probar a borrar como usuario ajeno a la incidencia
        assertThat(servicioIncidencia.borrarIncidencia("usuario@gmail.com",id1)).isEqualTo(false);

        // Probar a borrar como usuario de la incidencia con estado invalido
        servicioIncidencia.modificarEstadoIncidencia("admin", EstadoIncidencia.RESUELTA, id2);
        assertThat(servicioIncidencia.borrarIncidencia("usuario@gmail.com",id2)).isEqualTo(false);

        // Porbar a borrar como usuario de la incidencia con estado valido
        assertThat(servicioIncidencia.borrarIncidencia("email@gmail.com",id1)).isEqualTo(true);

        // Probar a borrar como admin
        assertThat(servicioIncidencia.borrarIncidencia("admin",id2)).isEqualTo(true);

    }

    @Test
    @DirtiesContext
    public void testModificarEstadoIncidencia(){
        //Probar a modificar incidencia inexistente
        assertThatThrownBy(() -> servicioIncidencia.modificarEstadoIncidencia("admin", EstadoIncidencia.EN_EVALUACION,1)).isInstanceOf(IncidenciaNoExiste.class);

        LocalDateTime fecha = LocalDateTime.now();
        //Incidencia incidencia1=new Incidencia(1,fecha, servicioIncidencia.obtenerTipoIncidencias().get(0), "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");

        int id=servicioIncidencia.nuevaIncidencia(fecha, "tipo", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "cliente@gmail.com");

        // Probar a modificar como admin
        servicioIncidencia.modificarEstadoIncidencia("admin", EstadoIncidencia.EN_EVALUACION,id);
        assertThat(servicioIncidencia.buscarIncidenciasTipoEstado(null, EstadoIncidencia.EN_EVALUACION)).hasSize(1);
        assertThat(servicioIncidencia.buscarIncidenciasTipoEstado(null, EstadoIncidencia.PENDIENTE)).hasSize(0);

        // Porbar a modificar como usuario normal
        assertThatThrownBy(() -> servicioIncidencia.modificarEstadoIncidencia("usuario@gmail.com", EstadoIncidencia.RESUELTA, id)).isInstanceOf(AccionNoAutorizada.class);
    }

    @Test
    @DirtiesContext
    public void testCrearTipoIncidencia(){
        // Probar a crear como admin
        servicioIncidencia.crearTipoIncidencia("admin", "nuevoTipoIncidencia");

        // Probar a crear tipo de incidencia ya existente
        assertThatThrownBy(() -> servicioIncidencia.crearTipoIncidencia("admin", "nuevoTipoIncidencia")).isInstanceOf(TipoIncidenciaExiste.class);

        // Probar a crear como usuario normal
        assertThatThrownBy(() -> servicioIncidencia.crearTipoIncidencia("usuario@gmail.com", "crearTipoIncidencia")).isInstanceOf(AccionNoAutorizada.class);

    }

    @Test
    @DirtiesContext
    public void testBorrarTipoIncidencia(){
        LocalDateTime fecha = LocalDateTime.now();

        servicioIncidencia.nuevaIncidencia(fecha, "Suciedad", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");
        servicioIncidencia.nuevaIncidencia(fecha, "Rotura en mobiliario urbano", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "usuario@gmail.com");

        // Probar a borrar como admin con incidencias de ese tipo
        TipoIncidencia tipo = new TipoIncidencia(0,"Suciedad");
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia("admin", tipo)).isInstanceOf(TipoIncidenciaEnUso.class);

        // Probar a borrar como admin sin incidencias de ese tipo
        tipo.nombre("Rotura en parque");
        servicioIncidencia.borrarTipoIncidencia("admin", tipo);

        // Probar a borrar tipo de incidencia inexistente
        tipo.nombre("Objeto extraviado");
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia("admin", tipo)).isInstanceOf(TipoIncidenciaNoExiste.class);

        // Probar a borrar como usuario normal
        tipo.nombre("Suciedad");
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia("usuario@gmail.com", tipo)).isInstanceOf(AccionNoAutorizada.class);
    }

}
