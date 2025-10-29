package es.ujaen.dae.indicenciasurbanas.entidades;

import es.ujaen.dae.indicenciasurbanas.utils.CoordenadasGps;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

@Entity
public class TipoIncidencia {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Positive
    private int id;

    @NotBlank
    private String nombre;

    public TipoIncidencia(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public TipoIncidencia() {}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipoIncidencia that = (TipoIncidencia) o;
        return Objects.equals(nombre, that.nombre); // Solo por nombre
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre); // Solo por nombre
    }
}
