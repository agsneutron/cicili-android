package com.cicili.mx.cicili.domain;

public class PerfilConductor {
    private Integer id;
    private Integer servicios;
    private Double litros;
    private Integer calificacion;
    private String miembroDesde;
    private String imagen;
    private Conductor conductor;

    public PerfilConductor() {
    }

    public PerfilConductor(Integer id, Integer servicios, Double litros, Integer calificacion, String miembroDesde, String imagen, Conductor conductor) {
        this.id = id;
        this.servicios = servicios;
        this.litros = litros;
        this.calificacion = calificacion;
        this.miembroDesde = miembroDesde;
        this.imagen = imagen;
        this.conductor = conductor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getServicios() {
        return servicios;
    }

    public void setServicios(Integer servicios) {
        this.servicios = servicios;
    }

    public Double getLitros() {
        return litros;
    }

    public void setLitros(Double litros) {
        this.litros = litros;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getMiembroDesde() {
        return miembroDesde;
    }

    public void setMiembroDesde(String miembroDesde) {
        this.miembroDesde = miembroDesde;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }


}
