package com.cicili.mx.cicili.domain;

import java.math.BigInteger;

public class Pedido {

    private Long cantidad;
    private Long monto;
    private AddressData domicilio;
    private Double latitud;
    private Double longitud;

    public Pedido() {
    }

    public Pedido(Long cantidad, Long monto, AddressData domicilio, Double latitud, Double longitud) {
        this.cantidad = cantidad;
        this.monto = monto;
        this.domicilio = domicilio;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    public Long getMonto() {
        return monto;
    }

    public void setMonto(Long monto) {
        this.monto = monto;
    }

    public AddressData getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(AddressData domicilio) {
        this.domicilio = domicilio;
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
