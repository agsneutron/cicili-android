package com.cicili.mx.cicili;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
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

import com.cicili.mx.cicili.domain.AclaracionData;
import com.cicili.mx.cicili.domain.Categorias;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Aclaracion extends AppCompatActivity {

    Application application = (Application) Client.getContext();
    Client client = (Client) application;
    Integer motivo_seleccionado=0;
    Spinner categoria;
    ArrayList<String> categoriaArray = new ArrayList<String>();
    ArrayList<Categorias> categoriaAux = new ArrayList<Categorias>();
    TextInputEditText mensaje;
    MaterialButton guarda_pregunta;
    Integer order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aclaracion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mensaje = (TextInputEditText) findViewById(R.id.mensaje);
        categoria = (Spinner) findViewById(R.id.categoria);
        guarda_pregunta =(MaterialButton) findViewById(R.id.guardapregunta);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            //recuperar datos de pedido
            order = bundle.getInt("order");
            ValidaAclaracionPendiente(order);

        }else{
            //no pedido
            AlertDialog.Builder builder = new AlertDialog.Builder(Aclaracion.this);

            builder.setMessage("Selecciona un pedido")
                    .setTitle("Debes seleccionar un pedido para levantar una aclaración ");

            builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.dismiss();
                    finish();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            finish();
        }



        guarda_pregunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (motivo_seleccionado.equals(0)) {
                    Snackbar.make(view, "Selecciona una categoría", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    if (!mensaje.getText().toString().isEmpty()) {
                        try {
                            GuardaPregunta();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mensaje.setError("Escribe una pregunta.");
                    }
                }
            }
        });

        LlenaMotivos(categoria);
        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("onItemSelected",String.valueOf(i));
                if (i!=0) {
                    motivo_seleccionado = categoriaAux.get(i-1).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void LlenaMotivos(final Spinner motivos){


        String url = WSkeys.URL_BASE + WSkeys.URL_CATEGORIAS_PREGUNTAS;
        Utilities.SetLog("LLENA motivo CANCELA",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(Client.getContext());
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

                Log.e("El error", error.toString());
                Snackbar.make(mensaje, R.string.errorlistener, Snackbar.LENGTH_SHORT)
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

        queue.add(jsonObjectRequest);

    }

    public void ParserMotivos(String response, Spinner categoria_sp) throws JSONException {

        Utilities.SetLog("RESPONSE_AYUDA",response,WSkeys.log);
        //Log.e("CodeResponse", response);


        JSONObject respuesta = new JSONObject(response);
        Integer posselected =0;

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)) {
            //ontener nivel de data
            //Utilities.SetLog("RESPONSEASENTAMIENTOS",data,WSkeys.log);
            JSONArray ja_usocfdi = respuesta.getJSONArray(WSkeys.data);
            Utilities.SetLog("motivoARRAY",ja_usocfdi.toString(),WSkeys.log);
            categoriaArray.add("Selecciona una categoría");
            for(int i=0; i<ja_usocfdi.length(); i++){
                Categorias categorias = new Categorias();
                try {
                    JSONObject jsonObject = (JSONObject) ja_usocfdi.get(i);
                    categorias.setId(jsonObject.getInt("id"));
                    categorias.setText(jsonObject.getString("text"));
                    categoriaAux.add(categorias);
                    categoriaArray.add(jsonObject.getString("text"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            categoria_sp.setAdapter(new ArrayAdapter<String>(Client.getContext(),android.R.layout.simple_spinner_dropdown_item,categoriaArray));
            categoria_sp.setSelection(posselected);
        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(mensaje, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    public void GuardaPregunta() throws JSONException {

        Gson gson = new Gson();
        String json;
        JSONObject params;
        AclaracionData aclaracionData = new AclaracionData();
        Categorias categoria = new Categorias();
        categoria.setId(motivo_seleccionado);
        aclaracionData.setAclaracion(mensaje.getText().toString());
        aclaracionData.setIdPedido(order);
        aclaracionData.setTipoAclaracion(categoria);
        json = gson.toJson(aclaracionData);
        params = new JSONObject(json);


        String url = WSkeys.URL_BASE + WSkeys.URL_AGREGA_ACLARACION;
        Utilities.SetLog("GUARDA PREGUNTA",url,WSkeys.log);
        Utilities.SetLog("DATA PREGUNTA",json,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(Client.getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ParserGuardaPregunta(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("El error", error.toString());
                Snackbar.make(mensaje, R.string.errorlistener, Snackbar.LENGTH_SHORT)
                        .show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json";
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

    public void ParserGuardaPregunta(JSONObject response) throws JSONException {

        Utilities.SetLog("RESPONSE_GUARDA",response.toString(),WSkeys.log);
        //Log.e("CodeResponse", response);


        // si el response regresa ok, entonces si inicia la sesión
        if (response.getInt("codeError") == (WSkeys.okresponse)) {
            //ontener nivel de data
            Utilities.SetLog("RESPONSE_GUARDADA",response.getString(WSkeys.data),WSkeys.log);
            AlertDialog.Builder builder = new AlertDialog.Builder(Aclaracion.this);

            builder.setMessage("En breve responderemos a tu asunto.")
                    .setTitle("Aclaración enviada");

            builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.dismiss();
                    finish();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(mensaje, response.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    public void ValidaAclaracionPendiente(Integer order){
        String url = WSkeys.URL_BASE + WSkeys.URL_OBTENER_ACLARACION+order;

        Utilities.SetLog("VALIDA SI HAY ACLARACION",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(Client.getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserAclaracion(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(mensaje, R.string.errorlistener, Snackbar.LENGTH_SHORT)
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

        queue.add(jsonObjectRequest);

    }

    public void ParserAclaracion(String respuesta) throws JSONException {

        Utilities.SetLog("RESPONSE_ACLARACION",respuesta,WSkeys.log);
        //Log.e("CodeResponse", response);

        JSONObject response = new JSONObject(respuesta);
        // si el response regresa ok, entonces si inicia la sesión
        if (response.getInt("codeError") == (WSkeys.okresponse)) {
            //ontener nivel de data
            Utilities.SetLog("RESPONSE_ACLARACIONPENDIENTE",response.getString(WSkeys.data),WSkeys.log);
            JSONArray ja_data = new JSONArray(response.getString(WSkeys.data));
            Intent intent = new Intent(Aclaracion.this, MessageActivity.class);
            if (ja_data.length() > 0) {
                Utilities.SetLog("LOGIN ja_data", ja_data.toString(), WSkeys.log);
                for(int i=0; i<ja_data.length(); i++) {
                    JSONObject jo_data = (JSONObject) ja_data.get(i);
                    Utilities.SetLog("jo_data",jo_data.toString(),WSkeys.log);
                    intent.putExtra("id",jo_data.getString("id"));
                    intent.putExtra("order",jo_data.getString("idPedido"));
                    intent.putExtra("aclaracion",jo_data.getString("aclaracion"));
                    JSONObject tipo = new JSONObject(jo_data.getString("tipoAclaracion"));
                    intent.putExtra("categoria",tipo.getString("text"));
                    intent.putExtra("uso","1");
                }
                //si hay aclaraciones pendientes

                startActivity(intent);
                finish();
            }

        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(mensaje, response.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

}
