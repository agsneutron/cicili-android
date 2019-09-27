package com.cicili.mx.cicili.io;

import com.cicili.mx.cicili.domain.Message;

import java.util.Map;

public class OutputMessage extends Message {
    private Map hora;

    public OutputMessage() {
    }

    public OutputMessage(Map hora) {
        this.hora = hora;
    }

    public OutputMessage(String mensaje, String nombre, String fotoPerfil, String type_mensaje, Map hora) {
        super(mensaje, nombre, fotoPerfil, type_mensaje);
        this.hora = hora;
    }

    public OutputMessage(String mensaje, String urlFoto, String nombre, String fotoPerfil, String type_mensaje, Map hora) {
        super(mensaje, urlFoto, nombre, fotoPerfil, type_mensaje);
        this.hora = hora;
    }

    public Map getHora() {
        return hora;
    }

    public void setHora(Map hora) {
        this.hora = hora;
    }
}
