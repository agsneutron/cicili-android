package com.cicili.mx.cicili.domain;

public class SeguimientoData {
    Integer aclaracion;
    String texto;

    public SeguimientoData() {
    }

    public SeguimientoData(Integer aclaracion, String texto) {
        this.aclaracion = aclaracion;
        this.texto = texto;
    }

    public Integer getAclaracion() {
        return aclaracion;
    }

    public void setAclaracion(Integer aclaracion) {
        this.aclaracion = aclaracion;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
