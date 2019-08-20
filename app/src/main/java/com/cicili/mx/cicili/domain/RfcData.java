package com.cicili.mx.cicili.domain;

public class RfcData {

    private Integer id;
    private String rfc;
    private Integer status;
    private String razonSocial;
    private UsoCfdi usoCfdi;
    /*private String calle;
    private String exterior;
    private String interior;
    private String cp;
    private Asentamiento asentamiento;*/

    public RfcData() {
    }

    public RfcData(Integer id, String rfc, Integer status, String razonSocial, UsoCfdi usoCfdi) {
        this.id = id;
        this.rfc = rfc;
        this.status = status;
        this.razonSocial = razonSocial;
        this.usoCfdi = usoCfdi;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
