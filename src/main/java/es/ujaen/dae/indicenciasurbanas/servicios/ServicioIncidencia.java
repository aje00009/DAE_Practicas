package es.ujaen.dae.indicenciasurbanas.servicios;

import es.ujaen.dae.indicenciasurbanas.entidades.EstadoIncidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Incidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import es.ujaen.dae.indicenciasurbanas.excepciones.UsuarioYaRegistrado;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
     * Creación de nuevas incidencias en el sistema
     * @param incidencia El objeto incidencia que debe ser válido para quedar registrado en el sistema
     */
    public void nuevaIncidencia(@Valid Incidencia incidencia) {
        incidenciaMap.put(incidencia.id(), incidencia);
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
     * Método para obtener una lista de incidencias generadas por un usuario concreto
     * @param login identificador de usuario
     * @return Devuelve una lista con las incidencias generadas por el usuario con el login
     */
    public List<Incidencia> obtenerListaIncidenciasUsuario(String login){
        return null;
        /*
        TODO - Asociar incidencia con un usuario
         */
    }

    /**
     * Método para obtener las incidencias con un tipo de incidencia y/o estado de incidencia concreto
     * @param tipoIncidencia valor del tipo de incidencia deseado, puede ser nulo
     * @param estadoIncidencia valor del estado de incidencia deseado, puede ser nulo
     * @return Devuelve una lista con las incidencias que tienen los valores deseados
     */
    public List<Incidencia> buscarIncidencias(String tipoIncidencia, EstadoIncidencia estadoIncidencia){
        List<Incidencia> listaResultado=new ArrayList<>();
        List<Incidencia> listaBusqueda=new ArrayList<>(incidenciaMap.values());

        boolean tipo=true, estado=true;

        if(tipoIncidencia.isEmpty()){
            tipo=false;
        }
        if(estadoIncidencia==null){
            estado=false;
        }

        for(int i=0; i<listaBusqueda.size(); i++){
            if(tipo && estado){
                if(listaBusqueda.get(i).tipo().equals(tipoIncidencia) && listaBusqueda.get(i).estado().equals(estadoIncidencia)){
                    listaResultado.add(listaBusqueda.get(i));
                }
            }
            else{
                if(tipo){
                    if(listaBusqueda.get(i).tipo().equals(tipoIncidencia)){
                        listaResultado.add(listaBusqueda.get(i));
                    }
                }
                if(estado){
                    if(listaBusqueda.get(i).estado().equals(estadoIncidencia)){
                        listaResultado.add(listaBusqueda.get(i));
                    }
                }
            }
        }

        return listaResultado;
    }

    /**
     * Método para que un administrador pueda borrar una incidencia
     * @param login identificador del usuario, debe ser admin
     * @param idIncidencia identificador de la incidencia que se elimina
     */
    public void borrarIncidencia(String login, int idIncidencia){
        if(login.equals("admin")){
            incidenciaMap.remove(idIncidencia);
        }
    }

    /**
     * Método para modificar el estado de una incidencia
     * @param login identificador del usuario, reservado para admin
     * @param estadoIncidencia valor del nuevo estado de la incidencia
     * @param idIncidencia identificador de la incidencia que se va a modificar
     */
    public void modificarEstadoIncidencia(String login, EstadoIncidencia estadoIncidencia, int idIncidencia){
        if(login.equals("admin")){
            incidenciaMap.get(idIncidencia).estado(estadoIncidencia);
        }
    }

    /**
     * Método para crear un nuevo tipo de incidencia
     * @param login identificador del usuario, reservado para admin
     * @param tipoIncidencia valor del nuevo tipo de incidencia creado
     */
    public void crearTipoIncidencia(String login, String tipoIncidencia){
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

    /**
     * Método para obtener la lista de los tipos de incidencias posibles
     * @return Devuelve una lista con los valores de los tipos de incidencias
     */
    public List<String> obtenerTipoIncidencia(){
        List<String> listaTipo=new ArrayList<>(tipoIncidencia);
        return listaTipo;
    }
}
