package es.ujaen.dae.incidenciasurbanas.entidades;

import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import es.ujaen.dae.indicenciasurbanas.excepciones.AccionNoAutorizada;
import es.ujaen.dae.indicenciasurbanas.excepciones.TipoIncidenciaNoExiste;
import es.ujaen.dae.indicenciasurbanas.servicios.ServicioIncidencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = es.ujaen.dae.indicenciasurbanas.servicios.ServicioIncidencia.class)
public class TestUsuario {
    @Autowired
    private ServicioIncidencia servicioIncidencia;

    @BeforeEach
    void setUp() {
        servicioIncidencia = new  ServicioIncidencia();
    }

    @Test
    @DirtiesContext
    public void testClave(){
        //Intentar cambiar la clave como administrador y comprobar que se cambia correctamente
        Usuario admin = servicioIncidencia.login("admin.dae@ujaen.es", "admin").get();

        admin.clave("nUevAClav3_");

        assertThat(admin.clave()).isEqualTo("nUevAClav3_");

        Usuario user = new Usuario("Alberto","Jiménez Expósito", LocalDate.now(),"Av. Arjona 10",673826467,"aje00009@red.ujaen.es","Passw0rD!");
        servicioIncidencia.nuevoUsuario(user);

        Usuario user_loged = servicioIncidencia.login(user.email(),user.clave()).get();
        assertThatThrownBy(() -> user_loged.clave("nUevAClav3_")).isInstanceOf(AccionNoAutorizada.class);

    }
}
