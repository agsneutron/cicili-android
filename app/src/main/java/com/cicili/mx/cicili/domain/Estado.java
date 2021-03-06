package com.cicili.mx.cicili.domain;

import java.util.ArrayList;

public class Estado {

    Integer id;
    String nombre;
    Pais pais;

    public Estado() {
    }

    public Estado(Integer id, String nombre, Pais pais) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }
}
