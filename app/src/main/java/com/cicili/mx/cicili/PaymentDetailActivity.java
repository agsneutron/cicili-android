package com.cicili.mx.cicili;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.PaymentData;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.cicili.mx.cicili.domain.Client.getContext;

public class PaymentDetailActivity extends AppCompatActivity {

    Application application = (Application) getContext();
    Client client = (Client) application;
    private Spinner spinner;
    private Adapter adapter;
    private TextView tipocta, tipotrj;
    private TextView numero,vencimiento, cvv,banco,estatus;
    private Button verifica;
    private String LOG = "PaymentDetailActivity";
    private  Integer pos;
    private WebView webView;
    String weburl = "";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Find the textviews objects
        //titular = (TextView) findViewById(R.id.titular);
        tipocta = findViewById(R.id.tipocta);
        tipotrj = findViewById(R.id.tipotrj);
        cvv = findViewById(R.id.cvv);
        numero = findViewById(R.id.numt);
        vencimiento = findViewById(R.id.vencimiento);
        banco = findViewById(R.id.banco);
        verifica = findViewById(R.id.btnVerifica);
        estatus = findViewById(R.id.estatus);
        webView = findViewById(R.id.webview);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String id="";



        if (bundle != null) {
            //si trae fragment por activar
            id =  bundle.getString("ARG_PARAM1");
            pos = Integer.parseInt(id);
            Utilities.SetLog("LOG ID",id,WSkeys.log);
        }
        else{
            finish();
        }
        Utilities.SetLog(LOG+ "pos",String.valueOf(pos),WSkeys.log);


        //Utilities.SetLog(LOG,client.getPaymentDataArrayList().get(pos).getNombreTitular(),WSkeys.log);
        Utilities.SetLog(LOG,String.valueOf(client.getPaymentDataArrayList().get(pos).getTipoTarjeta()),WSkeys.log);
        Utilities.SetLog(LOG,String.valueOf(client.getPaymentDataArrayList().get(pos).getBanco()),WSkeys.log);
        Utilities.SetLog(LOG,String.valueOf(client.getPaymentDataArrayList().get(pos).getTipoPago()),WSkeys.log);
        Utilities.SetLog(LOG,String.valueOf(client.getPaymentDataArrayList().get(pos).getCvv()),WSkeys.log);
        Utilities.SetLog(LOG,client.getPaymentDataArrayList().get(pos).getVencimiento(),WSkeys.log);
        Utilities.SetLog(LOG,String.valueOf(client.getPaymentDataArrayList().get(pos).getNumero()),WSkeys.log);
        //Utilities.SetLog(LOG,String.valueOf(client.getPaymentDataArrayList().get(pos).getPais()),WSkeys.log);

        //titular.setText(client.getPaymentDataArrayList().get(pos).getNombreTitular());
        if (client.getPaymentDataArrayList().get(pos).getTipoPago().equals(WSkeys.TDD)){
            tipocta.setText(R.string.tdd);
        }else if (client.getPaymentDataArrayList().get(pos).getTipoPago().equals(WSkeys.TDC)){
            tipocta.setText(R.string.TDC);
        }
        //tipocta.setText(String.valueOf(client.getPaymentDataArrayList().get(pos).getTipoPago()));
        //tipotrj.setText(String.valueOf(client.getPaymentDataArrayList().get(pos).getTipoTarjeta()));
        /*if (client.getPaymentDataArrayList().get(pos).getTipoTarjeta()!=null) {
            if (client.getPaymentDataArrayList().get(pos).getTipoTarjeta().equals(WSkeys.amex)) {
                tipotrj.setText(R.string.amx);
            } else if (client.getPaymentDataArrayList().get(pos).getTipoTarjeta().equals(WSkeys.mc)) {
                tipotrj.setText(R.string.mc);
            } else if (client.getPaymentDataArrayList().get(pos).getTipoTarjeta().equals(WSkeys.visa)) {
                tipotrj.setText(R.string.visa);
            }
        }*/

        cvv.setText(String.valueOf(String.valueOf(client.getPaymentDataArrayList().get(pos).getCvv()))+"");
        numero.setText(String.valueOf(client.getPaymentDataArrayList().get(pos).getNumero())+"");
        vencimiento.setText(client.getPaymentDataArrayList().get(pos).getVencimiento()+"");
        banco.setText(client.getPaymentDataArrayList().get(pos).getBanco()+"");
        tipotrj.setText(client.getPaymentDataArrayList().get(pos).getTipoTarjeta()+"");
        estatus.setText(client.getPaymentDataArrayList().get(pos).getNombreStatus()+"");
        //pais.setText(String.valueOf(client.getPaymentDataArrayList().get(pos).getPais()));




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (client.getPaymentDataArrayList().size() <= 1 ) {
            fab.setVisibility(View.GONE);
        }
            fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(PaymentDetailActivity.this,R.style.MyAlertDialogStyle)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Eliminar Cuenta")
                        .setMessage(" ¿ Está seguro que desea eliminar la cuenta bancaria ? ")
                        .setPositiveButton("     Si     ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deteteAccount();
                            }
                        })
                        .setNegativeButton("     No     ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //...
                            }
                        })
                        .show();
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                /* // toeditdata
                Intent intent = new Intent(Client.getContext(), PerfilData.class);
                intent.putExtra("active",WSkeys.datos_pago);
                intent.putExtra("id",String.valueOf(pos));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                //getDialog().dismiss();
            }
        });



        if (client.getPaymentDataArrayList().get(pos).getStatus() == 1){
            weburl= "https://api.cicili.com.mx:8443/banorte/3dSecureMetodoPago.jsp?id="+client.getPaymentDataArrayList().get(pos).getId()+"&cliente="+client.getIdcte()+"&modo=1";
            verifica.setEnabled(true);
            webView.setVisibility(View.VISIBLE);
        }else{

            weburl = "";
            verifica.setEnabled(false);
            Utilities.SetLog(LOG,weburl,WSkeys.log);
            webView.setVisibility(View.GONE);
        }



        verifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.SetLog(LOG,weburl,WSkeys.log);
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                //webView.loadUrl("https://www.google.com/");
                webView.loadUrl(weburl);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            private int running = 0; // Could be public if you want a timer to check.

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String urlNewString) {
                running++;
                webView.loadUrl(urlNewString);
               // Log.e("URLnew",urlNewString);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                running = Math.max(running, 1); // First request move it to 1.
               // Log.e("URLnewPS",url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
               // Log.e("URLnewPF",url);
                String lastUrl = "";
                if(--running == 0) { // just "running--;" if you add a timer.
                    // TODO: finished... if you want to fire a method.
                   // Log.e("running0",url);
                    lastUrl= webView.getUrl();
                    if (lastUrl.equals("https://api.cicili.com.mx:8443/banorte/ServletValidaTarjeta?status=2") || lastUrl.equals("https://api.cicili.com.mx:8443/banorte/ServletValidaTarjeta?status=1")){
                        if (lastUrl.substring(lastUrl.length() - 1).equals("2")) {
                            estatus.setText("VALIDA");
                            PaymentData paymentData = new PaymentData();
                            paymentData = client.getPaymentDataArrayList().get(pos);
                            paymentData.setNombreStatus("VALIDA");
                            paymentData.setStatus(2);
                            client.getPaymentDataArrayList().set(pos,paymentData);
                        }
                        webView.setVisibility(View.GONE);
                        verifica.setEnabled(false);
                    }
                }
            }
        });

        //Log.e("url",webView.getUrl());
    }


    public void deteteAccount(){

       if (client.getPaymentDataArrayList().size() > 1){


               /*if(pos != null) {
                   //toupdate
                   paymentData.setId(client.getPaymentDataArrayList().get(pos).getId());
                   json = gson.toJson(paymentData);
                   params = new JSONObject(json);
                   //Log.e("PaymentValuesUpdate--", json);
                   url = WSkeys.URL_BASE + WSkeys.URL_PAYMENTUPPDATE;
               }else{
                   //toadd
                   json = gson.toJson(paymentData);
                   params = new JSONObject(json);
                   //Log.e("PaymentValuePairs--", json);
                   url =  WSkeys.URL_BASE + WSkeys.URL_PAYMENTDATA;
               }*/

           String url =  WSkeys.URL_BASE + WSkeys.URL_ELIMINA_FORMAPAGO+client.getPaymentDataArrayList().get(pos).getId();
           //Log.e("URL", url);
               RequestQueue queue = Volley.newRequestQueue(getContext());
               JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
                   @Override
                   public void onResponse(JSONObject response) {
                       //Log.e("RESPONSE",response.toString());
                       //Log.e("paymentsize", String.valueOf(client.getPaymentDataArrayList().size()));
                        try {
                            if (response.getString("data").equals("La forma de pago se eliminó correctamente")) {
                                PaymentData paymentData = client.getPaymentDataArrayList().get(pos);
                                client.getPaymentDataArrayList().remove(paymentData);
                                //Log.e("NEW paymentsize", String.valueOf(client.getPaymentDataArrayList().size()));
                            }
                            Toast.makeText(PaymentDetailActivity.this, response.getString("data"),Toast.LENGTH_LONG).show();
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                       finish();
                   }
               }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {

                       //Log.e("El error", error.toString());
                       Snackbar.make(numero, R.string.errorlistener, Snackbar.LENGTH_LONG)
                               .show();
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

    }


}
