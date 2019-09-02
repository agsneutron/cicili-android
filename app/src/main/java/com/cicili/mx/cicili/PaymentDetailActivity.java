package com.cicili.mx.cicili;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Adapter;
import android.widget.Spinner;
import android.widget.TextView;

import static com.cicili.mx.cicili.domain.Client.getContext;

public class PaymentDetailActivity extends AppCompatActivity {

    Application application = (Application) getContext();
    Client client = (Client) application;
    private Spinner spinner;
    private Adapter adapter;
    private TextView tipocta, tipotrj;
    private TextView numero,vencimiento, cvv,banco;
    private String LOG = "PaymentDetailActivity";
    private  Integer pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Find the textviews objects
        //titular = (TextView) findViewById(R.id.titular);
        tipocta = (TextView) findViewById(R.id.tipocta);
        tipotrj = (TextView) findViewById(R.id.tipotrj);
        cvv = (TextView) findViewById(R.id.cvv);
        numero = (TextView) findViewById(R.id.numt);
        vencimiento = (TextView) findViewById(R.id.vencimiento);
        banco = (TextView) findViewById(R.id.banco);

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
        //pais.setText(String.valueOf(client.getPaymentDataArrayList().get(pos).getPais()));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent intent = new Intent(getContext(), PerfilData.class);
                intent.putExtra("active",WSkeys.datos_pago);
                intent.putExtra("id",pos);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //getDialog().dismiss();
            }
        });
    }



}
