package com.cicili.mx.cicili.domain;

import java.math.BigInteger;

public class Pedido {

    private Double cantidad;
    private Double monto;
    private AddressData domicilio;
    private Double latitud;
    private Double longitud;
    private String formaPago;
    private Integer idAutotanque;

    public Pedido() {
    }

    public Pedido(Double cantidad, Double monto, AddressData domicilio, Double latitud, Double longitud, String formaPago, Integer idAutotanque) {
        this.cantidad = cantidad;
        this.monto = monto;
        this.domicilio = domicilio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.formaPago = formaPago;
        this.idAutotanque = idAutotanque;
    }



    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
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

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public Integer getIdAutotanque() {
        return idAutotanque;
    }

    public void setIdAutotanque(Integer idAutotanque) {
        this.idAutotanque = idAutotanque;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }
}
