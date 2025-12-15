package es.ujaen.dae.incidenciasurbanas.rest;

import es.ujaen.dae.indicenciasurbanas.app.DaePracticaApplication;
import es.ujaen.dae.indicenciasurbanas.rest.dto.DAutenticacionUsuario;
import es.ujaen.dae.indicenciasurbanas.rest.dto.*;
import es.ujaen.dae.indicenciasurbanas.utils.EstadoIncidencia;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = DaePracticaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"test"})
public class TestControladorIncidencias {

    @LocalServerPort
    int localPort;

    TestRestTemplate restTemplate;

    @PostConstruct
    void crearRestTemplateBuilder(){
        var restTemplateBuilder = new RestTemplateBuilder().rootUri("http://localhost:" + localPort ); //+ "/incidencias");

        restTemplate = new TestRestTemplate(restTemplateBuilder);
    }

    static HttpHeaders headerAutorizacion(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testRegistroYLoginUsuario() {
        var usuario = new DUsuario(
                "Pepe", "Pérez", LocalDate.of(1990, 1, 1),
                "Calle Falsa 123", "+34600123456", "pepe@test.com", "secreto"
        );

        // Registro
        ResponseEntity<Void> respuestaRegistro = restTemplate.postForEntity(
                "/incidencias/usuarios",
                usuario,
                Void.class
        );
        assertThat(respuestaRegistro.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Login con credenciales incorrectas
        ResponseEntity<String> loginFallido = restTemplate.postForEntity(
                "/incidencias/autenticacion",
                new DAutenticacionUsuario("pepe@test.com", "clave_mal"),
                String.class
        );
        assertThat(loginFallido.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        // Login correcto
        ResponseEntity<String> loginExito = restTemplate.postForEntity(
                "/incidencias/autenticacion",
                new DAutenticacionUsuario("pepe@test.com", "secreto"),
                String.class
        );
        assertThat(loginExito.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Token de exito
        String token = loginExito.getBody();
        assertThat(token).isNotBlank();

        // Acceder perfil usando el Token
        var peticionPerfil = RequestEntity
                .get("/incidencias/usuarios/{email}", "pepe@test.com")
                .headers(headerAutorizacion(token))
                .build();

        var respuestaPerfil = restTemplate.exchange(peticionPerfil, DUsuario.class);
        assertThat(respuestaPerfil.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaPerfil.getBody().email()).isEqualTo("pepe@test.com");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCicloCompletoConSeguridad() {
        // Login admin
        ResponseEntity<String> loginAdmin = restTemplate.postForEntity(
                "/incidencias/autenticacion",
                new DAutenticacionUsuario("admin.dae@ujaen.es", "admin"),
                String.class
        );
        assertThat(loginAdmin.getStatusCode()).isEqualTo(HttpStatus.OK);
        String tokenAdmin = loginAdmin.getBody();

        // Crear Tipo Incidencia como admin
        var nuevoTipo = new DTipoIncidencia("Farola Rota");

        var peticionCrearTipo = RequestEntity
                .post("/incidencias/tipos")
                .headers(headerAutorizacion(tokenAdmin))
                .body(nuevoTipo);

        ResponseEntity<Void> respuestaTipo = restTemplate.exchange(peticionCrearTipo, Void.class);
        assertThat(respuestaTipo.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Creación usuario
        var usuario = new DUsuario("Ana", "García", LocalDate.now(), "Dir", "+34600000000", "ana@test.com", "pass123");
        restTemplate.postForEntity("/incidencias/usuarios", usuario, Void.class);

        // Login usuario
        ResponseEntity<String> loginUser = restTemplate.postForEntity(
                "/incidencias/autenticacion",
                new DAutenticacionUsuario("ana@test.com", "pass123"),
                String.class
        );
        String tokenUser = loginUser.getBody();

        // Crear incidencia como usuario
        var nuevaIncidencia = new DIncidencia(
                0, LocalDateTime.now(),
                "Farola Rota", // Tipo creado por el admin
                "Parpadea", "Calle Ancha",
                0f, 0f,
                EstadoIncidencia.PENDIENTE,
                "Electricidad",
                "ana@test.com" // Este campo será ignorado por seguridad, se usa el del token
        );

        var peticionIncidencia = RequestEntity
                .post("/incidencias")
                .headers(headerAutorizacion(tokenUser)) // Token de Ana
                .body(nuevaIncidencia);

        ResponseEntity<DIncidencia> respuestaIncidencia = restTemplate.exchange(peticionIncidencia, DIncidencia.class);

        assertThat(respuestaIncidencia.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuestaIncidencia.getBody().id()).isGreaterThan(0);
        assertThat(respuestaIncidencia.getBody().emailUsuario()).isEqualTo("ana@test.com"); // Asociación al usuario correcta
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testBorradoSinPermisos() {
        // Intentar borrar sin token
        RequestEntity<Void> peticionBorrarSinToken = RequestEntity
                .delete("/incidencias/{id}", 1)
                .build();

        var respuestaSinToken = restTemplate.exchange(peticionBorrarSinToken, Void.class);
        assertThat(respuestaSinToken.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testBusquedaPublica() {
        ResponseEntity<DIncidencia[]> respuesta = restTemplate.getForEntity("/incidencias", DIncidencia[].class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
