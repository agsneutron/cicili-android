package com.cicili.mx.cicili;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.cicili.mx.cicili.domain.WSkeys;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequestPassword extends AppCompatActivity {



    TextView mUserView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/




        mUserView = (EditText) findViewById(R.id.et_user);
        mUserView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    ValidateGetTemporalPsw(mUserView.getText().toString());
                    return true;
                }
                return false;
            }
        });

        Button mValidateButton = (Button) findViewById(R.id.register_button);
        mValidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(RegisterClient.this,MainActivity.class);
                //startActivity(intent);
                ValidateGetTemporalPsw(mUserView.getText().toString());
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void  ValidateGetTemporalPsw(String sUser){
        mUserView.setError(null);
        View focusView = mUserView;

        if (sUser.isEmpty()){
            mUserView.setError("Usuario no válido");
            focusView.requestFocus();
        }
        else{
            GetTemporalPsw(sUser);
        }
    }


    public void DialogValidate(final String sUser){
        AlertDialog.Builder builder = new AlertDialog.Builder(RequestPassword.this);
        builder.setCancelable(true)
                .setMessage("")
                .setView(R.layout.validate_sms_layout) //<-- layout containing EditText
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //All of the fun happens inside the CustomListener now.
                        //I had to move it to enable data validation.
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Validar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //All of the fun happens inside the CustomListener now.
                        //I had to move it to enable data validation.

                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextInputEditText mcode = (TextInputEditText) alertDialog.findViewById(R.id.code);
        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new CustomListener(alertDialog,sUser,mcode));
    }

    class CustomListener implements View.OnClickListener {
        private final Dialog dialog;
        private final String user;
        private final TextInputEditText mcode;
        public CustomListener(Dialog dialog,String sUser,TextInputEditText mcode ) {
            this.dialog = dialog;
            this.user = sUser;
            this.mcode = mcode;
        }
        @Override
        public void onClick(View v) {
            // put your code here
            //TextInputEditText mcode = (TextInputEditText)v.findViewById(R.id.code);

            String mValue = mcode.getText().toString()+"";
            if(mValue.isEmpty()){
                mcode.setError("Campo requerido");
                //dialog.dismiss();
            }else{
                UserCodeTask(mValue,dialog,user);
            }
        }
    }
    /**
     * Represents an asynchronous registration task used to register
     * the user.
     */
    public void UserCodeTask(final String mCode, final DialogInterface dialog, final String sUser) {

        String url = WSkeys.URL_BASE + WSkeys.URL_VALIDATETMPPASSWORD;
        RequestQueue queue = Volley.newRequestQueue(RequestPassword.this);
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserCode(response, dialog, mCode, sUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(mUserView, R.string.errorlistener, Snackbar.LENGTH_LONG)
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
                params.put(WSkeys.user, sUser);
                params.put(WSkeys.tmppsw, mCode);
                Log.e("PARAMETROS", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type", "application/json; charset=utf-8");
                //params.put("Authorization", token);
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);

    }

    public void ParserCode(String response, DialogInterface dialog, String code, String user) throws JSONException {

        Log.e("CodeResponse", response);
        JSONObject respuesta = new JSONObject(response);

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)){
            String data = respuesta.getString(WSkeys.data);

            if (data.equals("true")){
                Snackbar.make(mUserView, R.string.successvalidation, Snackbar.LENGTH_LONG)
                        .show();
                Intent intent = new Intent(RequestPassword.this,ForgotPswActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);

            }
            else{
                dialog.dismiss();
                Snackbar.make(mUserView, R.string.errorvalidation, Snackbar.LENGTH_LONG)
                        .show();
            }
        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(mUserView, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                    .show();
        }
    }


    /**
     * Represents an asynchronous registration task used to register
     * the user.
     */
    public void GetTemporalPsw(final String sUser) {



        String url = WSkeys.URL_BASE + WSkeys.URL_REQUESTPASSWORD;
        RequestQueue queue = Volley.newRequestQueue(RequestPassword.this);
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    ParserUser(response, sUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(mUserView, R.string.errorlistener, Snackbar.LENGTH_LONG)
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
                params.put(WSkeys.user, sUser);
                Log.e("PARAMETROS", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type", "application/json; charset=utf-8");
                //params.put("Authorization", token);
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);

    }

    public void ParserUser(String response, String user) throws JSONException {

        Log.e("CodeResponse", response);
        JSONObject respuesta = new JSONObject(response);

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)){
            DialogValidate(user);
        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(mUserView, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                    .show();
        }
    }
}
