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

    public InputMessage(Integer id, Integer aclaracion, String usuario, Integer idUsuario, String texto, String fecha){
        super(id, aclaracion, usuario, idUsuario, texto, fecha);

    }

    public InputMessage(String mensaje, Integer id, Integer idPedido, String usuario, Integer idUsuario, String fecha){
        super(mensaje, id, idPedido, usuario, idUsuario, fecha);

    }

    public Long getHora() {
        return hora;
    }

    public void setHora(Long hora) {
        this.hora = hora;
    }
}
