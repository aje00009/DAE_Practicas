package es.ujaen.dae.indicenciasurbanas.servicios;

import es.ujaen.dae.indicenciasurbanas.entidades.TipoIncidencia;
import es.ujaen.dae.indicenciasurbanas.repositorios.RepositorioIncidencias;
import es.ujaen.dae.indicenciasurbanas.repositorios.RepositorioTipoIncidencia;
import es.ujaen.dae.indicenciasurbanas.repositorios.RepositorioUsuarios;
import es.ujaen.dae.indicenciasurbanas.utils.CoordenadasGps;
import es.ujaen.dae.indicenciasurbanas.utils.DistanciaCoordenadas;
import es.ujaen.dae.indicenciasurbanas.utils.EstadoIncidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Incidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import es.ujaen.dae.indicenciasurbanas.excepciones.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Validated
@Transactional
public class ServicioIncidencia {
    @Autowired
    private RepositorioUsuarios repositorioUsuarios;
    @Autowired
    private RepositorioIncidencias repositorioIncidencias;

    @Autowired
    private RepositorioTipoIncidencia repositorioTipoIncidencia;

    private final Usuario admin = new Usuario("administrador","administrador",
            LocalDate.of(1995,1,1),"-","+34661030462","admin.dae@ujaen.es","admin");

    public ServicioIncidencia() { }

    /**
     * Registro de una nueva Incidencia en el sistema
     * @param fecha fecha de la Incidencia que va a ser registrada
     * @param tipo tipo de la Incidencia que va se va a registrar
     * @param descripcion descripción de la Incidencia que se va a registrar
     * @param localizacion localización de la Incidencia que va a ser registrada
     * @param latitud coordenadas x del la localización de la incidencia que se va a registrar
     * @param longitud coordenadas y del la localización de la incidencia que se va a registrar
     * @param dpto nombre del departamento que se va a asignar a la incidencia
     * @param user Usuario que ha notificado de la incidencia que se va a registrar
     * return Devuelve el identificador de la incidencia creada
     */
    public Incidencia nuevaIncidencia(@NotNull LocalDateTime fecha, @NotNull TipoIncidencia tipo, @NotBlank String descripcion, @NotBlank String localizacion,
                                 float latitud, float longitud, @NotBlank String dpto, @Valid Usuario user, byte[] foto) {

        CoordenadasGps coordenadasNuevaIncidencia = new CoordenadasGps(latitud,longitud);

        List<Incidencia> incidenciasRegistradas;
        incidenciasRegistradas = repositorioIncidencias.buscarPorEstado(EstadoIncidencia.PENDIENTE);
        incidenciasRegistradas.addAll(repositorioIncidencias.buscarPorEstado(EstadoIncidencia.EN_EVALUACION));

        for (Incidencia incidencia : incidenciasRegistradas) {
            double distancia = DistanciaCoordenadas.calcularDistanciaMetros(coordenadasNuevaIncidencia, incidencia.coordenadas());
            if(distancia<10){
                throw new IncidenciaEnCurso();
            }
        }

        Incidencia nuevaIncidencia = new Incidencia(fecha, tipo,
                descripcion, localizacion, latitud, longitud, dpto, user, foto);

        // Guardamos la entidad con el repositorio
        repositorioIncidencias.guardar(nuevaIncidencia);

        // Devolvemos la nueva incidencia (o null si no ha podido ser creada al estar repetida)
        return nuevaIncidencia;
    }

    /**
     * Registro de un nuevo usuario en el sistema
     * @param usuario El objeto usuario a ser añadido al sistema
     * @throws UsuarioYaRegistrado En caso de que el Usuario a registrar ya esté en el sistema o se intente crear con el mismo email que el admin
     */
    public void nuevoUsuario(@Valid Usuario usuario) {
        if(usuario.email().equals(admin.email())) //Si usuario tiene mismo email que el admin, registro denegado
            throw new UsuarioYaRegistrado();

        repositorioUsuarios.guardar(usuario); //Persistimos el objeto en la BBDD
    }

    /**
     * Login para identificarse como usuario en el sistema
     * @param email Email de usuario para hacer login
     * @param clave Contraseña asociada al usuario para hacer login
     * @return Un objeto Optional encapsulando a un objeto Usuario o vacío si no se ha encontrado al usuario
     */
    @Transactional(readOnly = true) // Optimización para consultas
    public Optional<Usuario> login(@Email String email, @NotBlank String clave){
        if(email.equals(admin.email()) &&  clave.equals(admin.clave()))
            return Optional.of(admin);

        // Buscamos al usuario. Esta consulta usará la caché "usuarios".
        Optional<Usuario> u = repositorioUsuarios.buscar(email);

        // Comprobamos la clave
        return u.filter(usuario -> usuario.clave().equals(clave));
    }

    /**
     * Obtener una lista de incidencias generadas por un usuario concreto
     * @param usuario usuario logeado
     * @return Devuelve una lista con las incidencias generadas por el usuario con el login
     */
    @Transactional(readOnly = true)
    public List<Incidencia> obtenerListaIncidenciasUsuario(@NotNull Usuario usuario){
        return repositorioIncidencias.buscarPorEmailUsuario(usuario.email());
    }

    /**
     * Obtener las incidencias con un tipo de incidencia y/o estado de incidencia concreto
     * @param tipoIncidencia valor del tipo de incidencia deseado, puede ser nulo
     * @param estadoIncidencia valor del estado de incidencia deseado, puede ser nulo
     * @return Devuelve una lista con las incidencias que tienen los valores deseados
     */
    public List<Incidencia> buscarIncidenciasTipoEstado(TipoIncidencia tipoIncidencia, EstadoIncidencia estadoIncidencia){


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

    /**
     * Eliminación de una incidencia registrada en el sistema
     * @param usuario usuario logeado
     * @param incidenciaBorrar Incidencia que se quiere eliminar
     */
    public boolean borrarIncidencia(@NotNull Usuario usuario, @NotNull Incidencia incidenciaBorrar){
        // 1. Buscamos la incidencia
        Incidencia incidencia = repositorioIncidencias.buscarPorIdBloqueando(incidenciaBorrar.id())
                .orElseThrow(IncidenciaNoExiste::new);

        // 2. Comprobamos permisos (lógica original)
        boolean esAdmin = usuario.equals(admin);
        boolean esPropietario = incidencia.usuario().email().equals(usuario.email());
        boolean estaPendiente = (incidencia.estado() == EstadoIncidencia.PENDIENTE);

        // 3. Borramos si cumple
        if (esAdmin || (esPropietario && estaPendiente)) {
            repositorioIncidencias.borrar(incidencia);
            return true;
        }

        return false;
    }

    /**
     * Modificación del estado de una incidencia
     * @param usuario usuario logeado
     * @param estadoIncidencia Nuevo estado de la incidencia
     * @param Incidencia Identificador de la incidencia a modificar
     */
    public void modificarEstadoIncidencia(@NotNull Usuario usuario, EstadoIncidencia estadoIncidencia, @NotNull Incidencia Incidencia){
        // 1. Buscamos la incidencia
        Incidencia incidencia = repositorioIncidencias.buscarPorId(Incidencia.id())
                .orElseThrow(IncidenciaNoExiste::new);

        if(!usuario.equals(admin)) {
            throw new AccionNoAutorizada();
        }

        // 2. Modificamos la entidad en memoria (con el @Transactional ya se modifica en la BBDD al tenerlo linkeado dentro de la transacción)
        incidencia.estado(estadoIncidencia);

    }

    /**
     * Crear un nuevo tipo de incidencia
     * @param usuario usuario logeado
     * @param tipoIncidencia Tipo de incidencia a añadir
     */
    public void crearTipoIncidencia(@NotNull Usuario usuario, @NotBlank String tipoIncidencia){
        if(!usuario.email().equals(admin.email())){
            throw new AccionNoAutorizada();
        }

        // Comprobamos si ya existe
        if(repositorioTipoIncidencia.buscarPorNombre(tipoIncidencia).isPresent()){
            throw new TipoIncidenciaExiste();
        }

        repositorioTipoIncidencia.guardar(new TipoIncidencia(tipoIncidencia));
    }

    /**
     * Borrar un tipo de Incidencia
     * @param usuario Identificador del usuario
     * @param tipoIncidencia tipo de Incidencia a borrar
     */
    public void borrarTipoIncidencia(@NotNull Usuario usuario, @NotNull TipoIncidencia tipoIncidencia){
        if(!usuario.equals(admin)) {
            throw new AccionNoAutorizada();
        }


        TipoIncidencia tipo = repositorioTipoIncidencia.buscarPorNombre(tipoIncidencia.nombre())
                .orElseThrow(TipoIncidenciaNoExiste::new);

        // Comprobamos si está en uso
        if (!repositorioIncidencias.buscarPorTipo(tipo).isEmpty()) {
            throw new TipoIncidenciaEnUso();
        }

        repositorioTipoIncidencia.borrar(tipo);
    }

    /**
     * Función que devuelve todos los tipos de incidencia registrados en el sistema
     * @return Devuelve una lista con los tipos de incidencia
     */
    public List<TipoIncidencia> obtenerTiposIncidencia(){
        return repositorioTipoIncidencia.buscarTodos();
    }

    /**
     * Función que devuelve un tipo de incidencia según su nombre
     * @param tipoIncidencia Nombre del tipo de incidencia a devolver
     * @return Devuelve un Optional encaspsulando el TipoIncidencia (o vacío si no existe)
     */
    public Optional<TipoIncidencia> obtenerTipoIncidencia(String tipoIncidencia){
        return repositorioTipoIncidencia.buscarPorNombre(tipoIncidencia);
    }
}
