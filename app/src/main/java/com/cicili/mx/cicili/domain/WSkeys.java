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

    //change psw
    //https://cicili.com.mx:8443/app/mv/cliente/password/cambiar
    public static final String URL_CHANGEPSW =  "app/mv/cliente/password/cambiar";


    //CLIENT STATUS
    public  static final String verifica_codigo = "VE";
    public  static final String datos_personales = "PE";
    public  static final String datos_pago = "PA";
    public  static final String datos_direccion = "PD";
    public static final String datos_rfc ="PR";
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

    public static final String defectivo = "EFECTIVO";
    public static final String dTDD = "TDD";
    public static final String dTDC = "TDC";

    public static final Integer visa = 1;
    public static final Integer mc = 2;
    public static final Integer amex = 3;

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
    public  static  final  String URL_RFCDATA ="app/mv/cliente/rfc/agregar";

    //RFC update
    //https://cicili.com.mx:8443/app/mv/cliente/rfc/actualizar
    public  static  final  String URL_RFCUPDATE ="app/mv/cliente/rfc/actualizar";

    //MainSearch
    //https://cicili.com.mx:8443/app/mv/cliente/autotanques/disponibles
    public static final String URL_MAINSEARCH = "app/mv/cliente/autotanques/disponibles";

    public static final Boolean log = true;
    public static int cplenght = 4;
    public static final float CAMERA_ZOOM = 15f;

    public static final String latitude = "latitud";
    public static final String longitude = "longitud";
    public static final String concesionario = "concesionario";
    public static final String conductor = "conductor";
    public static final String perfilconductor = "perfilConductor";
    public static final String autotanque = "autotanque";
    public static final String id = "id";
}
