package es.ujaen.dae.indicenciasurbanas.utils;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public class CoordenadasGps {

    @NotBlank
    private float latitud;
    @NotBlank
    private float longitud;

    public CoordenadasGps(float latitud, float longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public float latitud() {
        return latitud;
    }

    public void latitud(float latitud) {
        this.latitud = latitud;
    }

    public Float longitud() {
        return longitud;
    }

    public void longitud(float longitud) {
        this.longitud = longitud;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordenadasGps that = (CoordenadasGps) o;
        return Float.compare(that.latitud, latitud) == 0 &&
                Float.compare(that.longitud, longitud) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitud, longitud);
    }
}
