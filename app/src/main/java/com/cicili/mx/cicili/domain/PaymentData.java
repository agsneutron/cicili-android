package com.cicili.mx.cicili.domain;

public class PaymentData {

    private Integer id;
    private String status;
    private Integer tipoPago;
    //private String nombreTitular;
    private long numero;
    private String tipoTarjeta;
    //private Integer tipoCuenta;
    private String vencimiento;
    private Integer cvv;
    private String banco;
    //private Integer pais;


    public PaymentData() {
    }

    public PaymentData(Integer id, String status, Integer tipoPago, long numero, String tipoTarjeta, String vencimiento, Integer cvv, String banco) {
        this.id = id;
        this.status = status;
        this.tipoPago = tipoPago;
        this.numero = numero;
        this.tipoTarjeta = tipoTarjeta;
        this.vencimiento = vencimiento;
        this.cvv = cvv;
        this.banco = banco;
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


    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
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

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }
}
