package es.ujaen.dae.indicenciasurbanas.rest.dto;

import es.ujaen.dae.indicenciasurbanas.entidades.Incidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.TipoIncidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import es.ujaen.dae.indicenciasurbanas.excepciones.TipoIncidenciaNoExiste;
import es.ujaen.dae.indicenciasurbanas.excepciones.UsuarioNoExiste;
import es.ujaen.dae.indicenciasurbanas.repositorios.RepositorioTipoIncidencia;
import es.ujaen.dae.indicenciasurbanas.repositorios.RepositorioUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class Mapeador {
    @Autowired
    RepositorioUsuarios repositorioUsuarios;

    @Autowired
    RepositorioTipoIncidencia repositorioTipoIncidencia;

    // USUARIO
    public DUsuario dto(Usuario usuario){
        return new DUsuario(usuario.nombre(),
                usuario.apellido(),
                usuario.fNacimiento(),
                usuario.direccion(),
                usuario.telefono(),
                usuario.email(),
                "");
    }

    public Usuario entidad(DUsuario dUsuario){
        return new Usuario(dUsuario.nombre(),
                dUsuario.apellido(),
                dUsuario.fNacimiento(),
                dUsuario.direccion(),
                dUsuario.telefono(),
                dUsuario.email(),
                dUsuario.clave());
    }

    // TIPO INCIDENCIA
    public DTipoIncidencia dto(TipoIncidencia tipo){
        return new DTipoIncidencia(tipo.nombre());
    }

    public TipoIncidencia entidad(DTipoIncidencia dTipoIncidencia){
        return new TipoIncidencia(dTipoIncidencia.nombre());
    }

    // INCIDENCIA
    public DIncidencia dto(Incidencia incidencia){
        return new DIncidencia(incidencia.id(),
                incidencia.fecha(),
                incidencia.tipo().nombre(),
                incidencia.descripcion(),
                incidencia.localizacion(),
                incidencia.coordenadas().latitud(),
                incidencia.coordenadas().longitud(),
                incidencia.estado(),
                incidencia.dpto(),
                incidencia.usuario().email());
    }

    public Incidencia entidad(DIncidencia dIncidencia){
        // Buscamos el usuario y tipo de incidencia en la BBDD
        Usuario usuario = repositorioUsuarios.buscar(dIncidencia.emailUsuario())
                .orElseThrow(UsuarioNoExiste::new);

        TipoIncidencia tipo = repositorioTipoIncidencia.buscarPorNombre(dIncidencia.tipo())
                .orElseThrow(TipoIncidenciaNoExiste::new);

        return new Incidencia(dIncidencia.fecha(),
                tipo,
                dIncidencia.descripcion(),
                dIncidencia.localizacion(),
                dIncidencia.latitud(),
                dIncidencia.longitud(),
                dIncidencia.dpto(),
                usuario,
                null);
    }
}
