package es.ujaen.dae.indicenciasurbanas.utils;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Embeddable
public class CoordenadasGps {

    private float latitud;
    private float longitud;

    public CoordenadasGps(float latitud, float longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public CoordenadasGps() {}

    public float latitud() {
        return latitud;
    }

    public void latitud(float latitud) {
        this.latitud = latitud;
    }

    public float longitud() {
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
