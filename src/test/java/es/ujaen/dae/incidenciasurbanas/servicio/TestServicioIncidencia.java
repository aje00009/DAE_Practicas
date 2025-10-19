package es.ujaen.dae.incidenciasurbanas.servicio;

import es.ujaen.dae.indicenciasurbanas.entidades.EstadoIncidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Incidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import es.ujaen.dae.indicenciasurbanas.excepciones.*;
import es.ujaen.dae.indicenciasurbanas.servicios.ServicioIncidencia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;


import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = es.ujaen.dae.indicenciasurbanas.servicios.ServicioIncidencia.class)
public class TestServicioIncidencia {

    @Autowired
    ServicioIncidencia servicioIncidencia;

    @Test
    @DirtiesContext
    public void testNuevaIncidencia() {

        //Crear Incidencia
        LocalDateTime fecha = LocalDateTime.now();
        servicioIncidencia.nuevaIncidencia(fecha, "tipo", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");

        //Repetición de incidencia
        assertThatThrownBy(() -> servicioIncidencia.nuevaIncidencia(fecha, "tipo", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com")).isInstanceOf(IncidenciaYaRegistrada.class);
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
        resultado = servicioIncidencia.login("usuario@ujaen.es", "usuario");
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
        servicioIncidencia.nuevaIncidencia(fecha, "tipo", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");
        servicioIncidencia.nuevaIncidencia(fecha, "tipo", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "usuario@gmail.com");
        servicioIncidencia.nuevaIncidencia(fecha, "tipo", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");

        LocalDate fechanac = LocalDate.of(2000, 1, 1);
        servicioIncidencia.nuevoUsuario(new Usuario("nombre", "apellido", fechanac, "direccion", 777123456, "usuario@gmail.com", "clave"));
        servicioIncidencia.nuevoUsuario(new Usuario("nombre", "apellido", fechanac, "direccion", 777123456, "email@gmail.com", "clave"));

        List<Incidencia> incidencias=servicioIncidencia.obtenerListaIncidenciasUsuario("email@gmail.com");

        assertThat(incidencias).hasSize(2);
        assertThat(incidencias.get(0).id()).isEqualTo(1);
        assertThat(incidencias.get(1).id()).isEqualTo(3);

    }

    @Test
    @DirtiesContext
    public void testBuscarIncidencias(){
        LocalDateTime fecha = LocalDateTime.now();
        servicioIncidencia.nuevaIncidencia(fecha, "Suciedad", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");
        servicioIncidencia.nuevaIncidencia(fecha, "Rotura en parque", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "usuario@gmail.com");
        servicioIncidencia.nuevaIncidencia(fecha, "Rotura en mobiliario urbano", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");

        // Probar a buscar por solo tipo
        List<Incidencia> incidencias=servicioIncidencia.buscarIncidenciasTipoEstado("Suciedad", null);

        assertThat(incidencias).hasSize(1);
        assertThat(incidencias.get(0).id()).isEqualTo(1);

        // Porbar a buscar por solo estado
        incidencias=servicioIncidencia.buscarIncidenciasTipoEstado(null, EstadoIncidencia.PENDIENTE);

        assertThat(incidencias).hasSize(3);
        assertThat(incidencias.get(0).id()).isEqualTo(1);

        // Probar a buscar por tipo y estado
        incidencias=servicioIncidencia.buscarIncidenciasTipoEstado("Rotura en parque", EstadoIncidencia.PENDIENTE);

        assertThat(incidencias).hasSize(1);
        assertThat(incidencias.get(0).id()).isEqualTo(2);

        // Porbar a buscar sin especificaciones
        incidencias=servicioIncidencia.buscarIncidenciasTipoEstado(null, null);

        assertThat(incidencias).hasSize(3);
        assertThat(incidencias.get(0).id()).isEqualTo(1);

    }

    @Test
    @DirtiesContext
    public void testBorrarIncidencia(){
        //Probar a borrar incidencia inexistente
        assertThatThrownBy(() -> servicioIncidencia.borrarIncidencia("admin", 1)).isInstanceOf(IncidenciaNoExiste.class);

        LocalDateTime fecha = LocalDateTime.now();
        servicioIncidencia.nuevaIncidencia(fecha, "Suciedad", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");
        servicioIncidencia.nuevaIncidencia(fecha, "Rotura en parque", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "usuario@gmail.com");
        servicioIncidencia.nuevaIncidencia(fecha, "Rotura en mobiliario urbano", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");

        // Probar a borrar como usuario ajeno a la incidencia
        assertThat(servicioIncidencia.borrarIncidencia("usuario@gmail.com",1)).isEqualTo(false);

        // Probar a borrar como usuario de la incidencia con estado invalido
        assertThat(servicioIncidencia.borrarIncidencia("email@gmail.com",1)).isEqualTo(false);

        // Porbar a borrar como usuario de la incidencia con estado valido
        servicioIncidencia.modificarEstadoIncidencia("admin", EstadoIncidencia.RESUELTA, 1);
        assertThat(servicioIncidencia.borrarIncidencia("email@gmail.com",1)).isEqualTo(true);

        // Probar a borrar como admin
        assertThat(servicioIncidencia.borrarIncidencia("admin",2)).isEqualTo(true);

    }

    @Test
    @DirtiesContext
    public void testModificarEstadoIncidencia(){
        //Probar a modificar incidencia inexistente
        assertThatThrownBy(() -> servicioIncidencia.modificarEstadoIncidencia("admin", EstadoIncidencia.EN_EVALUACION,1)).isInstanceOf(IncidenciaNoExiste.class);

        LocalDateTime fecha = LocalDateTime.now();
        servicioIncidencia.nuevaIncidencia(fecha, "Suciedad", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");

        // Probar a modificar como admin
        servicioIncidencia.modificarEstadoIncidencia("admin", EstadoIncidencia.EN_EVALUACION,1);
        assertThat(servicioIncidencia.buscarIncidenciasTipoEstado("", EstadoIncidencia.EN_EVALUACION)).hasSize(1);
        assertThat(servicioIncidencia.buscarIncidenciasTipoEstado("", EstadoIncidencia.PENDIENTE)).hasSize(0);

        // Porbar a modificar como usuario normal
        assertThatThrownBy(() -> servicioIncidencia.modificarEstadoIncidencia("usuario@gmail.com", EstadoIncidencia.RESUELTA, 1)).isInstanceOf(AccionNoAutorizada.class);
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
        servicioIncidencia.nuevaIncidencia(fecha, "Rotura en mobiliario urbano", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", "email@gmail.com");

        // Probar a borrar como admin con incidencias de ese tipo
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia("admin", "Suciedad")).isInstanceOf(TipoIncidenciaEnUso.class);

        // Probar a borrar como admin sin incidencias de ese tipo
        servicioIncidencia.borrarTipoIncidencia("admin", "Rotura en parque");

        // Probar a borrar tipo de incidencia inexistente
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia("admin", "tipoIncidencia")).isInstanceOf(TipoIncidenciaNoExiste.class);

        // Probar a borrar como usuario normal
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia("usuario@gmail.com", "Rotura en parque")).isInstanceOf(AccionNoAutorizada.class);
    }

}
