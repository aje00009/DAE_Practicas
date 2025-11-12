package es.ujaen.dae.indicenciasurbanas.repositorios;

import es.ujaen.dae.indicenciasurbanas.entidades.TipoIncidencia;
import es.ujaen.dae.indicenciasurbanas.excepciones.TipoIncidenciaEnUso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RepositorioTipoIncidencia {
    @PersistenceContext
    EntityManager em;

    /**
     * Busca por ID. El resultado se cachea.
     * La caché se llama "tiposPorId" y la clave es el propio id.
     */
    @Cacheable(value = "tiposPorId", key = "#id", unless = "#result == null")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<TipoIncidencia> buscarPorId(int id) {
        return Optional.ofNullable(em.find(TipoIncidencia.class, id));
    }

    /**
     * Busca por nombre. El resultado se cachea.
     * La caché se llama "tiposPorNombre" y la clave es el nombre.
     */
    @Cacheable(value = "tiposPorNombre", key = "#nombre", unless = "#result == null")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<TipoIncidencia> buscarPorNombre(String nombre) {
        List<TipoIncidencia> resultados = em.createQuery(
                        "SELECT t FROM TipoIncidencia t WHERE t.nombre = :nombre", TipoIncidencia.class)
                .setParameter("nombre", nombre)
                .getResultList();

        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
    }

    /**
     * Busca todos. El resultado se cachea.
     * La caché se llama "todosTipos" y guarda la lista completa.
     */
    @Cacheable("todosTipos")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<TipoIncidencia> buscarTodos() {
        return em.createQuery("SELECT t FROM TipoIncidencia t", TipoIncidencia.class)
                .getResultList();
    }

    /**
     * Al guardar, borramos la caché "todosTipos" porque la lista ha cambiado.
     * No borramos las otras, ya que es un elemento nuevo.
     */
    public void guardar(TipoIncidencia tipoIncidencia) {
        em.persist(tipoIncidencia);
    }

    /**
     * Al actualizar, borramos todas las cachés relacionadas.
     * - "todosTipos": porque la lista ha cambiado.
     * - "tiposPorId": porque este ID puede tener datos nuevos.
     * - "tiposPorNombre": porque este nombre puede tener datos nuevos.
     */
    @Caching(evict = {
            @CacheEvict(value = "todosTipos", allEntries = true),
            @CacheEvict(value = "tiposPorId", key = "#tipoIncidencia.id()"),
            @CacheEvict(value = "tiposPorNombre", key = "#tipoIncidencia.nombre()")
    })
    public TipoIncidencia actualizar(TipoIncidencia tipoIncidencia) {
        return em.merge(tipoIncidencia);
    }

    /**
     * Al borrar, borramos todas las cachés, igual que al actualizar.
     */
    @Caching(evict = {
            @CacheEvict(value = "todosTipos", allEntries = true),
            @CacheEvict(value = "tiposPorId", key = "#tipoIncidencia.id()"),
            @CacheEvict(value = "tiposPorNombre", key = "#tipoIncidencia.nombre()")
    })
    public void borrar(TipoIncidencia tipoIncidencia) {
        em.remove(em.contains(tipoIncidencia) ? tipoIncidencia : em.merge(tipoIncidencia));
    }
}
