package es.ujaen.dae.indicenciasurbanas.entidades;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class TipoIncidencia {
    @Positive
    private int id;
    @NotBlank
    private String nombre;

    public TipoIncidencia(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int id() {
        return id;
    }

    public void id(int id) {
        this.id = id;
    }

    public String nombre() {
        return nombre;
    }

    public void nombre(String nombre) {
        this.nombre = nombre;
    }
}
