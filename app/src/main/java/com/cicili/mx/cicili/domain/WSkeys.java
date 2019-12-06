package com.cicili.mx.cicili.domain;

/**
 * Created by ariaocho on 25/06/19.
 */

public class WSkeys {

    //SERVER INFO
    //public static final String SERVERNAME = "https://cicili.com.mx";
    public static final String SERVERNAME = "http://34.66.139.244";
    public static final String PORTNUMBER = ":8080/";
    public static final String URL_BASE = SERVERNAME + PORTNUMBER ;

    public static final String DEVICE_NAME = "device_name";

    //ESTATUS TOKEN
    public static final String expired = "token_expired";
    public static final Integer okresponse = 0;
    public static final String error = "codeError";
    public static final String token = "token";
    public static final String messageError = "messageError";
    public static final String response = "response";
    public static final String httpStatus = "httpStatus";
    public static final String data = "data";



    // ACCESO
    // https://cicili.com.mx:8443/app/logincliente/
    public static final String URL_LOGIN = "app/mv/cliente/login";
    public static final String PUSERNAME = "user";
    public static final String PPASSWORD = "password";
    public static final String TOKENFIREBASE = "token_dispositivo";


    //REGISTRO
    //https://cicili.com.mx:8443/app/mv/cliente/registrar
    public static final String URL_REGISTER = "app/mv/cliente/registrar";
    public static final String PEMAIL = "correo_electronico";
    //public static final String PPASSWORD = "password";
    public static final String PCELLPHONE = "telefono";



    //VALIDA CODIGO
    //https://cicili.com.mx:8443/app/verifica/{codigo}
    public static final String URL_VALIDATECODE = "app/verifica/";
    public static final String PCODE = "code";
    public static final String nameValuePairs ="nameValuePairs";


    //CLIENTE DATA
    public static final String email = "correoElectronico";
    public static final String name="nombre";
    public static final String apepat="apellidoPaterno";
    public static final String apemat="apellidoMaterno";
    public static final String fecnac="nacimiento";
    public static final String cel="telefono";
    public static final String img="imagen";
    public static final String idcte="idCliente";
    public static final String status="status";
    public static final String formapago="formaPago";
    public static final String direcciones="direcciones";
    public static final String sexo ="sexo";
    public static final String idcta="id";
    //public static final String ="status";
    public static final String tpopag="tipoPago";
    public static final String nomtit="nombreTitular";
    public static final String num="numero";
    public static final String tpotrj="tipoTarjeta";
    public static final String tpocta="tipoCuenta";
    public static final String ven="vencimiento";
    public static final String cvv="cvv";
    public static final String pais="pais";



    //Request password
    //https://cicili.com.mx:8443/app/mv/cliente/password/solicitar
    public static final String URL_REQUESTPASSWORD = "app/mv/cliente/password/solicitar";
    public static final String user="user";

    //Validate tmppsw
    //https://cicili.com.mx:8443/app/mv/cliente/password/validar
    public static final String URL_VALIDATETMPPASSWORD = "app/mv/cliente/password/validar";
    public static final String tmppsw="tmp_password";

    //chat
    //app/mv/conductor/pedidoactivo
    public static final String URL_MENSAJES_CHAT = "app/chat/obtener/";

    //change psw
    //https://cicili.com.mx:8443/app/mv/cliente/password/cambiar
    public static final String URL_CHANGEPSW =  "app/mv/cliente/password/cambiar";


    //CLIENT STATUS
    public  static final String verifica_codigo = "VE";
    public  static final String datos_personales = "PE";
    public  static final String datos_pago = "PA";
    public  static final String datos_direccion = "PD";
    public static final String datos_rfc ="PR";
    public static final String datos_programar = "SO";
    public  static final String completo = "C";

    //Datos personales
    //https://cicili.com.mx:8443/app/mv/cliente/actualizar
    public static final String URL_PERSONALDATA = "app/mv/cliente/actualizar";
    public static final String fechanacimiento = "nacimiento";


    //Datos pago
    //https://cicili.com.mx:8443/app/mv/cliente/formapago/registrar
    /*Tipo de Pago:
        1 - Efectivo
        2 - Tarjeta de Débito
        3 - Tarjeta de Crédito

    Request: “application/json"
    Para tipo 1:
    {
        "tipoPago": int, (1 - Efectivo)
    }
    Para tipo 2,3;
    {
        "tipoPago": int,
        "nombreTitular": String,
            "numero": int,
        "tipoTarjeta": int, (1-Visa, 2-MasterCard, 3-Amex)
        "vencimiento": “aaaa-mm-dd“,
        "cvv": int,
        "fechaNacimiento": “aaaa-mm-dd“,
        "pais": int
    }*/

    public static final String URL_PAYMENTUPPDATE = "app/mv/cliente/formapago/actualizar";
    public static final String URL_PAYMENTDATA = "app/mv/cliente/formapago/registrar";
    public static final String fechaNacimiento = "fechaNacimiento";
    public static final Integer efectivo = 1;
    public static final Integer TDD = 2;
    public static final Integer TDC = 3;


    public static final String dtarjeta = "TARJETA";
    public static final String defectivo = "EFECTIVO";
    public static final String dTDD = "TDD";
    public static final String dTDC = "TDC";

    public static final Integer visa = 1;
    public static final Integer mc = 2;
    public static final Integer amex = 3;

    public static final String dvisa = "VISA";
    public static final String dmc = "MASTERCARD";
    public static final String damex = "AMERICAN EXPRESS";

    public static final String hombre = "Hombre";
    public static final String mujer = "Mujer";
    public static final String noindicar = "No Indicar";

    //Paises
    //app/catalogos/paises
    public static final String URL_COUNTRIES = "app/catalogos/paises";


    //Colinias
    //https://cicili.com.mx:8443/app/mv/cliente/asentamientos/{codigoPostal}
    public static final String URL_SEARCHBYCP = "app/mv/cliente/asentamientos/";


    //Address register
    public static final String URL_ADDRESSDATA = "app/mv/cliente/direccion/agregar";
    public static final String asentamientos = "asentamientos";


    //Address consult
    //https://cicili.com.mx:8443/app/mv/cliente/direcciones
    public static final String URL_ADDRESSCONSULTDATA = "app/mv/cliente/direcciones";

    //address update
    //https://cicili.com.mx:8443/app/mv/cliente/direccion/actualizar
    public static final String URL_ADDRESUPPDATE = "app/mv/cliente/direccion/actualizar";


    //RFC register
    //https://cicili.com.mx:8443/app/mv/cliente/rfc/agregar
    public  static  final  String URL_RFCDATA ="app/mv/cliente/rfc/registrar";

    //RFC update
    //https://cicili.com.mx:8443/app/mv/cliente/rfc/actualizar
    public  static  final  String URL_RFCUPDATE ="app/mv/cliente/rfc/actualizar";

    //RFC view
    public  static  final  String URL_RFCVIEW ="app/mv/cliente/rfc/obtener";

    //MainSearch
    //https://cicili.com.mx:8443/app/mv/cliente/autotanques/disponibles
    public static final String URL_MAINSEARCH = "app/mv/cliente/autotanques/disponibles";

    //Banco
    //https://api.cicili.com.mx:8443/app/mv/cliente/banco/
    public static final String URL_BANKSEARCH = "app/mv/cliente/banco/";

    //CFDI
    //https://api.cicili.com.mx:8443/app/mv/cliente/banco/
    public static final String URL_CFDI = "app/catalogos/usocfdi";

    //solicitar pedido
    //https://cicili.com.mx:8443/app/mv/cliente/pedido/solicitar
    public static final String URL_PEDIDO = "app/mv/cliente/pedido/solicitar";

    //cancela pedido
    //https://cicili.com.mx:8443/app/mv/cliente/pedido/cancelar
    public static final String URL_CANCELA = "app/mv/cliente/pedido/cancelar";
    public static final String pedido = "pedido";
    public static final String motivo = "motivo";

    //cinsultar pedidos
    //https://cicili.com.mx:8443/app/mv/cliente/pedido/obtener
    public static final String URL_CONSULTA_PEDIDO = "app/mv/cliente/pedido/obtener";

    //consulta id de pedido
    //https://cicili.com.mx:8443/app/mv/cliente/pedido/obtener/{idPedido}
    public static final String URL_CONSULTA_PEDIDO_ID ="app/mv/cliente/pedido/obtener/";

    //motivo cancela
    //https://cicili.com.mx:8443/app/catalogos/motivoscancelacion
    public static final String URL_MOTIVO_CANCELA = "app/catalogos/motivoscancelacion/1";

    //motivo programa
    //https://cicili.com.mx:8443/app/mv/cliente/pedido/programar
    public static final String URL_PROGRAMA = "app/mv/cliente/pedido/programar";

    //consulta programados
    //app/mv/cliente/pedido/programados
    public static final String URL_CONSULTA_PROGRAMADOS = "app/mv/cliente/pedido/programados";

    //facturar
    //https://cicili.com.mx:8443/app/mv/cliente/factura/solicitar/{idPedido}
    public static final String URL_FACTURA = "app/mv/cliente/factura/solicitar/";
    //tipo aclaracion
    //https://cicili.com.mx:8443/app/catalogos/tiposaclaracion
    public static final String URL_MOTIVO_ACLARACION = "app/catalogos/tiposaclaracion";


    //Eliminar cuenta
    //https://cicili.com.mx:8443/app/mv/cliente/eliminarcuenta
    public static final String URL_ELIMINA_CUENTA = "app/mv/cliente/eliminarcuenta";

    //Consultar Temas de Preguntas
    //https://cicili.com.mx:8443/app/catalogos/tiposaclaracion/1
    public static final String URL_CATEGORIAS_PREGUNTAS = "app/catalogos/tiposaclaracion/1";


    //Consultar Preguntas por Tema
    //https://cicili.com.mx:8443/app/mv/catalogos/preguntas/{idTema}
    public static final String URL_PREGUNTAS_POR_CATEGORIA = "app/catalogos/preguntas/";

    //ACLARACIONES
    //https://cicili.com.mx:8443/app/aclaracion/agregar
    public static final String URL_AGREGA_ACLARACION = "app/aclaracion/agregar";

    //OBTENER ACLARACIÓN
    //https://cicili.com.mx:8443/app/aclaracion/obtener
    public static final String URL_OBTENER_ACLARACION = "app/aclaracion/obtener/";

    //OBTENER SEGUIMIENTO
    //app/aclaracion/seguimiento/obtener/{idAclaracion}
    public static final String URL_OBTENER_SEGUIMIENTO_ACLARACION = "app/aclaracion/seguimiento/obtener/";

    //MENSAJE SEGUIMIENTO ACLARACION
    //https://cicili.com.mx:8443/app/aclaracion/seguimiento/agregar
    public static final String URL_DAR_SEGUIMIENTO_ACLARACION = "app/aclaracion/seguimiento/agregar";

    //Comunicación Cliente Conductor
    //https://cicili.com.mx:8443/app/chat/agregar
    public static final String URL_COMUNICACION_C_C = "app/chat/agregar";

    //OBTENER PREGUNTAS
    //app/aclaracion/preguntas
    public static final String URL_OBTENER_SEGUIMIENTO_PREGUNTAS = "app/aclaracion/preguntas";

    //VALIDACLIENTE
    // https://api.cicili.com.mx:8443/app/mv/cliente/status
    public static final String URL_VALIDATESTATUS = "app/mv/cliente/status";

    //PEDIDO ACTIVO
    //app/mv/cliente/pedidoactivo
    public static final String URL_PEDIDO_ACTIVO = "app/mv/cliente/pedidoactivo";

    //PEDIDO CALIFICA
    //app/mv/cliente/pedido/calificar
    public static final String URL_CALIFICA_PEDIDO = "app/mv/cliente/pedido/calificar?";

    public static final String calificacion="calificacion";
    public static final String comentario="comentario";


    public static final Boolean log = false; //true


    //Actualizar Pedido
    //https://cicili.com.mx:8443/app/mv/cliente/pedido/actualizar
    public static final String URL_UPDATE_ORDER = "app/mv/cliente/pedido/actualizar" ;
    public static int cplenght = 4;
    public static final float CAMERA_ZOOM = 15f;

    public static final String latitude = "latitud";
    public static final String longitude = "longitud";
    public static final String concesionario = "concesionario";
    public static final String conductor = "conductor";
    public static final String perfilconductor = "perfilConductor";
    public static final String autotanque = "autotanque";

    public static final String id = "id";

    //motivo programa
    //https://cicili.com.mx:8443/app/mv/cliente/pedido/programar
    public static final String URL_UBICACION_CONDUCTOR = "app/mv/cliente/pedido/ubicacion/";
    public static int no_error_ok = 1;
    public static String cantidad= "cantidad";
    public static String monto= "monto";
    public static String mensaje_facturar = "";
}
