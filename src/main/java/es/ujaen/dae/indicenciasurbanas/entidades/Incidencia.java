package es.ujaen.dae.indicenciasurbanas.entidades;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class Incidencia {
    @Positive
    private int id;

    @NotNull
    private LocalDateTime fecha;

    @NotBlank
    private String tipo;

    @NotBlank
    private String descripcion;

    @NotBlank
    private String localizacion;

    // Coordenadas GPS
    private float latitud;
    private float longitud;

    @NotBlank
    @Pattern(regexp = "^(resuelta|en evaluación|pendiente)$")
    private String estado;

    private String dpto; // Departamento asignado

    private String loginUsuario; //< Email del usuario que ha registrado la Incidencia


    public Incidencia(int id, LocalDateTime fecha, String tipo, String descripcion, String localizacion,
                      Float latitud, Float longitud, String estado, String dpto,  String loginUsuario) {
        this.id = id;
        this.fecha = fecha;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.localizacion = localizacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estado = estado;
        this.dpto = dpto;
        this.loginUsuario = loginUsuario;
    }


    public int id() {
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

    public String estado() {
        return estado;
    }

    public void estado(String estado) {
        this.estado = estado;
    }

    public String dpto() {
        return dpto;
    }

    public void dpto(String dpto) {
        this.dpto = dpto;
    }

    public String loginUsuario() {
        return loginUsuario;
    }

    public void loginUsuario(String loginUsuario) {
        this.loginUsuario = loginUsuario;
    }
}
