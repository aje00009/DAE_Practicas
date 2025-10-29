package es.ujaen.dae.indicenciasurbanas.repositorios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public class RepositorioIncidencias {
    @PersistenceContext
    private EntityManager em;

}
