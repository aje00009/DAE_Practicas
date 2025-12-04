package es.ujaen.dae.incidenciasurbanas.servicio;

import es.ujaen.dae.indicenciasurbanas.entidades.TipoIncidencia;
import es.ujaen.dae.indicenciasurbanas.utils.EstadoIncidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Incidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import es.ujaen.dae.indicenciasurbanas.excepciones.*;
import es.ujaen.dae.indicenciasurbanas.servicios.ServicioIncidencia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = es.ujaen.dae.indicenciasurbanas.app.DaePracticaApplication.class)
@ActiveProfiles("test")
public class TestServicioIncidencia {
    @Autowired
    ServicioIncidencia servicioIncidencia;

    @Test
    @DirtiesContext
    public void testNuevoUsuario() {

        //Crear Usuario normal
        LocalDate fecha = LocalDate.of(2000, 1, 1);
        Usuario usuario = new Usuario("nombre", "apellido", fecha, "direccion", "+34777123456", "email@gmail.com", "clave");
        servicioIncidencia.nuevoUsuario(usuario);

        //Repetir usuario
        assertThatThrownBy(() -> servicioIncidencia.nuevoUsuario(usuario)).isInstanceOf(UsuarioYaRegistrado.class);

        //Introducir nuevo administrador
        Usuario admin = new Usuario("administrador", "administrador",
                LocalDate.of(1995, 1, 1), "-", "+34661030462", "admin.dae@ujaen.es", "admin");
        assertThatThrownBy(() -> servicioIncidencia.nuevoUsuario(admin)).isInstanceOf(UsuarioYaRegistrado.class);

    }
    @Test
    @DirtiesContext
    public void testObtenerUsuario(){
        //Obtener admin
        Optional<Usuario> resultado = servicioIncidencia.obtenerUsuario("admin.dae@ujaen.es");
        assertThat(resultado).isPresent();
        assertThat(resultado.get().email()).isEqualTo("admin.dae@ujaen.es");

        //Obtener usuario que no existe
        resultado = servicioIncidencia.obtenerUsuario("email@gmail.com");
        assertThat(resultado).isEmpty();

        //Obtener usuario que existe
        LocalDate fecha = LocalDate.of(2000, 1, 1);
        Usuario usuario=new Usuario("nombre", "apellido", fecha, "direccion", "+34777123456", "email@gmail.com", "clave");
        servicioIncidencia.nuevoUsuario(usuario);
        resultado = servicioIncidencia.obtenerUsuario(usuario.email());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().email()).isEqualTo("email@gmail.com");

    }

    @Test
    @DirtiesContext
    public void testNuevaIncidencia (){
        //Optional<Usuario> resultado = servicioIncidencia.obtenerUsuario("admin.dae@ujaen.es");
        servicioIncidencia.crearTipoIncidencia("Tipo");
        TipoIncidencia tipoIncidencia = servicioIncidencia.obtenerTipoIncidencia("Tipo").get();

        LocalDate fecha = LocalDate.of(2000, 1, 1);
        Usuario usuario=new Usuario("nombre", "apellido", fecha, "direccion", "+34777123456", "email@gmail.com", "clave");
        servicioIncidencia.nuevoUsuario(usuario);
        Optional<Usuario> resultado = servicioIncidencia.obtenerUsuario(usuario.email());

        //Comprobar que se registra correctamente la incidencia
        LocalDateTime fecha1 = LocalDateTime.now();
        Incidencia incidencia=servicioIncidencia.nuevaIncidencia(fecha1, tipoIncidencia ,"desc", "loc", (float) 40.416775, (float) -3.703790, "dpt", resultado.get(),"imagen.jpg".getBytes());

        assertThat(servicioIncidencia.obtenerListaIncidenciasUsuario(resultado.get())).hasSize(1);

        Usuario user = resultado.get();
        assertThatThrownBy(() -> servicioIncidencia.nuevaIncidencia(fecha1, tipoIncidencia ,"desc", "loc", (float) 40.41681991555875, (float) -3.703731005258922, "dpt", user,"imagen.jpg".getBytes())).isInstanceOf(IncidenciaEnCurso.class);
    }

    @Test
    @DirtiesContext
    public void testObtenerIncidenciasUsuario(){
        // Probar que se obtiene la lista de incidencias del usuario correcto
        ///Optional<Usuario> resultado = servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        servicioIncidencia.crearTipoIncidencia("tipo1");
        TipoIncidencia tipo1 = servicioIncidencia.obtenerTipoIncidencia("tipo1").get();

        servicioIncidencia.crearTipoIncidencia("tipo2");
        TipoIncidencia tipo2 = servicioIncidencia.obtenerTipoIncidencia("tipo2").get();

        servicioIncidencia.crearTipoIncidencia("tipo3");
        TipoIncidencia tipo3 = servicioIncidencia.obtenerTipoIncidencia("tipo3").get();

        LocalDateTime fecha = LocalDateTime.now();

        Usuario user = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10","+34673826467","aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user);

        Usuario user1 = new Usuario("Carlos","Mayor Navarro",LocalDate.now(),"Av. Arjona 10","+34673826467","cmn00019@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user1);

        Optional<Usuario> opt = servicioIncidencia.obtenerUsuario(user.email());

        Incidencia i1=servicioIncidencia.nuevaIncidencia(fecha, tipo1, "desc", "loc", (float) 12903, (float) -1231, "dpt", opt.get(),"imagen.jpg".getBytes());

        Optional<Usuario> opt1 = servicioIncidencia.obtenerUsuario(user1.email());
        Incidencia i2=servicioIncidencia.nuevaIncidencia(fecha,tipo2 ,"desc", "loc", (float) 10, (float) 10, "dpt", opt1.get(),"imagen.jpg".getBytes());

        Incidencia i3=servicioIncidencia.nuevaIncidencia(fecha, tipo3,"desc", "loc", (float) 9921, (float) -9912, "dpt", opt1.get(),"imagen.jpg".getBytes());

        List<Incidencia> incidencias=servicioIncidencia.obtenerListaIncidenciasUsuario(opt.get());

        assertThat(incidencias).hasSize(1);
        assertThat(incidencias.getFirst()).isEqualTo(i1);

        incidencias=servicioIncidencia.obtenerListaIncidenciasUsuario(opt1.get());
        assertThat(incidencias).hasSize(2);
        assertThat(incidencias.getFirst()).isEqualTo(i2);
        assertThat(incidencias.get(1)).isEqualTo(i3);

        // Probar obtener lista de usuario sin incidencias

        Usuario user2 = new Usuario("Ximena","Galmades",LocalDate.now(),"Av. Arjona 10","+34673826467","xmnn10@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user2);

        Optional<Usuario> opt2 = servicioIncidencia.obtenerUsuario(user2.email());
        incidencias=servicioIncidencia.obtenerListaIncidenciasUsuario(opt2.get());
        assertThat(incidencias).hasSize(0);

    }

    @Test
    @DirtiesContext
    public void testBuscarIncidencias(){
        //Optional<Usuario> resultado = servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        servicioIncidencia.crearTipoIncidencia("Suciedad");
        TipoIncidencia suciedad = servicioIncidencia.obtenerTipoIncidencia("Suciedad").get();

        servicioIncidencia.crearTipoIncidencia("Rotura en parque");
        TipoIncidencia roturaEnParque = servicioIncidencia.obtenerTipoIncidencia("Rotura en parque").get();

        servicioIncidencia.crearTipoIncidencia("Rotura en mobiliario urbano");
        TipoIncidencia roturaEnMobiliarioUrbano = servicioIncidencia.obtenerTipoIncidencia("Rotura en mobiliario urbano").get();

        LocalDateTime fecha = LocalDateTime.now();

        Usuario user = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10","+34673826467","aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user);

        Usuario user1 = new Usuario("Carlos","Mayor Navarro",LocalDate.now(),"Av. Arjona 10","+34673826467","cmn00019@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user1);

        Optional<Usuario> opt = servicioIncidencia.obtenerUsuario(user.email());
        Incidencia i1=servicioIncidencia.nuevaIncidencia(fecha, suciedad,"desc", "loc", (float) 20, (float) -50, "dpt", opt.get(),"imagen.jpg".getBytes());

        Optional<Usuario> opt1 = servicioIncidencia.obtenerUsuario(user1.email());
        Incidencia i2=servicioIncidencia.nuevaIncidencia(fecha, roturaEnParque,"desc", "loc", (float) 10, (float) 10, "dpt", opt1.get(),"imagen.jpg".getBytes());
        Incidencia i3=servicioIncidencia.nuevaIncidencia(fecha, roturaEnMobiliarioUrbano,"desc", "loc", (float) 50, (float) -50, "dpt", opt.get(),"imagen.jpg".getBytes());

        // Probar a buscar por solo tipo
        List<Incidencia> incidencias=servicioIncidencia.buscarIncidenciasTipoEstado(suciedad, null);
        assertThat(incidencias).hasSize(1);
        assertThat(incidencias.getFirst()).isEqualTo(i1);

        // Probar a buscar por solo estado
        incidencias=servicioIncidencia.buscarIncidenciasTipoEstado(null, EstadoIncidencia.PENDIENTE);
        assertThat(incidencias).hasSize(3);
        assertThat(incidencias.getFirst()).isEqualTo(i1);

        // Probar a buscar por tipo y estado
        incidencias=servicioIncidencia.buscarIncidenciasTipoEstado(roturaEnParque, EstadoIncidencia.PENDIENTE);
        assertThat(incidencias).hasSize(1);
        assertThat(incidencias.getFirst()).isEqualTo(i2);

        // Probar a buscar sin especificaciones
        incidencias=servicioIncidencia.buscarIncidenciasTipoEstado(null, null);
        assertThat(incidencias).hasSize(3);
        assertThat(incidencias.getFirst()).isEqualTo(i1);

    }

    @Test
    @DirtiesContext
    public void testBorrarIncidencia(){
        //Probar a borrar incidencia inexistente

        //Optional<Usuario> resultado = servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        servicioIncidencia.crearTipoIncidencia("Suciedad");
        TipoIncidencia tipo1 = servicioIncidencia.obtenerTipoIncidencia("Suciedad").get();

        servicioIncidencia.crearTipoIncidencia("Rotura en parque");
        TipoIncidencia tipo2 = servicioIncidencia.obtenerTipoIncidencia("Rotura en parque").get();

        Incidencia incidencia = new Incidencia();

        assertThatThrownBy(() -> servicioIncidencia.borrarIncidencia(incidencia.id())).isInstanceOf(IncidenciaNoExiste.class);

        LocalDateTime fecha = LocalDateTime.now();

        Usuario usuario1 = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10","+34673826467","aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(usuario1);

        Optional<Usuario> user1=servicioIncidencia.obtenerUsuario(usuario1.email());

        Usuario usuario2 = new Usuario("Carlos","Mayor Navarro",LocalDate.now(),"Av. Arjona 10","+34673826467","cmn00019@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(usuario2);

        Optional<Usuario> user2=servicioIncidencia.obtenerUsuario(usuario2.email());

        Incidencia incidencia1=servicioIncidencia.nuevaIncidencia(fecha, tipo1,"desc", "loc", (float) 9921, (float) -9912, "dpt", user1.get(),"imagen.jpg".getBytes());
        Incidencia incidencia2=servicioIncidencia.nuevaIncidencia(fecha, tipo2,"desc", "loc", (float) 1.0, (float) 1.0, "dpt", user2.get(),"imagen.jpg".getBytes());

        // Probar a borrar como usuario ajeno a la incidencia
        /* No se puede comprobar el usuario que ejecuta la accion hasta mas adelante
        assertThat(servicioIncidencia.borrarIncidencia(incidencia1.id())).isEqualTo(false);

        // Probar a borrar como usuario de la incidencia con estado invalido
        servicioIncidencia.modificarEstadoIncidencia(incidencia2.id(), EstadoIncidencia.RESUELTA);
        assertThat(servicioIncidencia.borrarIncidencia(incidencia2.id())).isEqualTo(false);

        // Probar a borrar como usuario de la incidencia con estado valido
        assertThat(servicioIncidencia.borrarIncidencia(incidencia1.id())).isEqualTo(true);

        // Probar a borrar como admin
        assertThat(servicioIncidencia.borrarIncidencia(incidencia2.id())).isEqualTo(true);*/

    }

    @Test
    @DirtiesContext
    public void testModificarEstadoIncidencia(){
        //Probar a modificar incidencia inexistente
        //Optional<Usuario> admin=servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        servicioIncidencia.crearTipoIncidencia("tipo1");
        TipoIncidencia tipo = servicioIncidencia.obtenerTipoIncidencia("tipo1").get();

        Incidencia incidencia1 = new Incidencia();

        assertThatThrownBy(() -> servicioIncidencia.modificarEstadoIncidencia(incidencia1.id(), EstadoIncidencia.EN_EVALUACION)).isInstanceOf(IncidenciaNoExiste.class);

        LocalDateTime fecha = LocalDateTime.now();
        Usuario usuario1 = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10","+34673826467","aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(usuario1);

        Optional<Usuario> user1=servicioIncidencia.obtenerUsuario(usuario1.email());

        Incidencia incidencia2=servicioIncidencia.nuevaIncidencia(fecha, tipo, "desc", "loc", (float) 20.0, (float) 30.0, "dpt", user1.get(),"imagen.jpg".getBytes());

        // Probar a modificar como admin
        servicioIncidencia.modificarEstadoIncidencia(incidencia2.id(), EstadoIncidencia.EN_EVALUACION);
        assertThat(servicioIncidencia.buscarIncidenciasTipoEstado(null, EstadoIncidencia.EN_EVALUACION)).hasSize(1);
        assertThat(servicioIncidencia.buscarIncidenciasTipoEstado(null, EstadoIncidencia.PENDIENTE)).hasSize(0);

        // Porbar a modificar como usuario normal
        assertThatThrownBy(() -> servicioIncidencia.modificarEstadoIncidencia(incidencia2.id(), EstadoIncidencia.RESUELTA)).isInstanceOf(AccionNoAutorizada.class);
    }

    @Test
    @DirtiesContext
    public void testCrearTipoIncidencia(){
        // Probar a crear como admin
        //Optional<Usuario> admin=servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        servicioIncidencia.crearTipoIncidencia("nuevoTipoIncidencia");
        TipoIncidencia tipo = servicioIncidencia.obtenerTipoIncidencia("nuevoTipoIncidencia").get();

        // Probar a crear tipo de incidencia ya existente
        assertThatThrownBy(() -> servicioIncidencia.crearTipoIncidencia(tipo.nombre())).isInstanceOf(TipoIncidenciaExiste.class);

        // Probar a crear como usuario normal
        Usuario usuario1 = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10","+34673826467","aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(usuario1);

        Optional<Usuario> user1=servicioIncidencia.obtenerUsuario(usuario1.email());

        assertThatThrownBy(() -> servicioIncidencia.crearTipoIncidencia("tipo2")).isInstanceOf(AccionNoAutorizada.class);

    }

    @Test
    @DirtiesContext
    public void testBorrarTipoIncidencia(){
        LocalDateTime fecha = LocalDateTime.now();

        //Optional<Usuario> resultado = servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        servicioIncidencia.crearTipoIncidencia("Suciedad");
        TipoIncidencia suciedad = servicioIncidencia.obtenerTipoIncidencia("Suciedad").get();

        servicioIncidencia.crearTipoIncidencia("Rotura en parque");
        TipoIncidencia roturaEnParque = servicioIncidencia.obtenerTipoIncidencia("Rotura en parque").get();

        servicioIncidencia.crearTipoIncidencia("Rotura en mobiliario urbano");
        TipoIncidencia roturaEnMobiliarioUrbano = servicioIncidencia.obtenerTipoIncidencia("Rotura en mobiliario urbano").get();

        Usuario user = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10","+34673826467","aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user);

        Usuario user1 = new Usuario("Carlos","Mayor Navarro",LocalDate.now(),"Av. Arjona 10","+34673826467","cmn00019@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user1);

        Optional<Usuario> opt = servicioIncidencia.obtenerUsuario(user.email());

        servicioIncidencia.nuevaIncidencia(fecha, suciedad, "desc", "loc", (float) 12903, (float) -1231, "dpt", opt.get(),"imagen.jpg".getBytes());

        Optional<Usuario> opt1 = servicioIncidencia.obtenerUsuario(user1.email());

        servicioIncidencia.nuevaIncidencia(fecha, roturaEnParque, "desc", "loc", (float) 1.0, (float) 50.0, "dpt", opt1.get(),"imagen.jpg".getBytes());

        Optional<Usuario> opt2 = servicioIncidencia.obtenerUsuario("admin.dae@ujaen.es");

        // Probar a borrar como admin con incidencias en us;
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia(suciedad.nombre())).isInstanceOf(TipoIncidenciaEnUso.class);
        assertThat(servicioIncidencia.obtenerTiposIncidencia()).hasSize(3);

        // Probar a borrar tipo de incidencia inexistente
        TipoIncidencia noExiste = new TipoIncidencia("Pepito");
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia(noExiste.nombre())).isInstanceOf(TipoIncidenciaNoExiste.class);
        assertThat(servicioIncidencia.obtenerTiposIncidencia()).hasSize(3);

        // Probar a borrar como usuario normal
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia(suciedad.nombre())).isInstanceOf(AccionNoAutorizada.class);
        assertThat(servicioIncidencia.obtenerTiposIncidencia()).hasSize(3);

        // Probar a borrar como admin incidencia sin uso
        servicioIncidencia.borrarTipoIncidencia(roturaEnMobiliarioUrbano.nombre());
        assertThat(servicioIncidencia.obtenerTiposIncidencia()).hasSize(2); //Hemos borrado un tipo de incidencia, debería haber uno menos
    }

    @Test
    @DirtiesContext
    public void testobtenerTipoIncidencia(){
        //Optional<Usuario> resultado = servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        //Comprobar que el tipo de incidencia recien registrado existe
        servicioIncidencia.crearTipoIncidencia("Residuos tóxicos");
        assertThat(servicioIncidencia.obtenerTipoIncidencia("Residuos tóxicos").isPresent()).isTrue();

        //Comprobar un tipo de incidencia no existente
        assertThat(servicioIncidencia.obtenerTipoIncidencia("Pepito").isEmpty()).isTrue();
    }

    @Test
    @DirtiesContext
    public void testObtenerTiposIncidencia(){
        //Optional<Usuario> resultado = servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        //Comprobar que devuelve en principio una lista vacio
        assertThat(servicioIncidencia.obtenerTiposIncidencia()).hasSize(0);

        //Comprobar que devuelve la lista de todos los tipos de incidencias
        servicioIncidencia.crearTipoIncidencia("Tipo1");
        servicioIncidencia.crearTipoIncidencia("Tipo2");
        servicioIncidencia.crearTipoIncidencia("Tipo3");

        assertThat(servicioIncidencia.obtenerTiposIncidencia()).hasSize(3);
    }
}
