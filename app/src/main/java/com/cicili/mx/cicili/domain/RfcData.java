package com.cicili.mx.cicili.domain;

public class RfcData {

    private String id;
    private String rfc;
    private String razonSocial;
    private UsoCfdi usoCfdi;
    /*private String calle;
    private String exterior;
    private String interior;
    private String cp;
    private Asentamiento asentamiento;*/

    public RfcData() {
    }

    public RfcData(String id, String rfc, String razonSocial, UsoCfdi usoCfdi) {
        this.id = id;
        this.rfc = rfc;
        this.razonSocial = razonSocial;
        this.usoCfdi = usoCfdi;
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

    public UsoCfdi getUsoCfdi() {
        return usoCfdi;
    }

    public void setUsoCfdi(UsoCfdi usoCfdi) {
        this.usoCfdi = usoCfdi;
    }
}
