package es.ujaen.dae.indicenciasurbanas.repositorios;

import es.ujaen.dae.indicenciasurbanas.entidades.Incidencia;
import es.ujaen.dae.indicenciasurbanas.entidades.TipoIncidencia;
import es.ujaen.dae.indicenciasurbanas.utils.EstadoIncidencia;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching; // Importante añadir esta
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

    /**
     * Busca por ID. El resultado se cachea.
     * La caché se llama "incidenciasPorId" y la clave es el id.
     */
    @Cacheable(value = "incidenciasPorId", key = "#id", unless = "#result == null")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Incidencia> buscarPorId(int id) {
        return Optional.ofNullable(em.find(Incidencia.class, id));
    }

    /**
     * Busca por ID bloqueando.
     * El propósito de LockModeType.PESSIMISTIC_WRITE es bloquear la fila en la BD,
     * cachear el resultado impediría que el bloqueo se ejecute en llamadas subsecuentes.
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Incidencia> buscarPorIdBloqueando(int id) {
        return Optional.ofNullable(em.find(Incidencia.class, id, LockModeType.PESSIMISTIC_WRITE));
    }

    /**
     * Busca por email del usuario. El resultado se cachea.
     * La caché se llama "incidenciasPorEmail" y la clave es el email.
     */
    @Cacheable(value = "incidenciasPorEmail", key = "#email", unless = "#result == null || #result.isEmpty()")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Incidencia> buscarPorEmailUsuario(String email) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.usuario.email = ?1", Incidencia.class)
                .setParameter(1, email)
                .getResultList();
    }

    /**
     * Busca por tipo. El resultado se cachea.
     * La caché se llama "incidenciasPorTipo" y la clave es el objeto TipoIncidencia.
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Incidencia> buscarPorTipo(TipoIncidencia tipo) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.tipo = ?1", Incidencia.class)
                .setParameter(1, tipo)
                .getResultList();
    }

    /**
     * Busca por estado. El resultado se cachea.
     * La caché se llama "incidenciasPorEstado" y la clave es el Enum EstadoIncidencia.
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Incidencia> buscarPorEstado(EstadoIncidencia estado) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.estado = ?1", Incidencia.class)
                .setParameter(1, estado)
                .getResultList();
    }

    /**
     * Busca por tipo y estado. El resultado se cachea.
     * La caché se llama "incidenciasPorTipoYEstado" y la clave es una combinación de tipo y estado.
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Incidencia> buscarPorTipoYEstado(TipoIncidencia tipo, EstadoIncidencia estado) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.tipo = ?1 AND i.estado = ?2", Incidencia.class)
                .setParameter(1, tipo)
                .setParameter(2, estado)
                .getResultList();
    }

    /**
     * Busca todas. El resultado se cachea.
     * La caché se llama "todasIncidencias".
     */
    @Cacheable(value = "todasIncidencias", unless = "#result == null || #result.isEmpty()")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Incidencia> buscarTodas() {
        return em.createQuery("SELECT i FROM Incidencia i", Incidencia.class)
                .getResultList();
    }

    /**
     * Guarda una incidencia.
     * Evicta (limpia) todos los cachés de listas, ya que su contenido ha cambiado.
     */
    public void guardar(Incidencia incidencia) {
        em.persist(incidencia);
    }

    /**
     * Actualiza una incidencia.
     * Evict (limpia) el caché de esta incidencia por ID y también
     * todos los cachés de listas, ya que el estado, tipo, etc., porque podrían haber cambiado.
     */
    @Caching(evict = {
            @CacheEvict(value = "incidenciasPorId", key = "#incidencia.id()"),
            @CacheEvict(value = {"todasIncidencias", "incidenciasPorEmail", "incidenciasPorTipo", "incidenciasPorEstado", "incidenciasPorTipoYEstado"}, allEntries = true)
    })
    public Incidencia actualizar(Incidencia incidencia) {
        return em.merge(incidencia);
    }

    /**
     * Borra una incidencia.
     * Evict (limpia) el caché de esta incidencia por ID y también
     * todos los cachés de listas.
     */
    @Caching(evict = {
            @CacheEvict(value = "incidenciasPorId", key = "#incidencia.id()"),
            @CacheEvict(value = {"todasIncidencias", "incidenciasPorEmail", "incidenciasPorTipo", "incidenciasPorEstado", "incidenciasPorTipoYEstado"}, allEntries = true)
    })
    public void borrar(Incidencia incidencia) {
        em.remove(em.merge(incidencia));
    }


    public void comprobarErrores() {
        em.flush();
    }
}