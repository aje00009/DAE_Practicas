package es.ujaen.dae.incidenciasurbanas.servicio;

import es.ujaen.dae.indicenciasurbanas.entidades.TipoIncidencia;
import es.ujaen.dae.indicenciasurbanas.utils.EstadoIncidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Incidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import es.ujaen.dae.indicenciasurbanas.excepciones.*;
import es.ujaen.dae.indicenciasurbanas.servicios.ServicioIncidencia;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    EntityManager em;

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
        Usuario usuario=new Usuario("nombre", "apellido", fecha, "direccion", "+34777123456", "email@gmail.com", "clave");
        servicioIncidencia.nuevoUsuario(usuario);
        resultado = servicioIncidencia.login(usuario.email(), usuario.clave());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().email()).isEqualTo("email@gmail.com");

    }

    @Test
    @DirtiesContext
    public void testObtenerIncidenciasUsuario(){
        // Probar que se obtiene la lista de incidencias del usuario correcto
        Optional<Usuario> resultado = servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        servicioIncidencia.crearTipoIncidencia(resultado.get(),"tipo1");
        TipoIncidencia tipo1 = servicioIncidencia.obtenerTipoIncidencia("tipo1").get();

        servicioIncidencia.crearTipoIncidencia(resultado.get(),"tipo2");
        TipoIncidencia tipo2 = servicioIncidencia.obtenerTipoIncidencia("tipo2").get();

        servicioIncidencia.crearTipoIncidencia(resultado.get(),"tipo3");
        TipoIncidencia tipo3 = servicioIncidencia.obtenerTipoIncidencia("tipo3").get();

        LocalDateTime fecha = LocalDateTime.now();

        Usuario user = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10","+34673826467","aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user);

        Usuario user1 = new Usuario("Carlos","Mayor Navarro",LocalDate.now(),"Av. Arjona 10","+34673826467","cmn00019@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user1);

        Optional<Usuario> opt = servicioIncidencia.login(user.email(),user.clave());

        Incidencia i1=servicioIncidencia.nuevaIncidencia(fecha, tipo1, "desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt.get());

        Optional<Usuario> opt1 = servicioIncidencia.login(user1.email(),user1.clave());
        Incidencia i2=servicioIncidencia.nuevaIncidencia(fecha,tipo2 ,"desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt1.get());

        Incidencia i3=servicioIncidencia.nuevaIncidencia(fecha, tipo3,"desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt1.get());

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

        Optional<Usuario> opt2 = servicioIncidencia.login(user2.email(),user2.clave());
        incidencias=servicioIncidencia.obtenerListaIncidenciasUsuario(opt2.get());
        assertThat(incidencias).hasSize(0);

    }

    @Test
    @DirtiesContext
    public void testBuscarIncidencias(){
        Optional<Usuario> resultado = servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        servicioIncidencia.crearTipoIncidencia(resultado.get(),"Suciedad");
        TipoIncidencia suciedad = servicioIncidencia.obtenerTipoIncidencia("Suciedad").get();

        servicioIncidencia.crearTipoIncidencia(resultado.get(),"Rotura en parque");
        TipoIncidencia roturaEnParque = servicioIncidencia.obtenerTipoIncidencia("Rotura en parque").get();

        servicioIncidencia.crearTipoIncidencia(resultado.get(),"Rotura en mobiliario urbano");
        TipoIncidencia roturaEnMobiliarioUrbano = servicioIncidencia.obtenerTipoIncidencia("Rotura en mobiliario urbano").get();

        LocalDateTime fecha = LocalDateTime.now();

        Usuario user = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10","+34673826467","aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user);

        Usuario user1 = new Usuario("Carlos","Mayor Navarro",LocalDate.now(),"Av. Arjona 10","+34673826467","cmn00019@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user1);

        Optional<Usuario> opt = servicioIncidencia.login(user.email(),user.clave());
        Incidencia i1=servicioIncidencia.nuevaIncidencia(fecha, suciedad,"desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt.get());

        Optional<Usuario> opt1 = servicioIncidencia.login(user1.email(),user1.clave());
        Incidencia i2=servicioIncidencia.nuevaIncidencia(fecha, roturaEnParque,"desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt1.get());
        Incidencia i3=servicioIncidencia.nuevaIncidencia(fecha, roturaEnMobiliarioUrbano,"desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt.get());

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

        Optional<Usuario> resultado = servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        servicioIncidencia.crearTipoIncidencia(resultado.get(),"Suciedad");
        TipoIncidencia tipo1 = servicioIncidencia.obtenerTipoIncidencia("Suciedad").get();

        servicioIncidencia.crearTipoIncidencia(resultado.get(),"Rotura en parque");
        TipoIncidencia tipo2 = servicioIncidencia.obtenerTipoIncidencia("Rotura en parque").get();

        Incidencia incidencia = new Incidencia();

        assertThatThrownBy(() -> servicioIncidencia.borrarIncidencia(resultado.get(), incidencia)).isInstanceOf(IncidenciaNoExiste.class);

        LocalDateTime fecha = LocalDateTime.now();

        Usuario usuario1 = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10","+34673826467","aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(usuario1);

        Optional<Usuario> user1=servicioIncidencia.login(usuario1.email(), usuario1.clave());

        Usuario usuario2 = new Usuario("Carlos","Mayor Navarro",LocalDate.now(),"Av. Arjona 10","+34673826467","cmn00019@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(usuario2);

        Optional<Usuario> user2=servicioIncidencia.login(usuario2.email(), usuario2.clave());

        Incidencia incidencia1=servicioIncidencia.nuevaIncidencia(fecha, tipo1,"desc", "loc", (float) 1.0, (float) 1.0, "dpt", user1.get());
        Incidencia incidencia2=servicioIncidencia.nuevaIncidencia(fecha, tipo2,"desc", "loc", (float) 1.0, (float) 1.0, "dpt", user2.get());

        // Probar a borrar como usuario ajeno a la incidencia
        assertThat(servicioIncidencia.borrarIncidencia(user2.get(),incidencia1)).isEqualTo(false);

        // Probar a borrar como usuario de la incidencia con estado invalido
        servicioIncidencia.modificarEstadoIncidencia(resultado.get(), EstadoIncidencia.RESUELTA, incidencia2);
        assertThat(servicioIncidencia.borrarIncidencia(user1.get(),incidencia2)).isEqualTo(false);

        // Probar a borrar como usuario de la incidencia con estado valido
        assertThat(servicioIncidencia.borrarIncidencia(user1.get(),incidencia1)).isEqualTo(true);

        // Probar a borrar como admin
        assertThat(servicioIncidencia.borrarIncidencia(resultado.get(),incidencia2)).isEqualTo(true);

    }

    @Test
    @DirtiesContext
    public void testModificarEstadoIncidencia(){
        //Probar a modificar incidencia inexistente
        Optional<Usuario> admin=servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        servicioIncidencia.crearTipoIncidencia(admin.get(),"tipo1");
        TipoIncidencia tipo = servicioIncidencia.obtenerTipoIncidencia("tipo1").get();

        Incidencia incidencia1 = new Incidencia();

        assertThatThrownBy(() -> servicioIncidencia.modificarEstadoIncidencia(admin.get(), EstadoIncidencia.EN_EVALUACION,incidencia1)).isInstanceOf(IncidenciaNoExiste.class);

        LocalDateTime fecha = LocalDateTime.now();
        Usuario usuario1 = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10","+34673826467","aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(usuario1);

        Optional<Usuario> user1=servicioIncidencia.login(usuario1.email(), usuario1.clave());

        Incidencia incidencia2=servicioIncidencia.nuevaIncidencia(fecha, tipo, "desc", "loc", (float) 1.0, (float) 1.0, "dpt", user1.get());

        // Probar a modificar como admin
        servicioIncidencia.modificarEstadoIncidencia(admin.get(), EstadoIncidencia.EN_EVALUACION,incidencia2);
        assertThat(servicioIncidencia.buscarIncidenciasTipoEstado(null, EstadoIncidencia.EN_EVALUACION)).hasSize(1);
        assertThat(servicioIncidencia.buscarIncidenciasTipoEstado(null, EstadoIncidencia.PENDIENTE)).hasSize(0);

        // Porbar a modificar como usuario normal
        assertThatThrownBy(() -> servicioIncidencia.modificarEstadoIncidencia(user1.get(), EstadoIncidencia.RESUELTA, incidencia2)).isInstanceOf(AccionNoAutorizada.class);
    }

    @Test
    @DirtiesContext
    public void testCrearTipoIncidencia(){
        // Probar a crear como admin
        Optional<Usuario> admin=servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        servicioIncidencia.crearTipoIncidencia(admin.get(), "nuevoTipoIncidencia");
        TipoIncidencia tipo = servicioIncidencia.obtenerTipoIncidencia("nuevoTipoIncidencia").get();

        // Probar a crear tipo de incidencia ya existente
        assertThatThrownBy(() -> servicioIncidencia.crearTipoIncidencia(admin.get(), tipo.nombre())).isInstanceOf(TipoIncidenciaExiste.class);

        // Probar a crear como usuario normal
        Usuario usuario1 = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10","+34673826467","aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(usuario1);

        Optional<Usuario> user1=servicioIncidencia.login(usuario1.email(), usuario1.clave());

        assertThatThrownBy(() -> servicioIncidencia.crearTipoIncidencia(user1.get(), "tipo2")).isInstanceOf(AccionNoAutorizada.class);

    }

    @Test
    @DirtiesContext
    public void testBorrarTipoIncidencia(){
        LocalDateTime fecha = LocalDateTime.now();

        Optional<Usuario> resultado = servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        servicioIncidencia.crearTipoIncidencia(resultado.get(),"Suciedad");
        TipoIncidencia suciedad = servicioIncidencia.obtenerTipoIncidencia("Suciedad").get();

        servicioIncidencia.crearTipoIncidencia(resultado.get(),"Rotura en parque");
        TipoIncidencia roturaEnParque = servicioIncidencia.obtenerTipoIncidencia("Rotura en parque").get();

        servicioIncidencia.crearTipoIncidencia(resultado.get(),"Rotura en mobiliario urbano");
        TipoIncidencia roturaEnMobiliarioUrbano = servicioIncidencia.obtenerTipoIncidencia("Rotura en mobiliario urbano").get();

        Usuario user = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10","+34673826467","aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user);

        Usuario user1 = new Usuario("Carlos","Mayor Navarro",LocalDate.now(),"Av. Arjona 10","+34673826467","cmn00019@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user1);

        Optional<Usuario> opt = servicioIncidencia.login(user.email(),user.clave());

        servicioIncidencia.nuevaIncidencia(fecha, suciedad, "desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt.get());

        Optional<Usuario> opt1 = servicioIncidencia.login(user1.email(),user1.clave());

        servicioIncidencia.nuevaIncidencia(fecha, roturaEnParque, "desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt1.get());

        Optional<Usuario> opt2 = servicioIncidencia.login("admin.dae@ujaen.es","admin");

        // Probar a borrar como admin con incidencias en us;
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia(opt2.get(), suciedad)).isInstanceOf(TipoIncidenciaEnUso.class);
        assertThat(servicioIncidencia.obtenerTiposIncidencia()).hasSize(3);

        // Probar a borrar tipo de incidencia inexistente
        TipoIncidencia noExiste = new TipoIncidencia("Pepito");
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia(opt2.get(), noExiste)).isInstanceOf(TipoIncidenciaNoExiste.class);
        assertThat(servicioIncidencia.obtenerTiposIncidencia()).hasSize(3);

        // Probar a borrar como usuario normal
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia(opt1.get(), suciedad)).isInstanceOf(AccionNoAutorizada.class);
        assertThat(servicioIncidencia.obtenerTiposIncidencia()).hasSize(3);

        // Probar a borrar como admin incidencia sin uso
        servicioIncidencia.borrarTipoIncidencia(resultado.get(), roturaEnMobiliarioUrbano);
        assertThat(servicioIncidencia.obtenerTiposIncidencia()).hasSize(2); //Hemos borrado un tipo de incidencia, debería haber uno menos
    }
}
