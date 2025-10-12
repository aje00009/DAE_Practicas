package es.ujaen.dae.indicenciasurbanas.servicios;

import es.ujaen.dae.indicenciasurbanas.entidades.EstadoIncidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Incidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import es.ujaen.dae.indicenciasurbanas.excepciones.AccionNoAutorizada;
import es.ujaen.dae.indicenciasurbanas.excepciones.IncidenciaNoExiste;
import es.ujaen.dae.indicenciasurbanas.excepciones.IncidenciaYaRegistrada;
import es.ujaen.dae.indicenciasurbanas.excepciones.UsuarioYaRegistrado;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Validated
public class ServicioIncidencia {
    private List<String> tipoIncidencia;
    private Map<String, Usuario> usuarioMap;
    private Map<Integer, Incidencia> incidenciaMap;
    private static int nIncidencia= 1;

    private static final Usuario admin = new Usuario("administrador","administrador",
            LocalDate.of(1995,1,1),"-",661030462,"admin.dae@ujaen.es","admin");

    public ServicioIncidencia() {
        this.usuarioMap = new HashMap<>();
        this.incidenciaMap = new HashMap<>();
        tipoIncidencia = new ArrayList<>();

        tipoIncidencia.add("Suciedad");
        tipoIncidencia.add("Rotura en parque");
        tipoIncidencia.add("Rotura en mobiliario urbano");
    }

    /**
     *Registro de una nueva Incidencia en el sistema
     * @param fecha fecha de la Incidencia que va a ser registrada
     * @param tipo tipo de la Incidencia que va se va a registrar
     * @param descripcion descripción de la Incidencia que se va a registrar
     * @param localizacion localización de la Incidencia que va a ser registrada
     * @param latitud coordenadas x del la localización de la incidencia que se va a registrar
     * @param longitud coordenadas y del la localización de la incidencia que se va a registrar
     * @param dpto nombre del departamento que se va a asignar a la incidencia
     * @param emailUsuario email del usuario que ha notificado de la incidencia que se va a registrar
     */
    public void nuevaIncidencia(@NotNull LocalDateTime fecha, @NotBlank String tipo, @NotBlank String descripcion, @NotBlank String localizacion,
                                @NotBlank Float latitud,@NotBlank Float longitud, @NotBlank String dpto, @Email String emailUsuario) {

        Incidencia nuevaIncidencia = new Incidencia(nIncidencia++,fecha, tipo, descripcion, localizacion, latitud, longitud, dpto, emailUsuario);

        boolean existe = incidenciaMap.values().stream().anyMatch(existente -> existente.equals(nuevaIncidencia));

        if (existe) {
            throw new IncidenciaYaRegistrada();
        }

        incidenciaMap.put(nuevaIncidencia.id(), nuevaIncidencia);
    }

    /**
     * Registro de un nuevo usuario en el sistema
     * @param usuario El objeto usuario a ser añadido al sistema
     * @throws UsuarioYaRegistrado En caso de que el Usuario a registrar ya esté en el sistema o se intente crear con el mismo email que el admin
     */
    public void nuevoUsuario(@Valid Usuario usuario) {
        if(usuario.email().equals(admin.email()))
            throw new UsuarioYaRegistrado();

        Usuario u = usuarioMap.putIfAbsent(usuario.email(),usuario);
        if( u==null ) {
            throw new UsuarioYaRegistrado();
        }
    }

    /**
     * Login para identificarse como usuario en el sistema
     * @param email Email de usuario para hacer login
     * @param clave Contraseña asociada al usuario para hacer login
     * @return Un objeto Optional encapsulando a un objeto Usuario o vacío si no se ha encontrado al usuario
     */
    public Optional<Usuario> login(@Email String email, String clave){
        if(email.equals(admin.email()) &&  clave.equals(admin.clave()))
            return Optional.of(admin);

        Usuario u = usuarioMap.get(email);
        return (u != null && u.clave().equals(clave)) ? Optional.of(u) : Optional.empty();
    }

    /**
     * Obtener una lista de incidencias generadas por un usuario concreto
     * @param email Identificador único del usuario
     * @return Devuelve una lista con las incidencias generadas por el usuario con el login
     */
    public List<Incidencia> obtenerListaIncidenciasUsuario(String email){
        return incidenciaMap.values().stream().filter(i -> i.emailUsuario().equals(email)).toList();
    }

    /**
     * Obtener las incidencias con un tipo de incidencia y/o estado de incidencia concreto
     * @param tipoIncidencia valor del tipo de incidencia deseado, puede ser nulo
     * @param estadoIncidencia valor del estado de incidencia deseado, puede ser nulo
     * @return Devuelve una lista con las incidencias que tienen los valores deseados
     */
    public List<Incidencia> buscarIncidenciasTipoEstado(String tipoIncidencia, EstadoIncidencia estadoIncidencia){

        return incidenciaMap.values().stream()
                .filter(incidencia -> {

                    //Comprobación del tipo
                    boolean tipoCoincide = (tipoIncidencia == null || incidencia.tipo().equalsIgnoreCase(tipoIncidencia));

                    //Comprobación del estado
                    boolean estadoCoincide = (estadoIncidencia == null || incidencia.estado() == estadoIncidencia);

                    return tipoCoincide && estadoCoincide;
                })
                .toList();
    }

    /**
     * Eliminación de una incidencia registrada en el sistema
     * @param email Identificador del usuario logeado
     * @param idIncidencia identificador de la incidencia que se elimina
     */
    public boolean borrarIncidencia(String email, int idIncidencia){
        // Primero buscamos si existe la incidencia (si no, lanzamos excepción)
        Incidencia incidencia = incidenciaMap.get(idIncidencia);
        if(incidencia == null) {
            throw new IncidenciaNoExiste();
        }

        // Condiciones para borrar
        boolean esAdmin = "admin".equalsIgnoreCase(email);
        boolean esPropietario = incidencia.emailUsuario().equals(email);
        boolean estaPendiente = incidencia.estado() == EstadoIncidencia.PENDIENTE;

        // Se borra si cumple condiciones
        if (esAdmin || (esPropietario && estaPendiente)) {
            incidenciaMap.remove(idIncidencia);
            return true;
        }

        // Si no se borró, se comunica al usuario
        return false;
    }

    /**
     * Modificación del estado de una incidencia
     * @param email Identificador del usuario
     * @param estadoIncidencia Nuevo estado de la incidencia
     * @param idIncidencia Identificador de la incidencia a modificar
     */
    public void modificarEstadoIncidencia(String email, EstadoIncidencia estadoIncidencia, int idIncidencia){
        Incidencia incidencia = incidenciaMap.get(idIncidencia);

        if(incidencia == null) {
            throw new IncidenciaNoExiste();
        }

        if(!email.equals("admin")) {
            throw new AccionNoAutorizada();
        }

        incidencia.estado(estadoIncidencia);
    }

    /**
     * Crear un nuevo tipo de incidencia
     * @param email Identificador del usuario
     * @param tipoIncidencia Tipo de incidencia a añadir
     */
    public void crearTipoIncidencia(String email, String tipoIncidencia){
        if(login.equals("admin")){
            this.tipoIncidencia.add(tipoIncidencia);
        }
    }

    /**
     * Método para borrar un tipo de incidencia
     * @param login identificador del usuario, reservado para admin
     * @param tipoIncidencia valor del tipo de incidencia borrado
     */
    public void borrarTipoIncidencia(String login, String tipoIncidencia){
        if(login.equals("admin")){
            if (buscarIncidencias(tipoIncidencia, null).isEmpty()) {
                this.tipoIncidencia.remove(tipoIncidencia);
            }
        }
    }
}
