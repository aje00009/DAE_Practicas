package es.ujaen.dae.indicenciasurbanas.utils;

public class DistanciaCoordenadas{

    private static final double RADIO_TIERRA_M = 6371000.0; // Radio de la Tierra en metros

    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula de haversine
     * @param coordenada1 coordenadas GPS de la primera incidencia
     * @param coordenada2 coordenadas GPS de la segunda incidencia
     * @return Distancia en metros
     */
    public static double calcularDistanciaMetros(CoordenadasGps coordenada1, CoordenadasGps coordenada2) {
        // Convertir grados a radianes
        double lat1Rad = Math.toRadians(coordenada1.latitud());
        double lon1Rad = Math.toRadians(coordenada1.longitud());
        double lat2Rad = Math.toRadians(coordenada2.latitud());
        double lon2Rad = Math.toRadians(coordenada2.longitud());

        // Diferencias
        double difLat = lat2Rad - lat1Rad;
        double difLon = lon2Rad - lon1Rad;

        // Fórmula de haversine
        double a = Math.sin(difLat / 2) * Math.sin(difLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(difLon / 2) * Math.sin(difLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distancia en metros
        return RADIO_TIERRA_M * c;
    }
}