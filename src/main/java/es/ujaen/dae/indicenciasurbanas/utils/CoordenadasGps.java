package es.ujaen.dae.indicenciasurbanas.utils;

import jakarta.validation.constraints.NotBlank;

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
}
