package com.cicili.mx.cicili.domain;

public class SeguimientoPedido {
    String idPedido;
    String conductor;
    String clave;
    String placa;
    String color;
    String tiempo;
    String monto;
    String latitud;
    String longitud;
    String concesionario;
    String logo;
    String tipo;
    String status;
    String nombreStatus;


    public SeguimientoPedido() {
    }

    public SeguimientoPedido(String idPedido, String conductor, String clave, String placa, String color, String tiempo, String monto, String latitud, String longitud, String concesionario, String logo, String tipo, String status, String nombreStatus) {
        this.idPedido = idPedido;
        this.conductor = conductor;
        this.clave = clave;
        this.placa = placa;
        this.color = color;
        this.tiempo = tiempo;
        this.monto = monto;
        this.latitud = latitud;
        this.longitud = longitud;
        this.concesionario = concesionario;
        this.logo = logo;
        this.tipo = tipo;
        this.status = status;
        this.nombreStatus = nombreStatus;
    }

    public SeguimientoPedido(String idPedido, String tipo, String status, String nombreStatus) {
        this.idPedido = idPedido;
        this.tipo = tipo;
        this.status = status;
        this.nombreStatus = nombreStatus;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getConductor() {
        return conductor;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getConcesionario() {
        return concesionario;
    }

    public void setConcesionario(String concesionario) {
        this.concesionario = concesionario;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
