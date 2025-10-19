package es.ujaen.dae.indicenciasurbanas.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"es.ujaen.dae.indicenciasurbanas"})
public class DaePracticaApplication {
    public static void main(String[] args) {

        SpringApplication.run(DaePracticaApplication.class, args);
    }
}
