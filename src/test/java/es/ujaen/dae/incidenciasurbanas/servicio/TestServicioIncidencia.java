package es.ujaen.dae.incidenciasurbanas.servicio;

import es.ujaen.dae.indicenciasurbanas.entidades.Incidencia;
import es.ujaen.dae.indicenciasurbanas.servicios.ServicioIncidencia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Date;

@SpringBootTest//(classes = es.ujaen.dae.incidenciasurbanas.servicio.ServicioIncidencia)
public class TestServicioIncidencia {

    @Autowired
    ServicioIncidencia servicioIncidencia;

    @Test
    public void testNuevaIncidencia() {

        //Crear Incidencia con estado invalido
        Incidencia incidencia=new Incidencia(1, LocalDateTime.now(), "tipo", "desc", "loc", (float) 1.0, (float) 1.0, "estado", "dpt", "usuario");
        servicioIncidencia.nuevaIncidencia(incidencia);

        // Inserción correcta
        incidencia=new Incidencia(1, LocalDateTime.now(), "tipo", "desc", "loc", (float) 1.0, (float) 1.0, "pendiente", "dpt", "usuario");
        servicioIncidencia.nuevaIncidencia(incidencia);

        //Porbar insertar elemento repetido
        servicioIncidencia.nuevaIncidencia(incidencia);
    }

    @Test
    public void testObtenerIncidenciasUsuario(){
        // Probar que se obtiene la lista de incidencias del usuario correcto
    }

    @Test
    public void testBuscarIncidencias(){
        // Probar a buscar por solo tipo

        // Porbar a buscar por solo estado

        // Probar a buscar por tipo y estado

        // Porbar a buscar sin especificaciones
    }

    @Test
    public void testBorrarIncidencia(){
        // Probar a borrar como admin

        // Probar a borrar como usuario de la incidencia con estado invalido

        // Porbar a borrar como usuario de la incidencia con estado valido

        // Probar a borrar como usuario ajeno a la incidencia
    }

    @Test
    public void testModificarEstadoIncidencia(){
        // Probar a modificar como admin

        // Porbar a modificar como usuario normal
    }

    @Test
    public void testCrearTipoIncidencia(){
        // Probar a crear como admin

        // Probar a crear como usuario normal
    }

    @Test
    public void testBorrarTipoIncidencia(){
        // Probar a borrar como admin con incidencias de ese tipo

        // Probar a borrar como admin sin incidencias de ese tipo

        // Probar a borrar como usuario normal
    }

    @Test
    public void testObtenerTipoIncidencia(){

    }
}
