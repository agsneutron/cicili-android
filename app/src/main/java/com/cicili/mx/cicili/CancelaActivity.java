package com.cicili.mx.cicili;

import android.content.Intent;
import android.os.Bundle;

import com.cicili.mx.cicili.domain.Pedido;
import com.cicili.mx.cicili.domain.SeguimientoPedido;
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

    LinearLayout linearLayout,detalle;
    Button nuevo_pedido;
    TextView estatus_cancelado, message;
    TextView address,alias,liter, ammount;
    String json_order, cause;
    Pedido pedidoData;
    SeguimientoPedido seguimientoPedido;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancela);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linearLayout = findViewById(R.id.view_error);
        detalle = findViewById(R.id.detalle);
        nuevo_pedido = findViewById(R.id.nuevo_pedido);
        nuevo_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CancelaActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
            cause = bundle.getString("cause");
            estatus_cancelado.setText(data);
            message.setText(String.format("Motivo: %s", cause));

            if (bundle.getString("from").equals("solicitado")){
                pedidoData = gson.fromJson(json_order , Pedido.class);
                address.setText(pedidoData.getDomicilio().toString());
                alias.setText(String.valueOf(pedidoData.getFormaPago()));
                //date.setText();
                ammount.setText(String.valueOf(pedidoData.getMonto()));
                liter.setText(String.valueOf(pedidoData.getCantidad()));
            }
            else if (bundle.getString("from").equals("aceptado")){
                seguimientoPedido = gson.fromJson(json_order , SeguimientoPedido.class);
                address.setText(String.valueOf(seguimientoPedido.getNombreConcesionario()));
                alias.setText(String.valueOf(seguimientoPedido.getNombreConductor()));
                //date.setText();
                detalle.setVisibility(View.GONE);
            }
        }

    }

}
