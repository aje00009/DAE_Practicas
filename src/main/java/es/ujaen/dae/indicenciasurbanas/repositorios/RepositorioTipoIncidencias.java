package es.ujaen.dae.indicenciasurbanas.repositorios;

import es.ujaen.dae.indicenciasurbanas.entidades.TipoIncidencia;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public class RepositorioTipoIncidencias {
    @PersistenceContext
    private EntityManager em;

    public TipoIncidencia getTipoIncidencia(int id) {
        return em.find(TipoIncidencia.class, id);
    }

    public List<TipoIncidencia> getTipoIncidencias() {
        return em.createQuery("select t from TipoIncidencia t")
                .getResultList();
    }

    // Creación del objeto en BBDD
    public void guardarTipoIncidencia(TipoIncidencia tipoIncidencia) {
        em.persist(tipoIncidencia);
    }

    // Actualizar el objeto en BBDD
    public void actualizarTipoIncidencia(TipoIncidencia tipoIncidencia) {
        em.persist(tipoIncidencia);
    }

    // Quita el objeto de BBDD
    public void borrarTipoIncidencia(TipoIncidencia tipoIncidencia) {
        em.remove(tipoIncidencia);
    }

}
