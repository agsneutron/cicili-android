package com.cicili.mx.cicili.domain;

import java.math.BigInteger;

public class AutotanquesCercanos {
    private Integer id;
    private Integer idAutotanque;
    private BigInteger idConcesionario;
    private Integer idConductor;
    private Double precio;
    private String conductor;
    private String concesionario;
    private String tiempoLlegada;
    private Double latitud;
    private Double longitud;

    public AutotanquesCercanos() {
    }


    public AutotanquesCercanos(Integer id, Integer idAutotanque, BigInteger idConcesionario, Integer idConductor, Double precio, String conductor, String concesionario, String tiempoLlegada, Double latitud, Double longitud) {
        this.id = id;
        this.idAutotanque = idAutotanque;
        this.idConcesionario = idConcesionario;
        this.idConductor = idConductor;
        this.precio = precio;
        this.conductor = conductor;
        this.concesionario = concesionario;
        this.tiempoLlegada = tiempoLlegada;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdAutotanque() {
        return idAutotanque;
    }

    public void setIdAutotanque(Integer idAutotanque) {
        this.idAutotanque = idAutotanque;
    }

    public BigInteger getIdConcesionario() {
        return idConcesionario;
    }

    public void setIdConcesionario(BigInteger idConcesionario) {
        this.idConcesionario = idConcesionario;
    }

    public Integer getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(Integer idConductor) {
        this.idConductor = idConductor;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getConductor() {
        return conductor;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public String getConcesionario() {
        return concesionario;
    }

    public void setConcesionario(String concesionario) {
        this.concesionario = concesionario;
    }

    public String getTiempoLlegada() {
        return tiempoLlegada;
    }

    public void setTiempoLlegada(String tiempoLlegada) {
        this.tiempoLlegada = tiempoLlegada;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
}
