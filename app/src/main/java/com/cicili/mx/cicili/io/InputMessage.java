package com.cicili.mx.cicili.io;

import com.cicili.mx.cicili.domain.Message;

public class InputMessage extends Message {

    private Long hora;

    public InputMessage() {
    }

    public InputMessage(Long hora) {
        this.hora = hora;
    }

    public InputMessage(String mensaje, String urlFoto, String nombre, String fotoPerfil, String type_mensaje, Long hora) {
        super(mensaje, urlFoto, nombre, fotoPerfil, type_mensaje);
        this.hora = hora;
    }

    public Long getHora() {
        return hora;
    }

    public void setHora(Long hora) {
        this.hora = hora;
    }
}