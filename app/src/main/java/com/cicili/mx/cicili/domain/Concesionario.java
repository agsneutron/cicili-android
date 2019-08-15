package com.cicili.mx.cicili.domain;

public class Concesionario {
   Long  idConcesionario;
   String descripcion;
   Integer status;
   Integer token;
   String password;
   String correoElectronico;
   String nombre;
   String apellidoPaterno;
   String apellidoMaterno;
   String telefono;
   String imagen;

    public Concesionario() {
    }

    public Concesionario(Long idConcesionario, String descripcion, Integer status, Integer token, String password, String correoElectronico, String nombre, String apellidoPaterno, String apellidoMaterno, String telefono, String imagen) {
        this.idConcesionario = idConcesionario;
        this.descripcion = descripcion;
        this.status = status;
        this.token = token;
        this.password = password;
        this.correoElectronico = correoElectronico;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.telefono = telefono;
        this.imagen = imagen;
    }

    public Long getId() {
        return idConcesionario;
    }

    public void setId(Long idConcesionario) {
        this.idConcesionario = idConcesionario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getToken() {
        return token;
    }

    public void setToken(Integer token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
