package com.cicili.mx.cicili.domain;

public class ComunicaCC {

    Integer idPedido;
    String mensaje;

    public ComunicaCC() {
    }

    public ComunicaCC(Integer idPedido, String mensaje) {
        this.idPedido = idPedido;
        this.mensaje = mensaje;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
