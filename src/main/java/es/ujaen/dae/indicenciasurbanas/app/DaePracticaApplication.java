package es.ujaen.dae.indicenciasurbanas.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"es.ujaen.dae.indicenciasurbanas"})
@EntityScan(basePackages = {"es.ujaen.dae.indicenciasurbanas"})
@EnableJpaRepositories(basePackages = "es.ujaen.dae.indicenciasurbanas")
@EnableCaching
public class DaePracticaApplication {
    public static void main(String[] args) {

        SpringApplication.run(DaePracticaApplication.class, args);
    }
}
