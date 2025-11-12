package es.ujaen.dae.indicenciasurbanas.entidades;

import es.ujaen.dae.indicenciasurbanas.utils.CoordenadasGps;
import es.ujaen.dae.indicenciasurbanas.utils.EstadoIncidencia;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Incidencia {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @NotNull
    private LocalDateTime fecha;

    @ManyToOne
    @NotNull
    private TipoIncidencia tipo;

    @NotBlank
    private String descripcion;

    @NotBlank
    private String localizacion;

    @Embedded
    @NotNull
    private CoordenadasGps coordenadas;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EstadoIncidencia estado;

    @NotBlank
    private String dpto;// Departamento asignado

    @ManyToOne
    @Valid
    private Usuario usuario; //< Usuario que ha registrado la incidencia

    @Version
    private int version;

    public Incidencia(LocalDateTime fecha, TipoIncidencia tipo, String descripcion, String localizacion,
                      float latitud, float longitud,  String dpto,  Usuario usuario) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.localizacion = localizacion;
        this.coordenadas = new CoordenadasGps(latitud,longitud);
        this.estado = EstadoIncidencia.PENDIENTE; // Asignamos por defecto el estado PENDIENTE al ser el primer estado por el que debe pasar una Incidencia
        this.dpto = dpto;
        this.usuario = usuario;
    }

    public Incidencia() {}


    public int id() {
        return id;
    }

    public LocalDateTime fecha() {
        return fecha;
    }

    public CoordenadasGps coordenadas() {
        return coordenadas;
    }

    public void coordenadas(CoordenadasGps coordenadas) {
        this.coordenadas = coordenadas;
    }

    public TipoIncidencia tipo() {
        return tipo;
    }

    public void tipo(TipoIncidencia tipo) {
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

    public Usuario usuario() {
        return usuario;
    }

    public void usuario(Usuario usuario) {this.usuario = usuario; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Incidencia that = (Incidencia) o;

        return java.util.Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        // 5. El hashCode también debe basarse SOLO en el ID.
        return java.util.Objects.hash(id);
    }
}
