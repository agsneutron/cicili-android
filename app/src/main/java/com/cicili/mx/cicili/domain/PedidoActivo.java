package com.cicili.mx.cicili.domain;

public class PedidoActivo {

    String id;
    String nombreConductor;
    String clave;
    String placa;
    String color;
    String tiempo;
    String monto;
    String latitud;
    String longitud;
    String nombreConcesionario;
    String nombreCliente;
    String logo;
    String tipo;
    String status;
    String nombreStatus;
    String horaSolicitada;
    String direccion;
    String fechaSolicitada;
    String fechaPedido;
    String razonSocial;
    String cantidad;
    String idCliente;
    String formaPago;



    public PedidoActivo() {
    }

    public PedidoActivo(String id, String nombreConductor, String clave, String placa, String color, String tiempo, String monto, String latitud, String longitud, String nombreConcesionario, String nombreCliente, String logo, String tipo, String status, String nombreStatus, String horaSolicitada, String direccion, String fechaSolicitada, String fechaPedido, String razonSocial, String cantidad, String idCliente, String formaPago) {
        this.id = id;
        this.nombreConductor = nombreConductor;
        this.clave = clave;
        this.placa = placa;
        this.color = color;
        this.tiempo = tiempo;
        this.monto = monto;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombreConcesionario = nombreConcesionario;
        this.nombreCliente = nombreCliente;
        this.logo = logo;
        this.tipo = tipo;
        this.status = status;
        this.nombreStatus = nombreStatus;
        this.horaSolicitada = horaSolicitada;
        this.direccion = direccion;
        this.fechaSolicitada = fechaSolicitada;
        this.fechaPedido = fechaPedido;
        this.razonSocial = razonSocial;
        this.cantidad = cantidad;
        this.idCliente = idCliente;
        this.formaPago = formaPago;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getNombreConductor() {
        return nombreConductor;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNombreStatus() {
        return nombreStatus;
    }

    public void setNombreStatus(String nombreStatus) {
        this.nombreStatus = nombreStatus;
    }

    public String getHoraSolicitada() {
        return horaSolicitada;
    }

    public void setHoraSolicitada(String horaSolicitada) {
        this.horaSolicitada = horaSolicitada;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFechaSolicitada() {
        return fechaSolicitada;
    }

    public void setFechaSolicitada(String fechaSolicitada) {
        this.fechaSolicitada = fechaSolicitada;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }
}
