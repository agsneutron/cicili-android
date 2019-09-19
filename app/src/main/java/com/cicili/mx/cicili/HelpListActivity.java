package com.cicili.mx.cicili;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cicili.mx.cicili.domain.Categorias;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.PaymentData;
import com.cicili.mx.cicili.domain.Preguntas;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cicili.mx.cicili.domain.Client.getContext;

public class HelpListActivity extends AppCompatActivity {

    Application application = (Application) Client.getContext();
    Client client = (Client) application;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    ArrayList<Categorias> categoriasArrayList = new ArrayList<Categorias>();
    ArrayList<Preguntas> preguntasArrayList = new ArrayList<Preguntas>();
    HashMap<String, List<Preguntas>> expandableListDetail = new HashMap<String, List<Preguntas>>();
    ProgressDialog progressDialog;
    MaterialButton ask_to_cicili;
    Integer mis_preguntas_categoria=11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ask_to_cicili = findViewById(R.id.ask_to_cicili);
        ask_to_cicili.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HelpListActivity.this, AyudaActivity.class);
                intent.putExtra("id_categoria",mis_preguntas_categoria);
                startActivity(intent);
                finish();
            }
        });

        progressDialog = ProgressDialog.show(this, "Espera un momento por favor", "Estamos obteniendo la información de ayuda.", true);


        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);

        LlenaCategorias();
        //expandableListDetail = ExpandableListDataPump.getData();
        //expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        //expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        //expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
               /* Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();*/

            }
        });


        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                /*Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();*/

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                /*Toast.makeText(
                        getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();*/

                Utilities.SetLog("SELECTED ASK",expandableListDetail.get(
                        expandableListTitle.get(groupPosition)).get(
                        childPosition).getRespuesta(),WSkeys.log);

                AlertDialog.Builder builder = new AlertDialog.Builder(HelpListActivity.this);

                builder.setMessage(expandableListDetail.get(
                        expandableListTitle.get(groupPosition)).get(
                        childPosition).getRespuesta())
                        .setTitle(expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition).getPregunta());

                builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
    }

    public void LlenaCategorias(){

        String url = WSkeys.URL_BASE + WSkeys.URL_CATEGORIAS_PREGUNTAS;
        Utilities.SetLog("LLENA motivo CANCELA",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserCategories(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                /*Snackbar.make(linearLayout, R.string.errorlistener, Snackbar.LENGTH_SHORT)
                        .show();*/
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

    public void ParserCategories(String response) throws JSONException {
        Gson gson = new Gson();
        Utilities.SetLog("RESPONSE_CATEGORIAS",response,WSkeys.log);
        //Log.e("CodeResponse", response);


        JSONObject respuesta = new JSONObject(response);

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)) {

            //ontener nivel de data
            //Utilities.SetLog("RESPONSEASENTAMIENTOS",data,WSkeys.log);
            JSONArray ja_categorias = respuesta.getJSONArray(WSkeys.data);
            //Utilities.SetLog("CATEGORIASARRAY",ja_usocfdi.toString(),WSkeys.log);
            for(int i=0; i<ja_categorias.length(); i++) {
                JSONObject jo_categorias = (JSONObject) ja_categorias.get(i);
                Categorias categorias_aux = gson.fromJson(jo_categorias.toString(), Categorias.class);
                categoriasArrayList.add(categorias_aux);
                Utilities.SetLog("jo_categorias",categoriasArrayList.get(i).getText() + categoriasArrayList.get(i).getId(),WSkeys.log);

                if (categoriasArrayList.get(i).getText().equals("Mis preguntas")){
                    mis_preguntas_categoria = categoriasArrayList.get(i).getId();

                }
            }
            Preguntas_por_Categorias(categoriasArrayList);
        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            /*Snackbar.make(linearLayout, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();*/
            Log.e("El error", respuesta.getString(WSkeys.messageError));
        }
    }

    public void Preguntas_por_Categorias(ArrayList<Categorias> categorias_array){


        for(int i=0; i<categorias_array.size(); i++) {
            Utilities.SetLog("LLENA PREGUNTAS PARA",categorias_array.get(i).getText(),WSkeys.log);
            LlenaPreguntas(categorias_array.get(i));
        }
        progressDialog.dismiss();
    }


    public void LlenaPreguntas(final Categorias categorias_item){


        String url = WSkeys.URL_BASE + WSkeys.URL_PREGUNTAS_POR_CATEGORIA+categorias_item.getId();
        Utilities.SetLog("LLENA PREGUNTAS CATEGORIA",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Gson gson = new Gson();
                Utilities.SetLog("RESPONSE_PREGUNTAS",response,WSkeys.log);
                List<Preguntas> preguntas_list = new ArrayList<Preguntas>();
                try {
                    JSONObject respuesta = new JSONObject(response);
                    if (respuesta.getInt("codeError") == (WSkeys.okresponse)) {
                        //ontener nivel de data
                        JSONArray ja_preguntas = respuesta.getJSONArray(WSkeys.data);
                        //Utilities.SetLog("CATEGORIASARRAY",ja_usocfdi.toString(),WSkeys.log);
                        for(int i=0; i<ja_preguntas.length(); i++) {
                            JSONObject jo_preguntas = (JSONObject) ja_preguntas.get(i);
                            Preguntas preguntas_aux = gson.fromJson(jo_preguntas.toString(), Preguntas.class);
                            preguntas_list.add(preguntas_aux);
                            preguntasArrayList.add(preguntas_aux);
                            Utilities.SetLog("jo_preguntas",preguntasArrayList.get(i).getPregunta() + preguntasArrayList.get(i).getId(),WSkeys.log);
                        }
                        Utilities.SetLog("ELD",categorias_item.getText() + String.valueOf(preguntas_list.size()),WSkeys.log);

                        expandableListDetail.put(categorias_item.getText(), preguntas_list);
                        //expandableListDetail = ExpandableListDataPump.getData();
                        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                        expandableListAdapter = new CustomExpandableListAdapter(getContext(), expandableListTitle, expandableListDetail);
                        expandableListView.setAdapter(expandableListAdapter);

                    }
                    // si ocurre un error al registrar la solicitud se muestra mensaje de error
                    else{
                        Log.e("El error", respuesta.getString(WSkeys.messageError));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                /*Snackbar.make(linearLayout, R.string.errorlistener, Snackbar.LENGTH_SHORT)
                        .show();*/
                //progressDialog.dismiss();
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

}
