package com.cicili.mx.cicili.domain;

public class PedidoDetail {

    private Integer id;
    private Double cantidad;
    private String fechaPedido;
    private String fechaSolicitada;
    private String horaSolicitada;
    private Double monto;
    private Integer status;
    private Integer idcliente;
    private String nombreConductor;
    private String nombreConcesionario;
    private String nombreCliente;
    private String formaPago;
    private String placa;
    private String direccion;
    private String alias;
    private String nombreStatus;

    public PedidoDetail() {

    }

    public PedidoDetail(Integer id, Double cantidad, String fechaPedido, String fechaSolicitada, String horaSolicitada, Double monto, Integer status, Integer idcliente, String nombreConductor, String nombreConcesionario, String nombreCliente, String formaPago, String placa, String direccion, String alias, String nombreStatus) {
        this.id = id;
        this.cantidad = cantidad;
        this.fechaPedido = fechaPedido;
        this.fechaSolicitada = fechaSolicitada;
        this.horaSolicitada = horaSolicitada;
        this.monto = monto;
        this.status = status;
        this.idcliente = idcliente;
        this.nombreConductor = nombreConductor;
        this.nombreConcesionario = nombreConcesionario;
        this.nombreCliente = nombreCliente;
        this.formaPago = formaPago;
        this.placa = placa;
        this.direccion = direccion;
        this.alias = alias;
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

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(Integer idcliente) {
        this.idcliente = idcliente;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
    }

    public String getNombreConcesionario() {
        return nombreConcesionario;
    }

    public void setNombreConcesionario(String nombreConcesionario) {
        this.nombreConcesionario = nombreConcesionario;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNombreStatus() {
        return nombreStatus;
    }

    public void setNombreStatus(String nombreStatus) {
        this.nombreStatus = nombreStatus;
    }
}
