package es.ujaen.dae.indicenciasurbanas.entidades;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class Usuario {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotBlank
    private LocalDate fNacimiento;

    @NotBlank
    private String direccion;

    @Pattern(regexp = "^(\\+34|0034|34)?[6789]\\d{8}$", message = "No es un número de teléfono válido")
    private Integer telefono;

    @Email
    private String email;

    @NotBlank
    private String clave;

    public Usuario(String nombre, String apellido, LocalDate fNac,
           String direccion, Integer telefono, String email, String clave) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fNacimiento = fNac;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.clave = clave;
    }

    public void nombre(String nombre){
        this.nombre = nombre;
    }

    public String nombre(){
        return this.nombre;
    }

    public void apellido(String apellido){
        this.apellido = apellido;
    }

    public String apellido(){
        return this.apellido;
    }

    public void fNacimiento(LocalDate fNacimiento){
        this.fNacimiento = fNacimiento;
    }

    public LocalDate fNacimiento(){
        return this.fNacimiento;
    }

    public void direccion(String direccion){
        this.direccion = direccion;
    }

    public String direccion(){
        return this.direccion;
    }

    public void telefono(Integer telefono){
        this.telefono = telefono;
    }

    public Integer telefono(){
        return this.telefono;
    }

    public void email(String email){
        this.email = email;
    }

    public String email(){
        return this.email;
    }

    public void  clave(String clave){
        this.clave = clave;
    }

    public String clave(){
        return this.clave;
    }
}
