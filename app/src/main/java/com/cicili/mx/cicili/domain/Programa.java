package com.cicili.mx.cicili.domain;

public class Programa {

    public Double cantidad;
    public Double monto;
    public AddressData domicilio;
    public Double latitud;
    public Double longitud;
    public String formaPago;
    public String fechaSolicitada;
    public String horaSolicitada;

    public Programa() {
    }

    public Programa(Double cantidad, Double monto, AddressData domicilio, Double latitud, Double longitud, String formaPago, String fechaSolicitada, String horaSolicitada) {
        this.cantidad = cantidad;
        this.monto = monto;
        this.domicilio = domicilio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.formaPago = formaPago;
        this.fechaSolicitada = fechaSolicitada;
        this.horaSolicitada = horaSolicitada;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
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

    public String getFechaSolicitada() {
        return fechaSolicitada;
    }

    public void setFechaSolicitada(String fechaSolicitada) {
        this.fechaSolicitada = fechaSolicitada;
    }

    public String getHoraSolicitada() {
        return horaSolicitada;
    }

    public void setHoraSolicitada(String horaSolicitada) {
        this.horaSolicitada = horaSolicitada;
    }
}
