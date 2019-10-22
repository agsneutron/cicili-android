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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.cicili.mx.cicili.domain.Client.getContext;

public class RateService extends AppCompatActivity {

    RatingBar ratingBar;
    EditText etComments;
    MaterialButton bRate;
    ProgressDialog progressDialog;

    Application application = (Application) Client.getContext();
    Client client = (Client) application;
    String order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_service);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            //si trae fragment por activar
            order =  bundle.getString("order");
        }
        else{
            finish();
        }

        ratingBar = findViewById(R.id.ratingBar);
        etComments = findViewById(R.id.etComments);
        bRate = findViewById(R.id.bRate);
        bRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean cancel = false;
                View focusView = null;
                String error="";

                // Check for a valid l/p, if the user entered one.
                if (TextUtils.isEmpty(etComments.getText()) || String.valueOf(etComments.getText()).equals("")) {
                    etComments.setError(getString(R.string.error_comments));
                    error=getString(R.string.error_comments);
                    focusView = etComments;
                    cancel = true;
                }

                if (ratingBar.getNumStars() == 0){
                    error=getString(R.string.error_rating);
                    focusView=ratingBar;
                    cancel = true;
                }


                if (cancel) {
                    // There was an error
                    focusView.requestFocus();
                    Toast toast = Toast.makeText(getContext(),  error, Toast.LENGTH_LONG);
                    toast.show();
                    //Snackbar.make(view, error, Snackbar.LENGTH_SHORT).show();
                    Utilities.SetLog("in error rating", error, WSkeys.log);
                }
                else{
                    progressDialog = ProgressDialog.show(getContext(), "Espera un momento por favor", "Estamos enviando la calificación y comentarios.", true);
                    try {
                        RateOrderTask();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void RateOrderTask() throws JSONException {


        String url;

        url = WSkeys.URL_BASE + WSkeys.URL_CALIFICA_PEDIDO+WSkeys.pedido+"="+order+WSkeys.calificacion+"="+ratingBar.getNumStars()+WSkeys.comentario+"="+etComments.getText();


        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    ParserRating(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("El error -- ORDER", error.toString());
                progressDialog.dismiss();

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
                Log.e("PARAMETROS", params.toString());*/
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

    public void ParserRating(JSONObject respuesta) throws JSONException {

        progressDialog.dismiss();
        Utilities.SetLog("ParserOrderResponse",respuesta.toString(),WSkeys.log);
        Integer pedido_id;

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)){

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Add the buttons
            builder.setMessage(respuesta.getString("data"));
            builder.setPositiveButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    finish();
                    dialog.dismiss();
                }
            });

            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();

        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{

            Snackbar.make(etComments, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }

    }


}