package com.cicili.mx.cicili.domain;

/**
 * Created by ariaocho on 27/06/19.
 */

public class Conductor {
    private Integer id;
    private String ine;
    private String licencia;
    private String curp;
    private Integer status;
    private String token;
    private String correoElectronico;
    private String password;
    private String nombre;
    private Long telefono;
    private String pellidoPaterno;
    private String apellidoMaterno;
    private Integer idAutotanque;
    private String imagen;

    public Conductor() {
    }

    public Conductor(Integer id, String ine, String licencia, String curp, Integer status, String token, String correoElectronico, String password, String nombre, Long telefono, String pellidoPaterno, String apellidoMaterno, Integer idAutotanque, String imagen) {
        this.id = id;
        this.ine = ine;
        this.licencia = licencia;
        this.curp = curp;
        this.status = status;
        this.token = token;
        this.correoElectronico = correoElectronico;
        this.password = password;
        this.nombre = nombre;
        this.telefono = telefono;
        this.pellidoPaterno = pellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.idAutotanque = idAutotanque;
        this.imagen = imagen;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIne() {
        return ine;
    }

    public void setIne(String ine) {
        this.ine = ine;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getTelefono() {
        return telefono;
    }

    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }

    public String getPellidoPaterno() {
        return pellidoPaterno;
    }

    public void setPellidoPaterno(String pellidoPaterno) {
        this.pellidoPaterno = pellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public Integer getIdAutotanque() {
        return idAutotanque;
    }

    public void setIdAutotanque(Integer idAutotanque) {
        this.idAutotanque = idAutotanque;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
