package es.ujaen.dae.indicenciasurbanas.servicios;

import es.ujaen.dae.indicenciasurbanas.entidades.Incidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.TipoIncidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import es.ujaen.dae.indicenciasurbanas.excepciones.*;
import es.ujaen.dae.indicenciasurbanas.repositorios.RepositorioIncidencias;
import es.ujaen.dae.indicenciasurbanas.repositorios.RepositorioTipoIncidencia;
import es.ujaen.dae.indicenciasurbanas.repositorios.RepositorioUsuarios;
import es.ujaen.dae.indicenciasurbanas.utils.CoordenadasGps;
import es.ujaen.dae.indicenciasurbanas.utils.DistanciaCoordenadas;
import es.ujaen.dae.indicenciasurbanas.utils.EstadoIncidencia;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class ServicioIncidencia {
    @Autowired
    private RepositorioUsuarios repositorioUsuarios;

    @Autowired
    private RepositorioIncidencias repositorioIncidencias;

    @Autowired
    private RepositorioTipoIncidencia repositorioTipoIncidencia;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- USUARIO ADMINISTRADOR ---
    private static final Usuario admin = new Usuario(
            "administrador",
            "administrador",
            LocalDate.of(1995,1,1),
            "-",
            "+34661030462",
            "admin.dae@ujaen.es",
            "$2a$10$qukCo2vXP.jD.a/jD.a/jD.a/jD.a/jD.a/jD.a/jD.a/jD.a/jD.a" // Hash simulado válido para estructura BCrypt
    );

    public ServicioIncidencia() {}

    public void nuevoUsuario(@Valid Usuario usuario) {
        // 1. Evitar conflicto con el admin en memoria o verificar si ya existe
        if (usuario.email().equals(admin.email()) || repositorioUsuarios.buscar(usuario.email()).isPresent()) {
            throw new UsuarioYaRegistrado();
        }

        // 2. Guardado del usuario
        repositorioUsuarios.guardar(usuario);
    }

   /**
     * Método para API REST y futura Security.
     * Recupera usuario por email (incluido admin hardcodeado).
     */
    public Optional<Usuario> obtenerUsuario(@NotBlank String email) {
        if (email.equals(admin.email())) {
            return Optional.of(admin);
        }

        return repositorioUsuarios.buscar(email);
    }

    public Incidencia buscarIncidencia(int id) {
        return repositorioIncidencias.buscarPorId(id)
                .orElseThrow(IncidenciaNoExiste::new);
    }

    public Incidencia nuevaIncidencia(@NotNull LocalDateTime fecha, @NotNull TipoIncidencia tipo,
                                      @NotBlank String descripcion, @NotBlank String localizacion,
                                      float latitud, float longitud, @NotBlank String dpto,
                                      @Valid Usuario user, byte[] foto) {

        CoordenadasGps coordenadasNuevaIncidencia = new CoordenadasGps(latitud, longitud);

        // Lógica de negocio: Evitar duplicados cercanos (< 10m)
        List<Incidencia> incidenciasRegistradas = repositorioIncidencias.buscarPorEstado(EstadoIncidencia.PENDIENTE);
        incidenciasRegistradas.addAll(repositorioIncidencias.buscarPorEstado(EstadoIncidencia.EN_EVALUACION));

        for (Incidencia incidencia : incidenciasRegistradas) {
            double distancia = DistanciaCoordenadas.calcularDistanciaMetros(coordenadasNuevaIncidencia, incidencia.coordenadas());
            if (distancia < 10) {
                throw new IncidenciaEnCurso();
            }
        }

        Incidencia nuevaIncidencia = new Incidencia(fecha, tipo,
                descripcion, localizacion, latitud, longitud, dpto, user, foto);

        repositorioIncidencias.guardar(nuevaIncidencia);
        return nuevaIncidencia;
    }

    public void borrarIncidencia(int idIncidencia) {
        Incidencia incidencia = repositorioIncidencias.buscarPorIdBloqueando(idIncidencia)
                .orElseThrow(IncidenciaNoExiste::new);

        repositorioIncidencias.borrar(incidencia);
    }

    public void modificarEstadoIncidencia(int idIncidencia, @NotNull EstadoIncidencia estadoNuevo) {
        Incidencia incidencia = repositorioIncidencias.buscarPorId(idIncidencia)
                .orElseThrow(IncidenciaNoExiste::new);

        incidencia.estado(estadoNuevo);
    }

    public List<Incidencia> buscarIncidenciasTipoEstado(TipoIncidencia tipoIncidencia, EstadoIncidencia estadoIncidencia) {
        if (tipoIncidencia != null && estadoIncidencia != null) {
            return repositorioIncidencias.buscarPorTipoYEstado(tipoIncidencia, estadoIncidencia);
        }
        if (tipoIncidencia != null) {
            return repositorioIncidencias.buscarPorTipo(tipoIncidencia);
        }
        if (estadoIncidencia != null) {
            return repositorioIncidencias.buscarPorEstado(estadoIncidencia);
        }
        return repositorioIncidencias.buscarTodas();
    }

    public List<Incidencia> obtenerListaIncidenciasUsuario(@NotNull Usuario usuario) {
        return repositorioIncidencias.buscarPorEmailUsuario(usuario.email());
    }

    // --- GESTIÓN DE TIPOS DE INCIDENCIA ---

    public void crearTipoIncidencia(@NotBlank String nombreTipo) {
        if (repositorioTipoIncidencia.buscarPorNombre(nombreTipo).isPresent()) {
            throw new TipoIncidenciaExiste();
        }
        repositorioTipoIncidencia.guardar(new TipoIncidencia(nombreTipo));
    }

    public void borrarTipoIncidencia(@NotBlank String nombreTipo) {
        TipoIncidencia tipo = repositorioTipoIncidencia.buscarPorNombre(nombreTipo)
                .orElseThrow(TipoIncidenciaNoExiste::new);

        if (!repositorioIncidencias.buscarPorTipo(tipo).isEmpty()) {
            throw new TipoIncidenciaEnUso();
        }

        repositorioTipoIncidencia.borrar(tipo);
    }

    public List<TipoIncidencia> obtenerTiposIncidencia() {
        return repositorioTipoIncidencia.buscarTodos();
    }

    public Optional<TipoIncidencia> obtenerTipoIncidencia(String nombreTipo) {
        return repositorioTipoIncidencia.buscarPorNombre(nombreTipo);
    }
}