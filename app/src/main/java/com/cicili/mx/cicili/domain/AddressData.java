package com.cicili.mx.cicili.domain;

import java.util.ArrayList;
import java.util.List;

public class AddressData {
    private Integer id;
    private String calle;
    private String exterior;
    private String interior;
    private String town;
    private String district;
    private String state;
    private Double latitud;
    private Double longitud;
    private String cp;
    private String alias;
    private Integer favorito;
    private Asentamiento asentamiento;



    public AddressData() {
    }

    public AddressData(Integer id, String calle, String exterior, String interior, String town, String district, String state, Double latitud, Double longitud, String cp, String alias, Integer favorito, Asentamiento asentamiento) {
        this.id = id;
        this.calle = calle;
        this.exterior = exterior;
        this.interior = interior;
        this.town = town;
        this.district = district;
        this.state = state;
        this.latitud = latitud;
        this.longitud = longitud;
        this.cp = cp;
        this.alias = alias;
        this.favorito = favorito;
        this.asentamiento = asentamiento;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getExterior() {
        return exterior;
    }

    public void setExterior(String exterior) {
        this.exterior = exterior;
    }

    public String getInterior() {
        return interior;
    }

    public void setInterior(String interior) {
        this.interior = interior;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getFavorito() {
        return favorito;
    }

    public void setFavorito(Integer favorito) {
        this.favorito = favorito;
    }

    public Asentamiento getAsentamiento() {
        return asentamiento;
    }

    public void setAsentamiento(Asentamiento asentamiento) {
        this.asentamiento = asentamiento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
