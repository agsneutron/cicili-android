package com.cicili.mx.cicili.domain;

public class PedidoData {

    private Integer id;
    private Double cantidad;
    private Double monto;
    private AddressData domicilio;
    private Double latitud;
    private Double longitud;
    private String formaPago;
    private String alias;
    private Integer idAutotanque;
    private String fechaPedido;
    private String fechaSolicitada;
    private String horaSolicitada;
    private Integer status;
    private Integer idCliente;
    private String nombreCliente;
    private String placa;
    private String direccion;
    private String nombreStatus;


    public PedidoData() {
    }

    public PedidoData(Integer id, Double cantidad, Double monto, AddressData domicilio, Double latitud, Double longitud, String formaPago, String alias, Integer idAutotanque, String fechaPedido, String fechaSolicitada, String horaSolicitada, Integer status, Integer idCliente, String nombreCliente, String placa, String direccion, String nombreStatus) {
        this.id = id;
        this.cantidad = cantidad;
        this.monto = monto;
        this.domicilio = domicilio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.formaPago = formaPago;
        this.alias = alias;
        this.idAutotanque = idAutotanque;
        this.fechaPedido = fechaPedido;
        this.fechaSolicitada = fechaSolicitada;
        this.horaSolicitada = horaSolicitada;
        this.status = status;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.placa = placa;
        this.direccion = direccion;
        this.nombreStatus = nombreStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getIdAutotanque() {
        return idAutotanque;
    }

    public void setIdAutotanque(Integer idAutotanque) {
        this.idAutotanque = idAutotanque;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombreStatus() {
        return nombreStatus;
    }

    public void setNombreStatus(String nombreStatus) {
        this.nombreStatus = nombreStatus;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
