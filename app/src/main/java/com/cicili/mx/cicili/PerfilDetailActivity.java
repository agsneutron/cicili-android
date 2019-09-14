package com.cicili.mx.cicili;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.cicili.mx.cicili.domain.Client;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PerfilDetailActivity extends AppCompatActivity {
    //widgets
    private TextView name,email,phone,date,sexo;
    private Button mButton;

    Application application = (Application) Client.getContext();
    Client client = (Client) application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //Find the textviews objects
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        phone = (TextView) findViewById(R.id.phone);
        date = (TextView) findViewById(R.id.date);
        sexo = (TextView) findViewById(R.id.sexo);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);

        byte[] decodedString = Base64.decode(client.getPhoto().substring(client.getPhoto().indexOf(",") + 1).getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        imageView.setImageBitmap(decodedByte);

        name.setText(String.format("%s %s %s", client.getName(), client.getLastname(), client.getLastsname()));
        email.setText(client.getEmail());
        phone.setText(client.getCellphone());
        date.setText(client.getDate());
        sexo.setText(client.getSexo());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(PerfilDetailActivity.this, PerfilData.class);
                intent.putExtra("active","PE");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                PerfilDetailActivity.this.finish();
                //getActivity().getFragmentManager().beginTransaction().remove().commit();

            }
        });

        mButton = findViewById(R.id.datosF);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Do something
                Intent intent = new Intent(PerfilDetailActivity.this, DeleteAccountActivity.class);
                startActivity(intent);
            }
        });


    }

}
