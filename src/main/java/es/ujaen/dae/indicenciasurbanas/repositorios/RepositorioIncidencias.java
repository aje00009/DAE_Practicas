package es.ujaen.dae.indicenciasurbanas.repositorios;

import es.ujaen.dae.indicenciasurbanas.entidades.Incidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.TipoIncidencia;
import es.ujaen.dae.indicenciasurbanas.utils.EstadoIncidencia;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class RepositorioIncidencias {

    @PersistenceContext
    EntityManager em;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Incidencia> buscarPorId(int id) {
        return Optional.ofNullable(em.find(Incidencia.class, id));
    }


    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Incidencia> buscarPorIdBloqueando(int id) {
        return Optional.ofNullable(em.find(Incidencia.class, id, LockModeType.PESSIMISTIC_WRITE));
    }


    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Incidencia> buscarPorEmailUsuario(String email) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.emailUsuario = ?1", Incidencia.class)
                .setParameter(1, email)
                .getResultList();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Incidencia> buscarPorTipo(TipoIncidencia tipo) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.tipo = ?1", Incidencia.class)
                .setParameter(1, tipo)
                .getResultList();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Incidencia> buscarPorEstado(EstadoIncidencia estado) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.estado = ?1", Incidencia.class)
                .setParameter(1, estado)
                .getResultList();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Incidencia> buscarPorTipoYEstado(TipoIncidencia tipo, EstadoIncidencia estado) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.tipo = ?1 AND i.estado = ?2", Incidencia.class)
                .setParameter(1, tipo)
                .setParameter(2, estado)
                .getResultList();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Incidencia> buscarTodas() {
        return em.createQuery("SELECT i FROM Incidencia i", Incidencia.class)
                .getResultList();
    }

    public void guardar(Incidencia incidencia) {
        em.persist(incidencia);
    }

    public Incidencia actualizar(Incidencia incidencia) {
        return em.merge(incidencia);
    }

    public void borrar(Incidencia incidencia) {
        em.remove(em.merge(incidencia));
    }
}