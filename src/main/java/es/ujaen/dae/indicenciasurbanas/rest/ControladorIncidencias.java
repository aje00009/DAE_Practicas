package es.ujaen.dae.indicenciasurbanas.rest;

import es.ujaen.dae.indicenciasurbanas.entidades.Incidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import es.ujaen.dae.indicenciasurbanas.entidades.TipoIncidencia;
import es.ujaen.dae.indicenciasurbanas.excepciones.*;
import es.ujaen.dae.indicenciasurbanas.rest.dto.DIncidencia;
import es.ujaen.dae.indicenciasurbanas.rest.dto.DTipoIncidencia;
import es.ujaen.dae.indicenciasurbanas.rest.dto.DUsuario;
import es.ujaen.dae.indicenciasurbanas.rest.dto.Mapeador;
import es.ujaen.dae.indicenciasurbanas.servicios.ServicioIncidencia;
import es.ujaen.dae.indicenciasurbanas.utils.EstadoIncidencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

import java.util.List;

@RestController
@RequestMapping("/incidencias")
public class ControladorIncidencias {

    @Autowired
    ServicioIncidencia servicioIncidencia;

    @Autowired
    Mapeador mapeador;

    // --- Gestión de Errores ---

    @ExceptionHandler(UsuarioYaRegistrado.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handlerUsuarioYaRegistrado() {}

    @ExceptionHandler(IncidenciaNoExiste.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handlerIncidenciaNoExiste() {}

    @ExceptionHandler(TipoIncidenciaNoExiste.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handlerTipoIncidenciaNoExiste() {}

    @ExceptionHandler(IncidenciaEnCurso.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handlerIncidenciaEnCurso() {}

    @ExceptionHandler(TipoIncidenciaExiste.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handlerTipoIncidenciaExiste() {}

    @ExceptionHandler(TipoIncidenciaEnUso.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handlerTipoIncidenciaEnUso() {}

    @ExceptionHandler(UsuarioNoExiste.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handlerUsuarioNoRegistrado() {}

    // --- Endpoints Usuarios ---

    @PostMapping("/usuarios")
    public ResponseEntity<Void> nuevoUsuario(@RequestBody DUsuario dUsuario) {
        Usuario usuario = mapeador.entidad(dUsuario);
        servicioIncidencia.nuevoUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/usuarios/{email}")
    public ResponseEntity<DUsuario> obtenerUsuario(@PathVariable String email) {
        return servicioIncidencia.obtenerUsuario(email)
                .map(u -> ResponseEntity.ok(mapeador.dto(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Endpoints Incidencias ---

    @PostMapping
    public ResponseEntity<DIncidencia> crearIncidencia(@RequestBody DIncidencia dIncidencia, Principal principal) {
        String emailAutenticado = principal.getName();

        Usuario autor = servicioIncidencia.obtenerUsuario(emailAutenticado)
                .orElseThrow(UsuarioNoExiste::new);

        Incidencia nueva = mapeador.entidad(dIncidencia);

        // Creamos la incidencia forzando al autor autenticado
        Incidencia creada = servicioIncidencia.nuevaIncidencia(
                nueva.fecha(),
                nueva.tipo(),
                nueva.descripcion(),
                nueva.localizacion(),
                nueva.coordenadas().latitud(),
                nueva.coordenadas().longitud(),
                nueva.dpto(),
                autor,
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapeador.dto(creada));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DIncidencia> obtenerIncidencia(@PathVariable int id) {
        Incidencia incidencia = servicioIncidencia.verIncidencia(id);
        return ResponseEntity.ok(mapeador.dto(incidencia));
    }

    @GetMapping
    public ResponseEntity<List<DIncidencia>> buscarIncidencias(
            @RequestParam(required = false) String nombreTipo,
            @RequestParam(required = false) EstadoIncidencia estado) {

        TipoIncidencia tipoObj = null;
        if(nombreTipo != null) {
            tipoObj = servicioIncidencia.obtenerTipoIncidencia(nombreTipo).orElse(null);
            if(tipoObj == null) return ResponseEntity.ok(List.of());
        }

        List<Incidencia> resultado = servicioIncidencia.buscarIncidenciasTipoEstado(tipoObj, estado);
        return ResponseEntity.ok(resultado.stream().map(mapeador::dto).toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarIncidencia(@PathVariable int id) {
        servicioIncidencia.borrarIncidencia(id);
        return ResponseEntity.ok().build();
    }

    // Endpoint para modificar estado (Necesario para probar conflicto Admin vs User)

    @PutMapping("/{id}")
    public ResponseEntity<Void> modificarEstadoIncidencia(@PathVariable int id, @RequestBody EstadoIncidencia estado) {
        servicioIncidencia.modificarEstadoIncidencia(id, estado);
        return ResponseEntity.ok().build();
    }

    // --- Endpoints Tipos de Incidencia ---

    @GetMapping("/tipos")
    public ResponseEntity<List<DTipoIncidencia>> listarTipos() {
        List<TipoIncidencia> tipos = servicioIncidencia.obtenerTiposIncidencia();
        return ResponseEntity.ok(tipos.stream().map(mapeador::dto).toList());
    }

    @PostMapping("/tipos")
    public ResponseEntity<Void> crearTipo(@RequestBody DTipoIncidencia dTipo) {
        servicioIncidencia.crearTipoIncidencia(dTipo.nombre());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/tipos/{nombre}")
    public ResponseEntity<Void> borrarTipo(@PathVariable String nombre) {
        servicioIncidencia.borrarTipoIncidencia(nombre);
        return ResponseEntity.ok().build();
    }
}