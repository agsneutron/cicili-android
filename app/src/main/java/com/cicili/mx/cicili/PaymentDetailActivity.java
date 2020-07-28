package com.cicili.mx.cicili;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.PaymentData;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PaymentDetailActivity extends AppCompatActivity {

    Application application = (Application) Client.getContext();
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

        cvv.setText(String.valueOf(String.valueOf(client.getPaymentDataArrayList().get(pos).getCvv())));
        numero.setText(String.valueOf(client.getPaymentDataArrayList().get(pos).getNumero()));
        vencimiento.setText(client.getPaymentDataArrayList().get(pos).getVencimiento());
        banco.setText(client.getPaymentDataArrayList().get(pos).getBanco());
        tipotrj.setText(client.getPaymentDataArrayList().get(pos).getTipoTarjeta());
        estatus.setText(client.getPaymentDataArrayList().get(pos).getNombreStatus());
        //pais.setText(String.valueOf(client.getPaymentDataArrayList().get(pos).getPais()));




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent intent = new Intent(Client.getContext(), PerfilData.class);
                intent.putExtra("active",WSkeys.datos_pago);
                intent.putExtra("id",String.valueOf(pos));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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
                Log.e("URLnew",urlNewString);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                running = Math.max(running, 1); // First request move it to 1.
                Log.e("URLnewPS",url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("URLnewPF",url);
                String lastUrl = "";
                if(--running == 0) { // just "running--;" if you add a timer.
                    // TODO: finished... if you want to fire a method.
                    Log.e("running0",url);
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



}
