package com.cicili.mx.cicili;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PerfilData extends AppCompatActivity implements PersonalDataFragment.OnFragmentInteractionListener,
        PaymentDataFragment.OnFragmentInteractionListener, AddressDataFragment.OnFragmentInteractionListener,
        RfcDataFragment.OnFragmentInteractionListener, ScheduleDataFragment.OnFragmentInteractionListener{
    private TextView mTextMessage;

    Application application = (Application) Client.getContext();
    Client client = (Client) application;

    final Fragment fragmentPersonal = new PersonalDataFragment();
    final Fragment fragmentPayment = new PaymentDataFragment();
    final Fragment fragmentAddress = new AddressDataFragment();
    final Fragment fragmentRfc = new RfcDataFragment();
    final Fragment fragmentDataSchedule = new ScheduleDataFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragmentPersonal;
    BottomNavigationView nv;


    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    /*private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_perfil:
                    fm.beginTransaction().hide(active).show(fragmentPersonal).commit();
                    active = fragmentPersonal;
                    return true;
                case R.id.navigation_payment:
                    fm.beginTransaction().hide(active).show(fragmentPayment).commit();
                    active = fragmentPayment;
                    return true;
                case R.id.navigation_address:
                    fm.beginTransaction().hide(active).show(fragmentAddress).commit();
                    active = fragmentAddress;
                    return true;
            }
            return false;
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_data);
        //BottomNavigationView navView = findViewById(R.id.nav_view);
        //navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        String dataactive="";
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String id="";



        if (bundle != null) {
            //si trae fragment por activar
            dataactive = bundle.getString("active");
            id =  bundle.getString("id");

            Utilities.SetLog("LOG ID",id,WSkeys.log);
        }else{
            CheckStatus();
        }

        //fm.beginTransaction().add(R.id.container, fragmentAddress, "3").hide(fragmentAddress).commit();
        //fm.beginTransaction().add(R.id.container, fragmentPayment, "2").hide(fragmentPayment).commit();
        //fm.beginTransaction().add(R.id.container, fragmentPersonal, "1").hide(fragmentPersonal).commit();
        //fm.beginTransaction().hide(active).show(fragmentPersonal).commit();


        if (dataactive.equals(WSkeys.datos_personales) || active.equals("")) {
            //navView.setSelectedItemId(R.id.navigation_perfil);
            fm.beginTransaction().hide(active);
            fm.beginTransaction().add(R.id.container, fragmentPersonal, "1").show(fragmentPersonal).commit();
            active = fragmentPersonal;
        }
        else if (dataactive.equals(WSkeys.datos_pago)){
            //navView.setSelectedItemId(R.id.navigation_payment);
            if(id != null) {
                bundle = new Bundle();
                bundle.putString("ARG_PARAM1", id);
                bundle.putString("ARG_PARAM2", " ");
                Utilities.SetLog("LOG ID in not null",id,WSkeys.log);
                fragmentPayment.setArguments(bundle);
            }
            fm.beginTransaction().hide(active);
            fm.beginTransaction().add(R.id.container, fragmentPayment, "2").show(fragmentPayment).commit();
            active = fragmentPayment;
        }
        else if (dataactive.equals(WSkeys.datos_direccion)){
            //navView.setSelectedItemId(R.id.navigation_address);
            if(id != null) {
                bundle = new Bundle();
                bundle.putString("ARG_PARAM1", id);
                bundle.putString("ARG_PARAM2", " ");
                Utilities.SetLog("LOG ID in not null",id,WSkeys.log);
                fragmentAddress.setArguments(bundle);
            }
            fm.beginTransaction().hide(active);
            fm.beginTransaction().add(R.id.container, fragmentAddress, "3").show(fragmentAddress).commit();
            active = fragmentAddress;
        }
        else if (dataactive.equals(WSkeys.datos_rfc)) {
            //navView.setSelectedItemId(R.id.navigation_address);
            if (id != null) {
                bundle = new Bundle();
                bundle.putString("ARG_PARAM1", id);
                bundle.putString("ARG_PARAM2", " ");
                Utilities.SetLog("LOG ID in not null", id, WSkeys.log);
                fragmentRfc.setArguments(bundle);
            }
            fm.beginTransaction().hide(active);
            fm.beginTransaction().add(R.id.container, fragmentRfc, "4").show(fragmentRfc).commit();
            active = fragmentRfc;
        }
        else if (dataactive.equals(WSkeys.datos_programar)) {
            //navView.setSelectedItemId(R.id.navigation_address);
            if (id != null) {
                bundle = new Bundle();
                bundle.putString("ARG_PARAM1", id);
                bundle.putString("ARG_PARAM2", " ");
                Utilities.SetLog("LOG ID in not null", id, WSkeys.log);
                fragmentDataSchedule.setArguments(bundle);
            }
            fm.beginTransaction().hide(active);
            fm.beginTransaction().add(R.id.container, fragmentDataSchedule, "5").show(fragmentDataSchedule).commit();
            active = fragmentDataSchedule;
        }

    }

    public void CheckStatus() {

        String url = WSkeys.URL_BASE + WSkeys.URL_VALIDATESTATUS;
        RequestQueue queue = Volley.newRequestQueue(PerfilData.this);
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserCode(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("El error", error.toString());
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
                Log.e("PARAMETROS", params.toString());
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

    public void ParserCode(String response) throws JSONException {
        Log.e("CodeResponse", response);
        JSONObject respuesta = new JSONObject(response);

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)){
            JSONObject jo_data = respuesta.getJSONObject(WSkeys.data);
            String data = jo_data.getString("idCliente");
            String dataactive = jo_data.getString("status");
            if (!data.equals("")){

                if (dataactive.equals(WSkeys.datos_personales) || active.equals("")) {
                    //navView.setSelectedItemId(R.id.navigation_perfil);
                    fm.beginTransaction().hide(active);
                    fm.beginTransaction().add(R.id.container, fragmentPersonal, "1").show(fragmentPersonal).commit();
                    active = fragmentPersonal;
                }
                else if (dataactive.equals(WSkeys.datos_pago)){

                    fm.beginTransaction().hide(active);
                    fm.beginTransaction().add(R.id.container, fragmentPayment, "2").show(fragmentPayment).commit();
                    active = fragmentPayment;
                }
                else if (dataactive.equals(WSkeys.datos_direccion)){

                    fm.beginTransaction().hide(active);
                    fm.beginTransaction().add(R.id.container, fragmentAddress, "3").show(fragmentAddress).commit();
                    active = fragmentAddress;
                }
                else if (dataactive.equals(WSkeys.datos_rfc)) {

                    fm.beginTransaction().hide(active);
                    fm.beginTransaction().add(R.id.container, fragmentRfc, "4").show(fragmentRfc).commit();
                    active = fragmentRfc;
                }


            }
            else{
               // Snackbar.make(mCodeView, R.string.errorvalidation, Snackbar.LENGTH_LONG)
               //         .show();
            }
        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            //Snackbar.make(mCodeView, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
            //        .show();
        }
    }
}
