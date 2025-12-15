package es.ujaen.dae.indicenciasurbanas.rest.dto;

import es.ujaen.dae.indicenciasurbanas.utils.EstadoIncidencia;
import java.time.LocalDateTime;

public record DIncidencia(
        int id,
        LocalDateTime fecha,
        String tipo,
        String descripcion,
        String localizacion,
        float latitud,
        float longitud,
        EstadoIncidencia estado,
        String dpto,
        String emailUsuario
) {}