package es.ujaen.dae.indicenciasurbanas.servicios;

import es.ujaen.dae.indicenciasurbanas.entidades.Incidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@Validated
public class ServicioIncidencia {
    private List<String> tipoIncidencia;
    private Map<String, Usuario> usuarioMap;
    private Map<Integer, Incidencia> incidenciaMap;

    public ServicioIncidencia() {}

    public ServicioIncidencia(List<String> tipoIncidencia) {
        this.tipoIncidencia=new ArrayList<>(tipoIncidencia);
        usuarioMap=new HashMap<>();
        incidenciaMap=new HashMap<>();
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
    public List<Incidencia> buscarIncidencias(String tipoIncidencia, String estadoIncidencia){
        List<Incidencia> listaResultado=new ArrayList<>();
        List<Incidencia> listaBusqueda=new ArrayList<>(incidenciaMap.values());

        boolean tipo=true, estado=true;

        if(tipoIncidencia.isEmpty()){
            tipo=false;
        }
        if(estadoIncidencia.isEmpty()){
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
    public void modificarEstadoIncidencia(String login, String estadoIncidencia, int idIncidencia){
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
            if (buscarIncidencias(tipoIncidencia, "").isEmpty()) {
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
