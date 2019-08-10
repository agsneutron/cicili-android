package com.cicili.mx.cicili.domain;

public class Autotanque {
    private Integer id;
    private String placa;
    private String clave;
    private String color;
    private String marca;
    private String modelo;
    private Integer idMarca;
    private Integer idModelo;
    private Integer idRazonSocial;
    private Integer idCuenta;
    private Double latitud;
    private Double longitud;

    public Autotanque() {
    }

    public Autotanque(Integer id, String placa, String clave, String color, String marca, String modelo, Integer idMarca, Integer idModelo, Integer idRazonSocial, Integer idCuenta, Double latitud, Double longitud) {
        this.id = id;
        this.placa = placa;
        this.clave = clave;
        this.color = color;
        this.marca = marca;
        this.modelo = modelo;
        this.idMarca = idMarca;
        this.idModelo = idModelo;
        this.idRazonSocial = idRazonSocial;
        this.idCuenta = idCuenta;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(Integer idMarca) {
        this.idMarca = idMarca;
    }

    public Integer getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(Integer idModelo) {
        this.idModelo = idModelo;
    }

    public Integer getIdRazonSocial() {
        return idRazonSocial;
    }

    public void setIdRazonSocial(Integer idRazonSocial) {
        this.idRazonSocial = idRazonSocial;
    }

    public Integer getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Integer idCuenta) {
        this.idCuenta = idCuenta;
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
