package es.ujaen.dae.indicenciasurbanas.repositorios;

import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import es.ujaen.dae.indicenciasurbanas.excepciones.UsuarioYaRegistrado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

@Repository
@Transactional
public class RepositorioUsuarios {

    @PersistenceContext
    EntityManager em;

    /**
     * Busca un usuario por su email (PK).
     * El resultado se cachea en "usuarios" usando el email como clave.
     * Esto acelera muchísimo el login.
     */
    @Cacheable(value = "usuarios", key = "#email")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Usuario> buscar(String email) {
        return Optional.ofNullable(em.find(Usuario.class, email));
    }

    /**
     * Al guardar un usuario nuevo, no necesitamos borrar nada de la caché,
     * ya que este usuario aún no estaba cacheado.
     */
    public void guardar(Usuario usuario) {
        if (em.find(Usuario.class, usuario.email()) != null) {
            throw new UsuarioYaRegistrado();
        }
        em.persist(usuario);
    }

    /**
     * Al actualizar un usuario (ej. cambiar clave, dirección...),
     * debemos borrar su entrada de la caché "usuarios" para
     * forzar que la próxima llamada a buscar() lea los datos nuevos.
     */
    @CacheEvict(value = "usuarios", key = "#usuario.email()")
    public Usuario actualizar(Usuario usuario) {
        return em.merge(usuario);
    }

    /**
     * Al borrar un usuario, también borramos su entrada de la caché.
     */
    @CacheEvict(value = "usuarios", key = "#usuario.email()")
    public void borrar(Usuario usuario) {
        em.remove(em.contains(usuario) ? usuario : em.merge(usuario));
    }
}