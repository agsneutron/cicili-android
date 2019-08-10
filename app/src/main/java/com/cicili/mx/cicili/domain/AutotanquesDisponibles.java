package com.cicili.mx.cicili.domain;

import java.math.BigInteger;

public class AutotanquesDisponibles {

    private Integer id;
    private Integer idAutotanque;
    private BigInteger idConcesionario;
    private Integer idConductor;
    private Double precio;
    private Concesionario concecionario;
    private PerfilConductor perfilConductor;
    private Autotanque autotanque;

    public AutotanquesDisponibles() {
    }

    public AutotanquesDisponibles(Integer id, Integer idAutotanque, BigInteger idConcesionario, Integer idConductor, Double precio, Concesionario concecionario, PerfilConductor perfilConductor, Autotanque autotanque) {
        this.id = id;
        this.idAutotanque = idAutotanque;
        this.idConcesionario = idConcesionario;
        this.idConductor = idConductor;
        this.precio = precio;
        this.concecionario = concecionario;
        this.perfilConductor = perfilConductor;
        this.autotanque = autotanque;
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

    public Concesionario getConcecionario() {
        return concecionario;
    }

    public void setConcecionario(Concesionario concecionario) {
        this.concecionario = concecionario;
    }

    public PerfilConductor getPerfilConductor() {
        return perfilConductor;
    }

    public void setPerfilConductor(PerfilConductor perfilConductor) {
        this.perfilConductor = perfilConductor;
    }

    public Autotanque getAutotanque() {
        return autotanque;
    }

    public void setAutotanque(Autotanque autotanque) {
        this.autotanque = autotanque;
    }
}
