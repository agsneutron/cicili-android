package com.cicili.mx.cicili;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.MotivoCancela;
import com.cicili.mx.cicili.domain.Pedido;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.cicili.mx.cicili.domain.Client.getContext;

public class NewScheduledOrderActivity extends AppCompatActivity {



    String json_order="";
    ProgressDialog progressDialog;
    View view;
    Application application = (Application) Client.getContext();
    Client client = (Client) application;
    LinearLayout linearLayout;
    LinearLayout bottom_sheet;
    BottomSheetBehavior bsb;
    String motivo_seleccionado="";
    String motivo_texto="";
    Button cancela_bsb, nuevo_pedido;
    ArrayList<String> motivoArray = new ArrayList<String>();
    ArrayList<MotivoCancela> motivoAux = new ArrayList<MotivoCancela>();
    String LOG = "ORDEN";
    String order ="";
    TextView estatuspedido;

    Gson gson = new Gson();
    ArrayList<Pedido> pedidoAux = new ArrayList<Pedido>();
    Pedido pedidoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scheduled_order);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        linearLayout = findViewById(R.id.view_error);
        cancela_bsb = findViewById(R.id.cancela_pedido_bss);
        cancela_bsb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        estatuspedido = findViewById(R.id.estatuspedido);

        nuevo_pedido = findViewById(R.id.nuevo_pedido);
        nuevo_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewScheduledOrderActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String id="";



        if (bundle != null) {
            //recuperar datos de pedido
            json_order = bundle.getString("json_order");
            Utilities.SetLog("NEWORDER-JSON_ORDER",json_order, WSkeys.log);
            try {
                //progressDialog = ProgressDialog.show(getContext(), "Espera un momento por favor", "Estamos programando tu pedido.", true);

                //pedidoData = gson.fromJson(json_order , Pedido.class);
                //pedidoAux.add(pedidoData);
                //OrderDataTask(json_order);
                ParserOrder(json_order);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //bottomsheet
            Spinner motivos = (Spinner) findViewById(R.id.motivos);
            LlenaMotivos(motivos);

            motivos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    //Log.e("onItemSelected",String.valueOf(i));

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

                    //Log.i("BottomSheets", "Nuevo estado: " + nuevoEstado);

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
                                Toast toast = Toast.makeText(getContext(),  error, Toast.LENGTH_LONG);
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
                                        Toast toast = Toast.makeText(getContext(),  "Espera a que se asigne tu pedido", Toast.LENGTH_LONG);
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
                    //Log.i("BottomSheets", "Offset: " + slideOffset);
                }




                public void CancelOrderTask(final String motivo, final String order) throws JSONException {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put(WSkeys.pedido, order);
                    params.put(WSkeys.motivo, motivo);
                    //Log.e("PARAMETROSCANCEL_B", params.toString());


                    String url = WSkeys.URL_BASE + WSkeys.URL_CANCELA+ "?"+WSkeys.pedido+"="+order+"&"+WSkeys.motivo+"="+motivo+"";
                    Utilities.SetLog("CANCELA",url,WSkeys.log);

                    RequestQueue queue = Volley.newRequestQueue(getContext());
                    //JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
                    StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                ParserCancela(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Utilities.SetLog("ERROR RESPONSE",error.toString(),WSkeys.log);
                            Snackbar.make(linearLayout, R.string.errorlistener, Snackbar.LENGTH_SHORT)
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
                            //Log.e("PARAMETROSCANCEL", params.toString());
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

                    queue.add(jsonObjectRequest);

                }

                public void ParserCancela(String response) throws JSONException {

                    Utilities.SetLog("PARSER-CANCELA",response.toString(),WSkeys.log);
                    JSONObject response_object = new JSONObject(response);


                    // si el response regresa ok, entonces si inicia la sesión
                    if (response_object.getInt("codeError") == (WSkeys.okresponse)) {

                        Intent intent = new Intent(NewScheduledOrderActivity.this, CancelaActivity.class);
                        intent.putExtra("cancel_result",response_object.getString("data"));
                        intent.putExtra("cause",motivo_texto);
                        intent.putExtra("order",json_order);
                        intent.putExtra("from","programado");

                        startActivity(intent);

                        finish();
                    }
                    // si ocurre un error al registrar la solicitud se muestra mensaje de error
                    else{
                        Snackbar.make(linearLayout, response_object.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        }


    }


    public void OrderDataTask(String json_pedido) throws JSONException {


        String url;
        JSONObject params;


        params = new JSONObject(json_pedido);
        //Log.e("PedidoValuePairs--", json_pedido);
        url = WSkeys.URL_BASE + WSkeys.URL_PEDIDO;


        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               /* try {
                    progressDialog.dismiss();
                   // ParserOrder(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Log.e("El error", error.toString());
                Snackbar.make(linearLayout, R.string.errorlistener, Snackbar.LENGTH_SHORT)
                        .show();
               // progressDialog.dismiss();
            }
        }) {
            @Override
            public String getBodyContentType()
            {
                return "application/json";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                /*params.put(WSkeys.user, client.getUsername());
                params.put(WSkeys.name, vname);
                params.put(WSkeys.apepat, vpat);
                params.put(WSkeys.apemat, vmat);
                params.put(WSkeys.fechanacimiento, vnac);
                //Log.e("PARAMETROS", params.toString());*/
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Authorization", client.getToken());
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);

    }

    public void ParserOrder(String s_respuesta) throws JSONException {

        Utilities.SetLog("ParserOrderResponse",s_respuesta,WSkeys.log);
        Integer pedido_id;

        JSONObject respuesta = new JSONObject(s_respuesta);
        JSONObject jo_sheduled = respuesta.getJSONObject(WSkeys.nameValuePairs);


        // si el response regresa ok, entonces si inicia la sesión
        //if (respuesta.getInt("codeError") == (WSkeys.okresponse)){
            pedido_id = respuesta.getJSONObject(WSkeys.nameValuePairs).getInt("id");
            order = String.valueOf(pedido_id);
            estatuspedido.setText(String.format(" Estatus de Pedido: %s\nNúmero de Orden: %s", respuesta.getJSONObject(WSkeys.nameValuePairs).getString("nombreStatus"), String.valueOf(order)));
            json_order = jo_sheduled.toString();

        //} // si ocurre un error al registrar la solicitud se muestra mensaje de error
        //else{
        //    Snackbar.make(linearLayout, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
        //            .show();
        //}

    }

    public void LlenaMotivos(final Spinner motivos){


        String url = WSkeys.URL_BASE + WSkeys.URL_MOTIVO_CANCELA;
        Utilities.SetLog("LLENA motivo CANCELA",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserMotivos(response, motivos);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Log.e("El error", error.toString());
                Snackbar.make(linearLayout, R.string.errorlistener, Snackbar.LENGTH_SHORT)
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
                ////Log.e("PARAMETROS", params.toString());
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

        queue.add(jsonObjectRequest);

    }

    public void ParserMotivos(String response, Spinner motivos) throws JSONException {

        Utilities.SetLog("RESPONSE_MOTIVOS",response,WSkeys.log);
        ////Log.e("CodeResponse", response);


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
            motivos.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,motivoArray));
            motivos.setSelection(posselected);
        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(linearLayout, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }


}
