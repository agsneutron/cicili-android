package com.cicili.mx.cicili;

import android.content.Intent;
import android.os.Bundle;

import com.cicili.mx.cicili.domain.Pedido;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CancelaActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    Button nuevo_pedido;
    TextView estatus_cancelado, message;
    TextView address,alias,liter, ammount;
    String json_order, cause;
    Pedido pedidoData;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancela);
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

        linearLayout = findViewById(R.id.view_error);
        nuevo_pedido = findViewById(R.id.nuevo_pedido);
        nuevo_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        estatus_cancelado = findViewById(R.id.estatuscancelado);
        message = findViewById(R.id.message);
        address = findViewById(R.id.content);
        alias = findViewById(R.id.item_number);
        //date = findViewById(R.id.date);
        liter = findViewById(R.id.liter);
        ammount = findViewById(R.id.ammount);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String data="";



        if (bundle != null) {
            data = bundle.getString("cancel_result");
            json_order = bundle.getString("order");
            pedidoData = gson.fromJson(json_order , Pedido.class);
            cause = bundle.getString("cause");
            estatus_cancelado.setText(data);
            message.setText(String.format("Motivo: %s", cause));
            address.setText(pedidoData.getDomicilio().toString());
            alias.setText(String.valueOf(pedidoData.getFormaPago()));
            //date.setText();
            ammount.setText(String.valueOf(pedidoData.getMonto()));
            liter.setText(String.valueOf(pedidoData.getCantidad()));


        }




    }

}
