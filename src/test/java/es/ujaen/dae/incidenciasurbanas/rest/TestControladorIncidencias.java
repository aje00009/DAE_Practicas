package es.ujaen.dae.incidenciasurbanas.rest;

import es.ujaen.dae.indicenciasurbanas.app.DaePracticaApplication;
import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import es.ujaen.dae.indicenciasurbanas.rest.dto.DUsuario;
import jakarta.annotation.PostConstruct;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = DaePracticaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"test"})
public class TestControladorIncidencias {

    @LocalServerPort
    int localPort;

    TestRestTemplate restTemplate;

    @PostConstruct
    void crearRestTemplateBuilder(){
        var restTemplateBuilder = new RestTemplateBuilder().rootUri("http://localhost:" + localPort + "/incidencias");

        restTemplate = new TestRestTemplate(restTemplateBuilder);
    }

    @Test
    @DirtiesContext
    public void testNuevoCliente(){
        var usuario = new DUsuario("Ximena", "Galdames", LocalDate.of(2000, 1, 1), "direccion", "+34600123456", "mxgf0001@red.ujaen.es", "clave");

        ResponseEntity<Void> respuesta = restTemplate.postForEntity(
                "/usuarios",
                usuario,
                Void.class);

        // Creación correcta
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> respuestaDuplicada = restTemplate.postForEntity(
                "/usuarios",
                usuario,
                Void.class);

        // Creación de usuario duplicado
        assertThat(respuestaDuplicada.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DirtiesContext
    public void testObtenerUsuario(){
        // Obtener usuario inexistente
        ResponseEntity<DUsuario> respuestaIncorrecta = restTemplate.getForEntity(
                "/usuarios/?email={email}",
                DUsuario.class,
                "emailNoExiste@gmail.com");

        assertThat(respuestaIncorrecta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        var u1 = new DUsuario("Ximena", "Galdames", LocalDate.of(2000, 1, 1), "direccion", "+34600123456", "mxgf0001@red.ujaen.es", "clave");

        restTemplate.postForEntity("/usuarios", u1, Void.class);

        // Obtener usuario creado
        ResponseEntity<DUsuario> respuesta = restTemplate.getForEntity(
                "/usuarios/?email={email}",
                DUsuario.class,
                u1.email());

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody().nombre()).isEqualTo(u1.nombre());
    }
}
