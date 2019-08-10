package com.cicili.mx.cicili.domain;

public class PaymentData {

    private Integer id;
    private String status;
    private Integer tipoPago;
    private String nombreTitular;
    private long numero;
    private Integer tipoTarjeta;
    private Integer tipoCuenta;
    private String vencimiento;
    private Integer cvv;
    private Integer pais;


    public PaymentData() {
    }

    public PaymentData(Integer id, String status, Integer tipoPago, String nombreTitular, Integer numero, Integer tipoTarjeta, Integer tipoCuenta, String vencimiento, Integer cvv, Integer pais) {
        this.id = id;
        this.status = status;
        this.tipoPago = tipoPago;
        this.nombreTitular = nombreTitular;
        this.numero = numero;
        this.tipoTarjeta = tipoTarjeta;
        this.tipoCuenta = tipoCuenta;
        this.vencimiento = vencimiento;
        this.cvv = cvv;
        this.pais = pais;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(Integer tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public Integer getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(Integer tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public Integer getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(Integer tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getVencimiento() {
        return vencimiento;
    }

    public void setVencimiento(String vencimiento) {
        this.vencimiento = vencimiento;
    }

    public Integer getCvv() {
        return cvv;
    }

    public void setCvv(Integer cvv) {
        this.cvv = cvv;
    }

    public Integer getPais() {
        return pais;
    }

    public void setPais(Integer pais) {
        this.pais = pais;
    }
}
