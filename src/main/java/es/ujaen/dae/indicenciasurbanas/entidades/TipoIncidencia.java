package es.ujaen.dae.indicenciasurbanas.entidades;

public class TipoIncidencia {
    private int id;
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
