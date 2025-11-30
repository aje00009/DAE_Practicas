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
import java.util.List;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/incidencias")
public class ControladorIncidencias {
    @Autowired
    ServicioIncidencia servicioIncidencia;

    @Autowired
    Mapeador mapeador;

    // --- Gestión de Errores (Mapeo de Excepciones a HTTP) ---

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

    @PostMapping
    public ResponseEntity<DIncidencia> crearIncidencia(@RequestBody DIncidencia dIncidencia) {
        Incidencia nueva = mapeador.entidad(dIncidencia);

        Incidencia creada = servicioIncidencia.nuevaIncidencia(
                nueva.fecha(), nueva.tipo(), nueva.descripcion(), nueva.localizacion(),
                nueva.coordenadas().latitud(), nueva.coordenadas().longitud(),
                nueva.dpto(), nueva.usuario(), null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapeador.dto(creada));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DIncidencia> obtenerIncidencia(@PathVariable int id) {
        Incidencia incidencia = servicioIncidencia.buscarIncidencia(id);
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

