package com.cicili.mx.cicili;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LegalListActivity extends AppCompatActivity {

    String[] legalItemsArray = {"Contrato de Servicios", "Términos y Condiciones", "Aviso de Privacidad"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_list);
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

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.legal_item_list_view, legalItemsArray);

        ListView listView = (ListView) findViewById(R.id.legal_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                switch(i) {
                    case 0:
                        intent = new Intent(LegalListActivity.this, LegalActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(LegalListActivity.this, TermsActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(LegalListActivity.this, AdviceActivity.class);
                        startActivity(intent);
                        break;
                    default:

                        break;
                }
            }
        });
    }

}
