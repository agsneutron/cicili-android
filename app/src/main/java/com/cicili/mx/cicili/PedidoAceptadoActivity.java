package com.cicili.mx.cicili;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.cicili.mx.cicili.directionhelpers.FetchURL;
import com.cicili.mx.cicili.directionhelpers.TaskLoadedCallback;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.MotivoCancela;
import com.cicili.mx.cicili.domain.SeguimientoPedido;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;


//Google API classes
//import com.google.api.GoogleAPI;
//import com.google.api.translate.Language;
//import com.google.api.translate.Translate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PedidoAceptadoActivity extends AppCompatActivity implements OnMapReadyCallback , TaskLoadedCallback, MessageReceiverCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private Double latOrderAddress, lonOrderAddress, monto_c, litro_c;
    ImageButton aclarar, facturar, cambiar;
    Button cancelar;
    LinearLayout bottom_sheet, linearLayout,f_row, bottom_sheet_cambiar;
    BottomSheetBehavior bsb, bsb_cambiar;
    String motivo_seleccionado="";
    String motivo_texto="";
    Button cancela_bsb;
    ArrayList<String> motivoArray = new ArrayList<String>();
    ArrayList<MotivoCancela> motivoAux = new ArrayList<MotivoCancela>();
    String order ="";
    String getStatus="";

    RequestQueue queue = Volley.newRequestQueue(Client.getContext());
    public static final String TAG = "TagQueue";



    protected static final String ESTATUS_ACTION = "statusaction";

    private  NotificationReceiver broadcast;

    /***** Ejecutar tarea cada 5 segundos < **/
    Handler handler = new Handler();
    private final int TIEMPO = 5000;
    Double iLat = 0.00, iLon = 0.00;
    private Marker mMarkerConductor = null;
    /***** > Ejecutar tarea cada 5 segundos **/

    private Polyline currentPolyline;

    MapFragment mapFragment;
    TextView vista;
    Application application = (Application) Client.getContext();
    Client client = (Client) application;

    /**
     * PEDIDO DATA
     **/
    String pedido_data = "";
    TextView monto;
    TextView lbl1, lbl2, lbl3, lbl4;
    TextView time, status_order, date, cantidad, name, item_price;
    Gson gson = new Gson();
    SeguimientoPedido seguimientoPedido = client.getSeguimientoPedido();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_aceptado);
        String nombreEstatus = "";
        String status = "2";
        JSONObject objJson = null;

        client.setMessageContext(this);
        client.setContextMap(null);

        vista = findViewById(R.id.name);
        monto = findViewById(R.id.cantidad);
        time = findViewById(R.id.time);
        lbl1 = findViewById(R.id.lbl1);
        lbl2 = findViewById(R.id.lbl2);
        lbl3 = findViewById(R.id.lbl3);
        lbl4 = findViewById(R.id.lbl4);
        name = findViewById(R.id.name);
        status_order = findViewById(R.id.formaPago);
        item_price = findViewById(R.id.item_price);
        date =  findViewById(R.id.date);
        cantidad =  findViewById(R.id.item_number);
        linearLayout = findViewById(R.id.linearL_pa);
        f_row = findViewById(R.id.f_row);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_ubicacion_detail);

        mapFragment.getMapAsync(this);

        latOrderAddress = 0.0;
        lonOrderAddress = 0.0;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        IntentFilter intentFilter = new IntentFilter(ESTATUS_ACTION);
        registerReceiver(broadcast,intentFilter);



        Utilities.SetLog("PEDIDOACP bundle", intent.getExtras().toString(), WSkeys.log);

        if (bundle != null) {
            //recuperar datos de pedido
            pedido_data = bundle.getString("pedido_data");
            //status = bundle.getString("status");
            Utilities.SetLog("PEDIDO ACEPTADO DATA", pedido_data, WSkeys.log);

            try {
                objJson = new JSONObject(pedido_data);
                status = objJson.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
            //seguimientoPedido= gson.fromJson(pedido_data , SeguimientoPedido.class);
            //seguimientoPedido= client.getSeguimientoPedido();
            order = seguimientoPedido.getId();
            monto.setText(seguimientoPedido.getMonto());
            cantidad.setText(seguimientoPedido.getCantidad());
            date.setText(seguimientoPedido.getFechaPedido());
            latOrderAddress = Double.parseDouble(seguimientoPedido.getLatitud());
            lonOrderAddress = Double.parseDouble(seguimientoPedido.getLongitud());
            time.setText(seguimientoPedido.getTiempo());
            lbl1.setText(String.format("Conductor:  %s", seguimientoPedido.getNombreConductor()));
            lbl2.setText(String.format("Color: %s", seguimientoPedido.getColor()));
            lbl3.setText("");
            lbl4.setText(String.format("Placa:  %s", seguimientoPedido.getPlaca()));
            item_price.setText(String.format("$ %s", seguimientoPedido.getPrecio()));

            status_order.setText(seguimientoPedido.getFormaPago());
            aclarar = findViewById(R.id.aclaracion);
            cancelar = findViewById(R.id.cancelar);
            facturar = findViewById(R.id.facturar);
            facturar.setEnabled(false);
            cambiar = findViewById(R.id.cambiar);

            Utilities.SetLog("NOTIFICATION estatus: ", client.getEstatusPedido(), WSkeys.log);
            if (seguimientoPedido.getTipo().toString().equals("3")) {
                switch (Integer.parseInt(client.getEstatusPedido())) {
                    case 1:
                        nombreEstatus = "Solicitado";

                        break;
                    case 2:
                        nombreEstatus = "Aceptado";
                        break;
                    case 3:
                        nombreEstatus = "En Camino";
                        break;
                    case 4:
                        nombreEstatus = "Preparando Carga";
                        cancelar.setEnabled(false);
                        cambiar.setEnabled(false);
                        break;
                    case 5:
                        nombreEstatus = "Cargando";
                        cancelar.setEnabled(false);
                        cambiar.setEnabled(false);
                        handler.removeCallbacksAndMessages(null);
                        break;
                    case 6:  //finaliza carga mostrar montos
                        nombreEstatus = "Cargado";
                        cambiar.setEnabled(false);
                        break;
                    case 7:
                        nombreEstatus = "Pagado";
                        cancelar.setEnabled(false);
                        facturar.setEnabled(true);
                        cambiar.setEnabled(false);
                        break;
                    case 8:
                        nombreEstatus = "Programado";
                        break;
                    case 9:
                        nombreEstatus = "Cancelado";
                        cambiar.setEnabled(false);
                        break;
                    default:
                        nombreEstatus = "Facturado";
                        cancelar.setEnabled(false);
                        facturar.setEnabled(true);
                        cambiar.setEnabled(false);
                        break;
                }
                name.setText("Pedido : " + nombreEstatus);

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // Add the buttons
                builder.setMessage("Pedido : " + nombreEstatus);
                builder.setPositiveButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button

                        dialog.dismiss();
                    }
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();

                //dialog.show();
            }

        //map
        getMyLocationPermision();

        facturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FacturaPedido(Integer.parseInt(seguimientoPedido.getId()));
            }
        });

        aclarar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AclararPedido(Integer.parseInt(seguimientoPedido.getId()));
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        cambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsb_cambiar.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        //to cancel
        //bottomsheet
        Spinner motivos = (Spinner) findViewById(R.id.motivos);
        LlenaMotivos(motivos);

        motivos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("onItemSelected",String.valueOf(i));

                motivo_seleccionado = String.valueOf(motivoAux.get(i).getId());
                motivo_texto = String.valueOf(motivoAux.get(i).getText());


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        bottom_sheet = (LinearLayout)findViewById(R.id.bottomSheetCancela);
        bsb = BottomSheetBehavior.from(bottom_sheet);
        bsb.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                String nuevoEstado = "";



                switch(newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        nuevoEstado = "STATE_COLLAPSED";
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nuevoEstado = "STATE_EXPANDED";
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        nuevoEstado = "STATE_HIDDEN";
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        nuevoEstado = "STATE_DRAGGING";
                        break;

                    case BottomSheetBehavior.STATE_SETTLING:
                        nuevoEstado = "STATE_SETTLING";
                        break;
                }

                Log.i("BottomSheets", "Nuevo estado: " + nuevoEstado);

                Button btnCancelaPedido = (Button) findViewById(R.id.cancela_pedido);
                btnCancelaPedido.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean cancel = false;
                        View focusView = null;
                        String error="";

                        // Check for a valid l/p, if the user entered one.
                        if (motivo_seleccionado.equals("")) {
                            // litros.setError(getString(R.string.error_invalid_value));
                            // focusView = litros;
                            error=getString(R.string.error_invalid_motivo);
                            cancel = true;
                        }


                        if (cancel) {
                            // There was an error
                            //focusView.requestFocus();
                            Toast toast = Toast.makeText(Client.getContext(),  error, Toast.LENGTH_LONG);
                            toast.show();
                            //Snackbar.make(view, error, Snackbar.LENGTH_SHORT).show();
                            Utilities.SetLog("in cancel pedido", error, WSkeys.log);
                        }
                        else{

                            //ejecuta  cancela pedido
                            try {
                                //CancelOrderTask(String.valueOf(motivo_seleccionado),String.valueOf(order));
                                Utilities.SetLog("in cancel motivo", motivo_seleccionado, WSkeys.log);
                                Utilities.SetLog("in cancel pedido", order, WSkeys.log);
                                if(!order.equals("")) {
                                    CancelOrderTask(motivo_seleccionado, order);
                                }
                                else{
                                    Toast toast = Toast.makeText(Client.getContext(),  "Espera a que se asigne tu pedido", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Utilities.SetLog("ejecuta cancela pedido", "", WSkeys.log);

                        }

                    }
                });

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheets", "Offset: " + slideOffset);
            }




            public void CancelOrderTask(final String motivo, final String order) throws JSONException {

                Map<String, String> params = new HashMap<String, String>();
                params.put(WSkeys.pedido, order);
                params.put(WSkeys.motivo, motivo);
                Log.e("PARAMETROSCANCEL_B", params.toString());


                String url = WSkeys.URL_BASE + WSkeys.URL_CANCELA+ "?"+WSkeys.pedido+"="+order+"&"+WSkeys.motivo+"="+motivo+"";
                Utilities.SetLog("CANCELA",url,WSkeys.log);

                //RequestQueue queue = Volley.newRequestQueue(getContext());
                //JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
                StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            //onStop();
                            ParserCancela(response);
                            queue.cancelAll(TAG);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Utilities.SetLog("ERROR RESPONSE",error.toString(),WSkeys.log);
                        Snackbar.make(linearLayout, R.string.errorlistener, Snackbar.LENGTH_LONG)
                                .show();
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put(WSkeys.pedido, order);
                        params.put(WSkeys.motivo, motivo);
                        return new JSONObject(params).toString().getBytes();
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(WSkeys.pedido, order);
                        params.put(WSkeys.motivo, motivo);
                        Log.e("PARAMETROSCANCEL", params.toString());
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        //params.put("Content-Type", "application/x-www-form-urlencoded");
                        //params.put("Content-Type", "application/json; charset=utf-8");
                        params.put("Authorization", client.getToken());

                        return params;
                    }
                };

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                jsonObjectRequest.setTag(TAG);
                queue.add(jsonObjectRequest);

            }

            public void ParserCancela(String response) throws JSONException {

                Utilities.SetLog("PARSER-CANCELA",response.toString(),WSkeys.log);
                JSONObject response_object = new JSONObject(response);

                // si el response regresa ok, entonces si inicia la sesión
                if (response_object.getInt("codeError") == (WSkeys.okresponse)) {
                    handler.removeCallbacksAndMessages(null);
                    Intent intent = new Intent(PedidoAceptadoActivity.this, CancelaActivity.class);
                    intent.putExtra("cancel_result",response_object.getString("data"));
                    intent.putExtra("cause",motivo_texto);
                    intent.putExtra("order",pedido_data);
                    intent.putExtra("from","aceptado");

                    startActivity(intent);

                    finish();
                }
                // si ocurre un error al registrar la solicitud se muestra mensaje de error
                else{
                    Snackbar.make(linearLayout, response_object.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });

        bottom_sheet_cambiar = (LinearLayout)findViewById(R.id.bottomSheetCambiar);
        bsb_cambiar = BottomSheetBehavior.from(bottom_sheet_cambiar);
        bsb_cambiar.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                String nuevoEstado = "";

                switch(newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        nuevoEstado = "STATE_COLLAPSED";
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nuevoEstado = "STATE_EXPANDED";
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        nuevoEstado = "STATE_HIDDEN";
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        nuevoEstado = "STATE_DRAGGING";
                        break;

                    case BottomSheetBehavior.STATE_SETTLING:
                        nuevoEstado = "STATE_SETTLING";
                        break;
                }

                Log.i("BottomSheetsCambiar", "Nuevo estado: " + nuevoEstado);
                final RadioGroup rgMontoLitro = (RadioGroup) findViewById(R.id.rgMontoLitro);
                rgMontoLitro.check(R.id.litro);
                String formapagoseleccionada="";
                final TextInputEditText input_monto_litros = (TextInputEditText) findViewById(R.id.input);
                final TextInputEditText calculo_monto_litro = (TextInputEditText) findViewById(R.id.calculo_input);
                final TextInputLayout calculoinput = (TextInputLayout) findViewById(R.id.calculoinput);
                final TextInputLayout labelinput = (TextInputLayout) findViewById(R.id.labelinput);

                rgMontoLitro.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        if(radioGroup.getCheckedRadioButtonId() == R.id.litro){
                            labelinput.setHint("Litros");
                            calculoinput.setHint("Monto");
                            input_monto_litros.setText(input_monto_litros.getText().toString());
                        }

                        if (radioGroup.getCheckedRadioButtonId() == R.id.monto){
                            labelinput.setHint("Monto");
                            calculoinput.setHint("Litros");
                            input_monto_litros.setText(input_monto_litros.getText().toString());
                        }
                    }
                });



                input_monto_litros.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        Double nuevoprecio;
                        Double calculaLitros;
                        if(!input_monto_litros.getText().toString().isEmpty()) {
                            if (rgMontoLitro.getCheckedRadioButtonId() == R.id.litro) {

                                if (Double.valueOf(input_monto_litros.getText().toString()) > 0) {
                                    //nuevoprecio = client.getAutotanquesCercanosArrayList().get(pipaSeleccionada).getPrecio() * Double.valueOf(input_monto_litros.getText().toString());
                                    nuevoprecio = Double.parseDouble(client.getSeguimientoPedido().getPrecio()) * Double.valueOf(input_monto_litros.getText().toString());
                                    calculo_monto_litro.setText(String.valueOf(nuevoprecio));
                                    monto_c = nuevoprecio;
                                    litro_c = Double.parseDouble(input_monto_litros.getText().toString());
                                }
                                else{
                                    calculo_monto_litro.setText(String.valueOf(0));
                                }
                            } else if (rgMontoLitro.getCheckedRadioButtonId() == R.id.monto) {
                                if (Double.valueOf(input_monto_litros.getText().toString()) > 0) {
                                    calculaLitros = (Double.valueOf(input_monto_litros.getText().toString()) /  Double.parseDouble(client.getSeguimientoPedido().getPrecio()));
                                    calculo_monto_litro.setText(String.valueOf(calculaLitros));
                                    monto_c = Double.parseDouble(input_monto_litros.getText().toString());
                                    litro_c = calculaLitros;
                                }
                                else{
                                    calculo_monto_litro.setText(String.valueOf(0));
                                }
                            }
                        }
                        else {
                            input_monto_litros.setText("0");
                        }
                    }
                });


                Button btnCambiaPedido = (Button) findViewById(R.id.cambia_pedido);
                btnCambiaPedido.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean cancel = false;
                        View focusView = null;
                        String error="";

                        // Check for a valid l/p, if the user entered one.
                        if (TextUtils.isEmpty(input_monto_litros.getText()) || String.valueOf(input_monto_litros.getText()).equals("0")) {
                            // litros.setError(getString(R.string.error_invalid_value));
                            // focusView = litros;
                            error=getString(R.string.error_invalid_value);
                            cancel = true;
                        }

                        // Check for a valid ammount.
                        if (monto_c !=null){
                            if (monto_c < 200.00) {
                                //Snackbar.make(view, R.string.error_invalid_ammount, Snackbar.LENGTH_SHORT).show();
                                error= getString(R.string.error_invalid_ammount);
                                cancel = true;
                            }
                        }

                        // Check for a valid password, if the user entered one.
                        if (TextUtils.isEmpty(calculo_monto_litro.getText()) || String.valueOf(calculo_monto_litro.getText()).equals("0")) {
                            Utilities.SetLog("ERROR PRECIO", String.valueOf(calculo_monto_litro.getText()), WSkeys.log);
                            //precio.setError(getString(R.string.error_invalid_value));
                            //focusView = precio;
                            error=getString(R.string.error_invalid_value);
                            cancel = true;
                        }


                        if (cancel) {
                            // There was an error
                            //focusView.requestFocus();
                            //Toast toast = Toast.makeText(getContext(),  error, Toast.LENGTH_LONG);
                            //toast.show();
                            Snackbar.make(view, error, Snackbar.LENGTH_LONG).show();

                            //Snackbar.make(view, error, Snackbar.LENGTH_SHORT).show();
                            Utilities.SetLog("in error update pedido", error, WSkeys.log);
                        }
                        else{

                            //ejecuta  update pedido
                            try {
                                //CancelOrderTask(String.valueOf(motivo_seleccionado),String.valueOf(order));
                                Utilities.SetLog("in up monto ", String.valueOf(monto_c), WSkeys.log);
                                Utilities.SetLog("in up litro", String.valueOf(litro_c), WSkeys.log);
                                if(!order.equals("")) {
                                    UpdateOrderTask(monto_c, litro_c);
                                }
                                else{
                                   // Toast toast = Toast.makeText(getContext(),  "Espera a que se asigne tu pedido", Toast.LENGTH_LONG);
                                   // toast.show();
                                    Snackbar.make(view, "por el momento, no es posible actualizar tu pedido.", Snackbar.LENGTH_LONG).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Utilities.SetLog("ejecuta update pedido", "", WSkeys.log);

                        }

                    }
                });

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheets", "Offset: " + slideOffset);
            }




            public void UpdateOrderTask(final Double monto, final Double litro) throws JSONException {

                JSONObject params = new JSONObject();
                params.put(WSkeys.id, Integer.parseInt(order));
                params.put(WSkeys.cantidad, litro);
                params.put(WSkeys.monto, monto);
                Log.e("PARAMETROSUPDATE", params.toString());


                String url = WSkeys.URL_BASE + WSkeys.URL_UPDATE_ORDER+"?"+WSkeys.pedido+"="+order+"&"+WSkeys.cantidad+"="+ litro+"&"+WSkeys.monto+"="+ monto;
                Utilities.SetLog("ACTUALIZA",url,WSkeys.log);

                //RequestQueue queue = Volley.newRequestQueue(getContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //onStop();
                            ParserUPDATE(response);
                            queue.cancelAll(TAG);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Utilities.SetLog("ERROR RESPONSE",error.toString(),WSkeys.log);
                        Snackbar.make(linearLayout, R.string.errorlistener, Snackbar.LENGTH_LONG)
                                .show();
                    }
                }) {
                    @Override
                    public String getBodyContentType()
                    {
                        return "application/json";
                    }

                    @Override
                    public byte[] getBody() {
                        HashMap<String, String> params = new HashMap<String, String>();
                        return new JSONObject(params).toString().getBytes();
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        Log.e("PARAMETROSCANCEL", params.toString());
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        //params.put("Content-Type", "application/x-www-form-urlencoded");
                        //params.put("Content-Type", "application/json; charset=utf-8");
                        params.put("Content-Type", "application/json; charset=utf-8");
                        params.put("Authorization", client.getToken());

                        return params;
                    }
                };

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                jsonObjectRequest.setTag(TAG);
                queue.add(jsonObjectRequest);

            }

            public void ParserUPDATE(JSONObject response_object) throws JSONException {

                Utilities.SetLog("PARSER-UPDATE",response_object.toString(),WSkeys.log);

                // si el response regresa ok, entonces si inicia la sesión
                if (response_object.getInt("codeError") == (WSkeys.okresponse)) {
                    JSONObject data = response_object.getJSONObject("data");
                    monto.setText(data.getString("monto"));
                    cantidad.setText(data.getString("cantidad"));
                    bsb_cambiar.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    Snackbar.make(linearLayout, "Tu pedido se actualizó correctamente", Snackbar.LENGTH_LONG)
                            .show();
                }
                // si ocurre un error al registrar la solicitud se muestra mensaje de error
                else{
                    Snackbar.make(linearLayout, response_object.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }


    //*********  Ejecutar tarea cada 5 mins **************
    public void ejecutarTarea() {
        handler.postDelayed(new Runnable() {
            public void run() {


                // función a ejecutar
                //actualizarChofer(); // función para refrescar la ubicación del conductor, creada en otra línea de código
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Client.getContext());
                try {

                    Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                //iLat=iLat+0.0001399;
                                //iLon=iLon+0.0001223;
                                Location getCurrentLocation = (Location) task.getResult();
                                Utilities.SetLog("Ubicacion: ", Double.toString(getCurrentLocation.getLatitude()) + " , " + Double.toString(getCurrentLocation.getLongitude()), WSkeys.log);

                                //ActualizarUbicacionTask(getCurrentLocation.getLatitude()+iLat,getCurrentLocation.getLongitude()-iLon);
                                ActualizarUbicacionTask(getCurrentLocation.getLatitude(), getCurrentLocation.getLongitude());

                            } else {
                                Utilities.SetLog("MAP-LOcATION", task.toString(), WSkeys.log);
                                Snackbar.make(vista, R.string.failedgetlocation, Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });


                } catch (SecurityException e) {
                    Utilities.SetLog("MAP", e.getMessage(), WSkeys.log);
                }


                handler.postDelayed(this, TIEMPO);
            }

        }, TIEMPO);

    }

    private void ActualizarUbicacionTask(final Double latitud, final Double longitud) {
        moveCameratoCurrentLocation(WSkeys.CAMERA_ZOOM, new LatLng(latitud, longitud));
        AddMarkerConductor(latitud, longitud, seguimientoPedido.getNombreConductor(), seguimientoPedido.getRazonSocial(), Double.parseDouble(seguimientoPedido.getPrecio()), seguimientoPedido.getTiempo(), Integer.parseInt(seguimientoPedido.getId()));
    }


    public void ObtenerUbicacionConductorTask() {
        handler.postDelayed(new Runnable() {
            public void run() {

                // función a ejecutar
                try {
                    ubicacionConductor(); // función para refrescar la ubicación del conductor, creada en otra línea de código
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                handler.postDelayed(this, TIEMPO);
            }

        }, TIEMPO);

    }


    public void ubicacionConductor() throws JSONException {


        String url = WSkeys.URL_BASE + WSkeys.URL_UBICACION_CONDUCTOR + seguimientoPedido.getId();

        //RequestQueue queue = Volley.newRequestQueue(client.getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //onStop();
                    UbicacionParserData(response);
                    queue.cancelAll(TAG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(vista, R.string.errorlistener, Snackbar.LENGTH_LONG)
                        .show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=utf-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put(WSkeys.PEMAIL, mCode);
                //Log.e("PARAMETROS", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Authorization", client.getToken());
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsonObjectRequest.setTag(TAG);
        queue.add(jsonObjectRequest);

    }

    public void UbicacionParserData(JSONObject response) throws JSONException {
        //showProgress(false);
        JSONObject respuesta = response;

        Utilities.SetLog("ubicacion ", respuesta.toString(), WSkeys.log);
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)) {
            JSONObject dataUbicacion = respuesta.getJSONObject(WSkeys.data);
            //JSONArray jousuario = respuesta.getJSONArray(WSkeys.data);


            //iLat=iLat+0.0001399;
            //iLon=iLon+0.0001223;
            //ActualizarUbicacionTask(dataUbicacion.getDouble("latitud")+iLat,dataUbicacion.getDouble("longitud")-iLon);
            ActualizarUbicacionTask(dataUbicacion.getDouble("latitud"), dataUbicacion.getDouble("longitud"));

            /*if (iLat == 0) {
                iLat = dataUbicacion.getDouble("latitud");
                iLon = dataUbicacion.getDouble("longitud");
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(iLat, iLon), new LatLng(latOrderAddress, lonOrderAddress))
                        .width(5)
                        .color(Color.RED));
            }*/
            iLat = dataUbicacion.getDouble("latitud");
            iLon = dataUbicacion.getDouble("longitud");

            String respuestaDirecctions = new FetchURL(PedidoAceptadoActivity.this).execute(getUrl(new LatLng(iLat, iLon), new LatLng(latOrderAddress, lonOrderAddress), "driving"), "driving").toString();

            /*JSONArray ja_direction = new JSONArray(respuestaDirecctions);

            Utilities.SetLog("DATA Directions: ", ja_direction.toString(), WSkeys.log);

            Utilities.SetLog("ubicación recibida: ", "ubicacion", WSkeys.log);*/

            //Snackbar.make(vista, "ubicación recibida", Snackbar.LENGTH_SHORT).show();


        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else {
            Utilities.SetLog("ERROR api ubicación: ", respuesta.getString(WSkeys.messageError), WSkeys.log);

            Snackbar.make(vista, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                    .show();
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private void getMyLocationPermision() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(Client.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(Client.getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMyMap();
            } else {
                ActivityCompat.requestPermissions(PedidoAceptadoActivity.this, permissions, REQUEST_LOCATION_PERMISSION);
            }
        } else {
            ActivityCompat.requestPermissions(PedidoAceptadoActivity.this, permissions, REQUEST_LOCATION_PERMISSION);
        }
    }

    private void initMyMap() {
        //MAP
        //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync((new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gmMap) {
                mMap = gmMap;

                if (mLocationPermissionGranted) {
                    getDeviceCurrentLocation();
                    if (ContextCompat.checkSelfPermission(Client.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(Client.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                    //mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setZoomGesturesEnabled(true);
                    //mMap.getUiSettings().isMapToolbarEnabled();
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                }

                //ejecutarTarea();
                ObtenerUbicacionConductorTask();

            }
        }));
    }

    private void getDeviceCurrentLocation() {


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Client.getContext());
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            Location getCurrentLocation = (Location) task.getResult();
                            latOrderAddress = getCurrentLocation.getLatitude();
                            lonOrderAddress = getCurrentLocation.getLongitude();
                            moveCameratoCurrentLocation(WSkeys.CAMERA_ZOOM, new LatLng(latOrderAddress, lonOrderAddress));
                            AddMarker(latOrderAddress, lonOrderAddress, seguimientoPedido.getNombreStatus(), seguimientoPedido.getDireccion());

                        } else {
                            Utilities.SetLog("MAP-LOCATION", task.toString(), WSkeys.log);

                        }
                    }
                });

            }
        } catch (SecurityException e) {
            Utilities.SetLog("MAP", e.getMessage(), WSkeys.log);
        }
    }

    private void moveCameratoCurrentLocation(float zoom, LatLng ltln) {
        Utilities.SetLog("MAP_CAMERA", ltln.latitude + " " + ltln.longitude, WSkeys.log);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltln, zoom));

    }

    public void AddMarker(Double lat, Double lon, String conductor, String concesionario) {
        mMap.addMarker(new MarkerOptions()
                .icon(Utilities.bitmapDescriptorFromVector(PedidoAceptadoActivity.this, R.drawable.ic_home_blue_24dp))
                .position(new LatLng(lat, lon))
                .title(concesionario)
                .snippet(conductor))
                .showInfoWindow();
    }

    public void AddMarkerConductor(Double lat, Double lon, String conductor, String concesionario, Double precio, String tiempo, Integer id) {
        if (mMarkerConductor != null) {
            mMarkerConductor.remove();
        }
        Utilities.SetLog("pipa : ", lat + " " + lon, WSkeys.log);
        mMarkerConductor = mMap.addMarker(new MarkerOptions()
                .icon(bitmapDescriptorFromVector(PedidoAceptadoActivity.this, R.drawable.ic_pipa_2_01))
                .position(new LatLng(lat, lon))
                .title("Concesionario: " + concesionario)
                .snippet("Conductor: " + conductor + "\n" + "Precio: $" + String.valueOf(precio) + "por litro \n" + "Tiempo de Llegada: " + tiempo));

        mMarkerConductor.showInfoWindow();
        mMarkerConductor.setTag(id);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {

        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    //translates the string from English to French
    /*public String translateString( String source ) {
        String translatedText = "";
        GoogleAPI.setHttpReferrer("http://code.google.com/p/google-api-translate-java/");
        //Translate.setHttpReferrer("http://code.google.com/p/google-api-translate-java/");
        try {
            translatedText = Translate.execute(source, Language.ENGLISH, Language.FRENCH);
        } catch (Exception e) {
            System.err.println( "Exception " + e.getMessage() );
        }
        return removeSpaces ( translatedText );
    }*/

    @Override
    public void onTaskDoneData(String data) {
        JSONObject respuesta = null;
        JSONObject rutasObj = null;
        JSONObject legsObj = null;
        JSONObject distanceObj = null;
        JSONObject durationObj = null;
        JSONArray rutas;
        JSONArray legs;
        JSONObject arrival_time = null;

        try {
            respuesta = new JSONObject(data);
            rutas = new JSONArray(respuesta.getString("routes"));
            rutasObj = new JSONObject(rutas.getString(0));
            legs = new JSONArray(rutasObj.getString("legs"));
            legsObj = new JSONObject(legs.getString(0));

            distanceObj = new JSONObject(legsObj.getString("distance"));
            durationObj = new JSONObject(legsObj.getString("duration"));


            //arrival_time = new JSONObject(legs.getJSONArray(5).toString());

            Utilities.SetLog("result onTaskDoneData distance object : ", legsObj.getString("distance"), WSkeys.log);
            Utilities.SetLog("result onTaskDoneData duration object: ", legsObj.getString("duration"), WSkeys.log);
            Utilities.SetLog("result onTaskDoneData distance: ", distanceObj.getString("text"), WSkeys.log);
            Utilities.SetLog("result onTaskDoneData duration: ", durationObj.getString("text"), WSkeys.log);
            time.setText(durationObj.getString("text"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void FacturaPedido(Integer pos) {


        String url = WSkeys.URL_BASE + WSkeys.URL_FACTURA + pos;
        Utilities.SetLog("PIDE FACTURA", url, WSkeys.log);
        //RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //onStop();
                    ParserFactura(response);
                    queue.cancelAll(TAG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(facturar, R.string.errorlistener, Snackbar.LENGTH_LONG)
                        .show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=utf-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put(WSkeys.PEMAIL, mCode);
                //Log.e("PARAMETROS", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Authorization", client.getToken());
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsonObjectRequest.setTag(TAG);
        queue.add(jsonObjectRequest);

    }

    public void ParserFactura(String response) throws JSONException {

        Utilities.SetLog("RESPONSE_Factura", response, WSkeys.log);
        //Log.e("CodeResponse", response);
        Gson gson = new Gson();
        JSONObject respuesta = new JSONObject(response);

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)) {
            //ontener nivel de data
            //Utilities.SetLog("RESPONSEASENTAMIENTOS",data,WSkeys.log);

            Snackbar.make(facturar, respuesta.getString(WSkeys.data), Snackbar.LENGTH_LONG)
                    .show();

        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else {
            Snackbar.make(facturar, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                    .show();

            Utilities.SetLog("ERRORPARSER", response, WSkeys.log);
        }
    }


    public void AclararPedido(Integer pos) {
        // no se aclara es comunicación con conductor
        Utilities.SetLog("uso 3 pedido", String.valueOf(pos), WSkeys.log);
        Intent intent = new Intent(PedidoAceptadoActivity.this, MessageChatActivity.class);
        intent.putExtra("idPedido", String.valueOf(pos));
        intent.putExtra("uso", "3");
        startActivity(intent);
    }


    @Override
    public void getReceiverEstatusPedido(final String status, final String mensaje) {
        String nombreEstatus="";

        Utilities.SetLog("getReceiverEstatusPedido: ", status, WSkeys.log);

        if (seguimientoPedido.getTipo().toString().equals("3")) {
            switch (Integer.parseInt(status)) {
                case 1:
                    nombreEstatus = "Solicitado";
                    break;
                case 2:
                    nombreEstatus = "Aceptado";
                    break;
                case 3:
                    nombreEstatus = "En Camino";
                    break;
                case 4:
                    nombreEstatus = "Preparando Carga";
                    break;
                case 5:
                    nombreEstatus = "Cargando";
                    //handler.removeCallbacksAndMessages(null);
                    break;
                case 6:
                    nombreEstatus = "Cargado";
                    break;
                case 7:
                    nombreEstatus = "Pagado";
                    //facturar.setEnabled(true);
                    break;
                case 8:
                    nombreEstatus = "Programado";
                    break;
                case 9:
                    nombreEstatus = "Cancelado";
                    break;
                default:
                    nombreEstatus = "Facturado";
                    //facturar.setEnabled(true);
                    break;
            }
            getStatus = nombreEstatus;


            final AlertDialog.Builder builder = new AlertDialog.Builder(client.getMessageContext());
            // Add the buttons

            switch (Integer.parseInt(status)) {
                case 6:
                    builder.setTitle("El cobro de tu pedido es:");
                    builder.setMessage("Cargo de Comisión de Servicio: $" + client.getComision() +"\n" +
                            "Monto  total a pagar al conductor: $" + client.getTotal());
                    break;
                case 9:
                    builder.setTitle("Pedido Cancelado");
                    builder.setMessage("Tu pedido ha sido cancelado por el conductor");
                    break;
            }

            builder.setPositiveButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.dismiss();
                    if (Integer.parseInt(status)==9){
                        Intent intent = new Intent(PedidoAceptadoActivity.this, MenuActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    switch (Integer.parseInt(status)) {
                        case 4:
                            cancelar.setEnabled(false);
                            break;
                        case 5:
                            cancelar.setEnabled(false);
                            handler.removeCallbacksAndMessages(null);
                            break;
                        case 6:
                            cancelar.setEnabled(false);
                            break;
                        case 7:
                            cancelar.setEnabled(false);
                            facturar.setEnabled(true);
                            cambiar.setEnabled(false);
                            break;
                        case 8:
                            cancelar.setEnabled(false);
                            facturar.setEnabled(true);
                            cambiar.setEnabled(false);
                            break;
                        case 9:
                            handler.removeCallbacksAndMessages(null);
                            client.setMessageContext(null);
                            finish();
                            break;

                        default:
                            cancelar.setEnabled(false);
                            facturar.setEnabled(true);
                            cambiar.setEnabled(false);
                            break;
                    }
                }
            });

            // Create the AlertDialog
            //AlertDialog dialog = builder.create();

            this.runOnUiThread(new Runnable() {
                public void run() {
                    AlertDialog dialog = builder.create();
                    //
                    name.setText("Pedido : " + getStatus);
                    switch (Integer.parseInt(status)) {
                        case 4:
                            name.setText("PREPARANDO CARGA ...");
                            cancelar.setEnabled(false);
                            break;
                        case 5:
                            name.setText("CARGANDO PEDIDO ...");
                            cambiar.setEnabled(false);
                            cancelar.setEnabled(false);
                            handler.removeCallbacksAndMessages(null);
                            break;
                        case 6:
                            name.setText("CARGA FINALIZADA");
                            cambiar.setEnabled(false);
                            cancelar.setEnabled(false);
                            dialog.show();
                            break;
                        case 7:
                            name.setText("PEDIDO COBRADO");
                            cancelar.setEnabled(false);
                            cambiar.setEnabled(false);
                            facturar.setEnabled(true);
                            break;
                        case 8:
                            name.setText("PEDIDO FINALIZADO, GRACIAS.");
                            cancelar.setEnabled(false);
                            facturar.setEnabled(true);
                            cambiar.setEnabled(false);
                            handler.removeCallbacksAndMessages(null);
                            client.setMessageContext(null);
                            Intent intent = new Intent(PedidoAceptadoActivity.this, RateService.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("order",client.getSeguimientoPedido().getId());
                            startActivity(intent);
                            finish();
                            break;
                        case 9:
                            name.setText("PEDIDO CANCELADO.");
                            dialog.show();

                            handler.removeCallbacksAndMessages(null);
                            client.setMessageContext(null);
                            //finish();
                            break;

                        default:
                            cancelar.setEnabled(false);
                            facturar.setEnabled(true);
                            cambiar.setEnabled(false);
                            break;
                    }


                    Thread.currentThread().interrupt();

                }
            });


            //dialog.show();
        }
    }



    //to cancel order
    public void LlenaMotivos(final Spinner motivos){


        String url = WSkeys.URL_BASE + WSkeys.URL_MOTIVO_CANCELA;
        Utilities.SetLog("LLENA motivo CANCELA",url,WSkeys.log);
        //RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //onStop();
                    ParserMotivos(response, motivos);
                    queue.cancelAll(TAG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(linearLayout, R.string.errorlistener, Snackbar.LENGTH_LONG)
                        .show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=utf-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put(WSkeys.PEMAIL, mCode);
                //Log.e("PARAMETROS", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Authorization", client.getToken());
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsonObjectRequest.setTag(TAG);
        queue.add(jsonObjectRequest);

    }

    public void ParserMotivos(String response, Spinner motivos) throws JSONException {

        Utilities.SetLog("RESPONSE_MOTIVOS",response,WSkeys.log);
        //Log.e("CodeResponse", response);


        JSONObject respuesta = new JSONObject(response);
        Integer posselected =0;

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)) {
            //ontener nivel de data
            //Utilities.SetLog("RESPONSEASENTAMIENTOS",data,WSkeys.log);
            JSONArray ja_usocfdi = respuesta.getJSONArray(WSkeys.data);
            Utilities.SetLog("MOTIVOSARRAY",ja_usocfdi.toString(),WSkeys.log);
            for(int i=0; i<ja_usocfdi.length(); i++){
                MotivoCancela motivoCancela = new MotivoCancela();
                try {

                    JSONObject jsonObject = (JSONObject) ja_usocfdi.get(i);
                    motivoCancela.setId(jsonObject.getInt("id"));
                    motivoCancela.setText(jsonObject.getString("text"));
                    motivoAux.add(motivoCancela);
                    motivoArray.add(jsonObject.getString("text"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            motivos.setAdapter(new ArrayAdapter<String>(Client.getContext(),android.R.layout.simple_spinner_dropdown_item,motivoArray));
            motivos.setSelection(posselected);
        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(linearLayout, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        Utilities.SetLog("Evento: "," onBackPressed",WSkeys.log);
        handler.removeCallbacksAndMessages(null); // se deja de enviar la ubicación
        client.setMessageContext(null);
        super.onBackPressed();
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

}