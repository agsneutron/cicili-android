package com.cicili.mx.cicili.domain;

public class AclaracionData {

    Integer idPedido;
    String aclaracion;
    Categorias tipoAclaracion;

    public AclaracionData() {
    }

    public AclaracionData(Integer idPedido, String aclaracion, Categorias tipoAclaracion) {
        this.idPedido = idPedido;
        this.aclaracion = aclaracion;
        this.tipoAclaracion = tipoAclaracion;
    }


    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public String getAclaracion() {
        return aclaracion;
    }

    public void setAclaracion(String aclaracion) {
        this.aclaracion = aclaracion;
    }

    public Categorias getTipoAclaracion() {
        return tipoAclaracion;
    }

    public void setTipoAclaracion(Categorias tipoAclaracion) {
        this.tipoAclaracion = tipoAclaracion;
    }
}
