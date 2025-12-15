package es.ujaen.dae.indicenciasurbanas.rest.dto;

import java.time.LocalDate;

public record DUsuario(
        String nombre,
        String apellido,
        LocalDate fNacimiento,
        String direccion,
        String telefono,
        String email,
        String clave
) {}
