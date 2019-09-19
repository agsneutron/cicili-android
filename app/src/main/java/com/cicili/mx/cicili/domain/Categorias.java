package com.cicili.mx.cicili.domain;

public class Categorias {


    Integer id;
    String text;

    public Categorias() {
    }

    public Categorias(Integer id, String text) {
        this.id = id;
        this.text = text;
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


}
