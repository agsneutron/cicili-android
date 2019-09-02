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
import android.widget.TextView;

import static com.cicili.mx.cicili.domain.Client.getContext;

public class RfcDetailActivity extends AppCompatActivity {

    //widgets
    TextView street,town,state,cp, rfc, razonsocial, usocfdi;
    private  String LOG = "RFC DETAIL ";

    Application application = (Application) getContext();
    Client client = (Client) application;
    private  Integer pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfc_detail_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Find the textviews objects
        //street = (TextView) findViewById(R.id.street);
        rfc = (TextView) findViewById(R.id.rfc);
        //cp = (TextView) findViewById(R.id.cp);
        //town = (TextView) findViewById(R.id.town);
        razonsocial = (TextView) findViewById(R.id.razonsocial);
        usocfdi = (TextView) findViewById(R.id.usocfdi);

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


        //Utilities.SetLog(LOG,client.getRfcDataArrayList().get(pos).getCalle(),WSkeys.log);
        //Utilities.SetLog(LOG,String.valueOf(client.getRfcDataArrayList().get(pos).getExterior()),WSkeys.log);
        //Utilities.SetLog(LOG,String.valueOf(client.getRfcDataArrayList().get(pos).getInterior()),WSkeys.log);
        Utilities.SetLog(LOG,client.getRfcDataArrayList().get(pos).getRfc(),WSkeys.log);
        Utilities.SetLog(LOG,client.getRfcDataArrayList().get(pos).getRazonSocial(),WSkeys.log);
        Utilities.SetLog(LOG,String.valueOf(client.getRfcDataArrayList().get(pos).getUsoCfdi().getId()),WSkeys.log);
        //Utilities.SetLog(LOG,String.valueOf(client.getRfcDataArrayList().get(pos).getAsentamiento().getCp()),WSkeys.log);
        //Utilities.SetLog(LOG,client.getRfcDataArrayList().get(pos).getAsentamiento().getNombre(),WSkeys.log);

        //street.setText(client.getRfcDataArrayList().get(pos).getCalle() + "," + String.valueOf(client.getRfcDataArrayList().get(pos).getExterior()) + " " + String.valueOf(client.getRfcDataArrayList().get(pos).getInterior()));
        rfc.setText(client.getRfcDataArrayList().get(pos).getRfc());
        razonsocial.setText(client.getRfcDataArrayList().get(pos).getRazonSocial());
        usocfdi.setText(String.valueOf(client.getRfcDataArrayList().get(pos).getUsoCfdi().getText()));
        //cp.setText(String.valueOf(client.getRfcDataArrayList().get(pos).getAsentamiento().getCp()));
        //town.setText(client.getRfcDataArrayList().get(pos).getAsentamiento().getNombre());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent intent = new Intent(getContext(), PerfilData.class);
                intent.putExtra("active",WSkeys.datos_rfc);
                intent.putExtra("id",pos);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //getDialog().dismiss();


                //getActivity().getFragmentManager().beginTransaction().remove().commit();

            }
        });


    }

}
