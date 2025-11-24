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

        Usuario user = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10",673826467,"aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user);

        Usuario user1 = new Usuario("Carlos","Mayor Navarro",LocalDate.now(),"Av. Arjona 10",673826467,"cmn00019@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user1);

        Optional<Usuario> opt = servicioIncidencia.login(user.email(),user.clave());

        int id1=servicioIncidencia.nuevaIncidencia(fecha, "tipo1", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt.get());

        Optional<Usuario> opt1 = servicioIncidencia.login(user1.email(),user1.clave());
        int id2=servicioIncidencia.nuevaIncidencia(fecha, "tipo2", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt1.get());

        int id3=servicioIncidencia.nuevaIncidencia(fecha, "tipo3","desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt1.get());

        List<Incidencia> incidencias=servicioIncidencia.obtenerListaIncidenciasUsuario(opt.get());

        assertThat(incidencias).hasSize(1);
        assertThat(incidencias.getFirst().id()).isEqualTo(id1);

        incidencias=servicioIncidencia.obtenerListaIncidenciasUsuario(opt1.get());
        assertThat(incidencias).hasSize(2);
        assertThat(incidencias.getFirst().id()).isEqualTo(id2);
        assertThat(incidencias.get(1).id()).isEqualTo(id3);

        // Probar obtener lista de usuario sin incidencias

        Usuario user2 = new Usuario("Ximena","Galmades",LocalDate.now(),"Av. Arjona 10",673826467,"xmnn10@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user2);


        Optional<Usuario> opt2 = servicioIncidencia.login(user2.email(),user2.clave());
        incidencias=servicioIncidencia.obtenerListaIncidenciasUsuario(opt2.get());
        assertThat(incidencias).hasSize(0);

    }

    @Test
    @DirtiesContext
    public void testBuscarIncidencias(){
        LocalDateTime fecha = LocalDateTime.now();

        Usuario user = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10",673826467,"aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user);

        Usuario user1 = new Usuario("Carlos","Mayor Navarro",LocalDate.now(),"Av. Arjona 10",673826467,"cmn00019@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user1);

        Optional<Usuario> opt = servicioIncidencia.login(user.email(),user.clave());
        int id1=servicioIncidencia.nuevaIncidencia(fecha, "Suciedad","desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt.get());

        Optional<Usuario> opt1 = servicioIncidencia.login(user1.email(),user1.clave());
        int id2=servicioIncidencia.nuevaIncidencia(fecha, "Rotura en parque","desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt1.get());
        int id3=servicioIncidencia.nuevaIncidencia(fecha, "Rotura en mobiliario urbano","desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt.get());

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

        Optional<Usuario> admin=servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        assertThatThrownBy(() -> servicioIncidencia.borrarIncidencia(admin.get(), 1)).isInstanceOf(IncidenciaNoExiste.class);

        LocalDateTime fecha = LocalDateTime.now();

        Usuario usuario1 = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10",673826467,"aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(usuario1);

        Optional<Usuario> user1=servicioIncidencia.login(usuario1.email(), usuario1.clave());

        Usuario usuario2 = new Usuario("Carlos","Mayor Navarro",LocalDate.now(),"Av. Arjona 10",673826467,"cmn00019@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(usuario2);

        Optional<Usuario> user2=servicioIncidencia.login(usuario2.email(), usuario2.clave());


        int id1=servicioIncidencia.nuevaIncidencia(fecha, "tipo1","desc", "loc", (float) 1.0, (float) 1.0, "dpt", user1.get());
        int id2=servicioIncidencia.nuevaIncidencia(fecha, "tipo2","desc", "loc", (float) 1.0, (float) 1.0, "dpt", user2.get());

        // Probar a borrar como usuario ajeno a la incidencia
        assertThat(servicioIncidencia.borrarIncidencia(user2.get(),id1)).isEqualTo(false);

        // Probar a borrar como usuario de la incidencia con estado invalido
        servicioIncidencia.modificarEstadoIncidencia(admin.get(), EstadoIncidencia.RESUELTA, id2);
        assertThat(servicioIncidencia.borrarIncidencia(user1.get(),id2)).isEqualTo(false);

        // Probar a borrar como usuario de la incidencia con estado valido
        assertThat(servicioIncidencia.borrarIncidencia(user1.get(),id1)).isEqualTo(true);

        // Probar a borrar como admin
        assertThat(servicioIncidencia.borrarIncidencia(admin.get(),id2)).isEqualTo(true);

    }

    @Test
    @DirtiesContext
    public void testModificarEstadoIncidencia(){
        //Probar a modificar incidencia inexistente
        Optional<Usuario> admin=servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        assertThatThrownBy(() -> servicioIncidencia.modificarEstadoIncidencia(admin.get(), EstadoIncidencia.EN_EVALUACION,1)).isInstanceOf(IncidenciaNoExiste.class);

        LocalDateTime fecha = LocalDateTime.now();
        Usuario usuario1 = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10",673826467,"aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(usuario1);

        Optional<Usuario> user1=servicioIncidencia.login(usuario1.email(), usuario1.clave());

        int id=servicioIncidencia.nuevaIncidencia(fecha, "tipo", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", user1.get());

        // Probar a modificar como admin
        servicioIncidencia.modificarEstadoIncidencia(admin.get(), EstadoIncidencia.EN_EVALUACION,id);
        assertThat(servicioIncidencia.buscarIncidenciasTipoEstado(null, EstadoIncidencia.EN_EVALUACION)).hasSize(1);
        assertThat(servicioIncidencia.buscarIncidenciasTipoEstado(null, EstadoIncidencia.PENDIENTE)).hasSize(0);

        // Porbar a modificar como usuario normal
        assertThatThrownBy(() -> servicioIncidencia.modificarEstadoIncidencia(user1.get(), EstadoIncidencia.RESUELTA, id)).isInstanceOf(AccionNoAutorizada.class);
    }

    @Test
    @DirtiesContext
    public void testCrearTipoIncidencia(){
        // Probar a crear como admin
        Optional<Usuario> admin=servicioIncidencia.login("admin.dae@ujaen.es", "admin");

        servicioIncidencia.crearTipoIncidencia(admin.get(), "nuevoTipoIncidencia");

        // Probar a crear tipo de incidencia ya existente
        assertThatThrownBy(() -> servicioIncidencia.crearTipoIncidencia(admin.get(), "nuevoTipoIncidencia")).isInstanceOf(TipoIncidenciaExiste.class);

        // Probar a crear como usuario normal
        Usuario usuario1 = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10",673826467,"aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(usuario1);

        Optional<Usuario> user1=servicioIncidencia.login(usuario1.email(), usuario1.clave());

        assertThatThrownBy(() -> servicioIncidencia.crearTipoIncidencia(user1.get(), "crearTipoIncidencia")).isInstanceOf(AccionNoAutorizada.class);

    }

    @Test
    @DirtiesContext
    public void testBorrarTipoIncidencia(){
        LocalDateTime fecha = LocalDateTime.now();

        Usuario user = new Usuario("Alberto","Jiménez Expósito",LocalDate.now(),"Av. Arjona 10",673826467,"aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user);

        Usuario user1 = new Usuario("Carlos","Mayor Navarro",LocalDate.now(),"Av. Arjona 10",673826467,"cmn00019@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user1);

        Optional<Usuario> opt = servicioIncidencia.login(user.email(),user.clave());

        servicioIncidencia.nuevaIncidencia(fecha, "Suciedad", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt.get());

        Optional<Usuario> opt1 = servicioIncidencia.login(user1.email(),user1.clave());

        servicioIncidencia.nuevaIncidencia(fecha, "Rotura en mobiliario urbano", "desc", "loc", (float) 1.0, (float) 1.0, "dpt", opt1.get());

        Optional<Usuario> opt2 = servicioIncidencia.login("admin.dae@ujaen.es","admin");

        // Probar a borrar como admin con incidencias de ese tipo
        TipoIncidencia tipo = new TipoIncidencia(0,"Suciedad");
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia(opt2.get(), tipo)).isInstanceOf(TipoIncidenciaEnUso.class);

        // Probar a borrar tipo de incidencia inexistente
        tipo.nombre("Objeto extraviado");
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia(opt2.get(), tipo)).isInstanceOf(TipoIncidenciaNoExiste.class);

        // Probar a borrar como usuario normal
        tipo.nombre("Suciedad");
        assertThatThrownBy(() -> servicioIncidencia.borrarTipoIncidencia(opt1.get(), tipo)).isInstanceOf(AccionNoAutorizada.class);
    }

}
