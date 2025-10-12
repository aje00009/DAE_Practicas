package es.ujaen.dae.indicenciasurbanas.entidades;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class Incidencia {
    @Positive
    private Integer id;

    @NotNull
    private LocalDateTime fecha;

    @NotBlank
    private String tipo;

    @NotBlank
    private String descripcion;

    @NotBlank
    private String localizacion;

    // Coordenadas GPS
    @NotBlank
    private Float latitud;
    @NotBlank
    private Float longitud;

    @NotNull
    private EstadoIncidencia estado;

    @NotBlank
    private String dpto;// Departamento asignado

    @NotBlank
    private String emailUsuario; //< Email del usuario que ha registrado la Incidencia


    public Incidencia(Integer id, LocalDateTime fecha, String tipo, String descripcion, String localizacion,
                      Float latitud, Float longitud,  String dpto,  String emailUsuario) {
        this.id = id;
        this.fecha = fecha;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.localizacion = localizacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estado =EstadoIncidencia.PENDIENTE; // Asignamos por defecto el estado PENDIENTE al ser el primer estado por el que debe pasar una Incidencia
        this.dpto = dpto;
        this.emailUsuario = emailUsuario;
    }


    public Integer id() {
        return id;
    }

    public LocalDateTime fecha() {
        return fecha;
    }

    public Float latitud() {
        return latitud;
    }

    public Float longitud() {
        return longitud;
    }


    public String tipo() {
        return tipo;
    }

    public void tipo(String tipo) {
        this.tipo = tipo;
    }

    public String descripcion() {
        return descripcion;
    }

    public void descripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String localizacion() {
        return localizacion;
    }

    public void localizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public EstadoIncidencia estado() {
        return estado;
    }

    public void estado(EstadoIncidencia estado) {
        this.estado = estado;
    }

    public String dpto() {
        return dpto;
    }

    public void dpto(String dpto) {
        this.dpto = dpto;
    }

    public String emailUsuario() {
        return emailUsuario;
    }

    public void emailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }
}
