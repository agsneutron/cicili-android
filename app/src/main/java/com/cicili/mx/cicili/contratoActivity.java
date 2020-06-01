package com.cicili.mx.cicili;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.cicili.mx.cicili.domain.WSkeys;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

public class contratoActivity extends AppCompatActivity {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrato);

        Uri uri = Uri.parse(WSkeys.URL_BASE + "app/documentos/cliente");
        pdfView = findViewById(R.id.pdfView);
        pdfView.fitToWidth();
        pdfView.fromUri(uri)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {

                    }
                })
                .defaultPage(0)
                .load();
    }
}
