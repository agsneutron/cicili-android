package com.cicili.mx.cicili.domain;

public class RfcData {

    private String id;
    private String rfc;
    private String razonSocial;
    private String calle;
    private String exterior;
    private String interior;
    private String cp;
    private Asentamiento asentamiento;

    public RfcData() {
    }

    public RfcData(String id, String rfc, String razonSocial, String calle, String exterior, String interior, String cp, Asentamiento asentamiento) {
        this.id = id;
        this.rfc = rfc;
        this.razonSocial = razonSocial;
        this.calle = calle;
        this.exterior = exterior;
        this.interior = interior;
        this.cp = cp;
        this.asentamiento = asentamiento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getExterior() {
        return exterior;
    }

    public void setExterior(String exterior) {
        this.exterior = exterior;
    }

    public String getInterior() {
        return interior;
    }

    public void setInterior(String interior) {
        this.interior = interior;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public Asentamiento getAsentamiento() {
        return asentamiento;
    }

    public void setAsentamiento(Asentamiento asentamiento) {
        this.asentamiento = asentamiento;
    }
}
