package com.cicili.mx.cicili.domain;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

public class Asentamiento {

    private Integer id;
    private String text;
    private String nombre;
    private Integer cp;
    private String municipio;
    private String estado;
    private String pais;


    public Asentamiento() {
    }

    public Asentamiento(Integer id, String text, String nombre, Integer cp, String municipio, String estado, String pais) {
        this.id = id;
        this.text = text;
        this.nombre = nombre;
        this.cp = cp;
        this.municipio = municipio;
        this.estado = estado;
        this.pais = pais;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCp() {
        return cp;
    }

    public void setCp(Integer cp) {
        this.cp = cp;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
}
