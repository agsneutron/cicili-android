package com.cicili.mx.cicili;

import android.os.Bundle;

import com.cicili.mx.cicili.domain.Client;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class LegalActivity extends AppCompatActivity implements Response.Listener<byte[]>, Response.ErrorListener{

    PDFView pdfView;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE= 2;

    InputStreamVolleyRequest request;
    int count;

    TextView textoProgreso;
    ProgressBar barra;

    Application application = (Application) Client.getContext();
    Client client = (Client) application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.SetLog("url Documentos ", "entro", WSkeys.log);
        setContentView(R.layout.activity_legal);
        Utilities.SetLog("url Documentos ", "paso", WSkeys.log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWRITE_EXTERNAL_STORAGE();
        textoProgreso =  (TextView) findViewById(R.id.progresotextBar);
        textoProgreso.setText("");
        barra =  (ProgressBar) findViewById(R.id.progresoBar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        descargaArchivos("pdf","application/pdf");
    }

    public void getWRITE_EXTERNAL_STORAGE(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    public void descargaArchivos(String tipo, final String contentType){

        barra.setProgress(0);
        textoProgreso.setText("");

        String url = WSkeys.URL_BASE + WSkeys.URL_GENERIC_CONTRACT +"?id=" + client.getIdcte();
        Utilities.SetLog("url Documentos", url.toString(), WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext(),new HurlStack());
        request = new InputStreamVolleyRequest(Request.Method.GET, url, LegalActivity.this, LegalActivity.this, null){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", contentType);
                params.put("Authorization", client.getToken());
                return params;
            }
        };




        request.setRetryPolicy(new DefaultRetryPolicy(9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //Log.d("KEY_ERROR", "No se puede descargar el archivo:: "+error.getMessage());
    }

    @Override
    public void onResponse(byte[] response) {
        int downloadedSize = 0;
        Utilities.SetLog("downloadedSize: ","entro",true);
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            if (response!=null) {

                String contentType =request.responseHeaders.get("Content-Type")
                        .toString();
                //Read file name from headers
                String content =request.responseHeaders.get("Content-Disposition")
                        .toString();
                StringTokenizer st = new StringTokenizer(content, "=");
                String[] arrTag = st.toArray();

                String filename = arrTag[1];
                filename = filename.replace(":", ".");
                //Log.d("DEBUG::RESUME FILE NAME", filename);

                try{
                    long lenghtOfFile = response.length;
                    // Environment.getDownloadCacheDirectory()
                    //covert reponse to input stream
                    InputStream input = new ByteArrayInputStream(response);
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                    File file = new File(path, filename);
                    map.put("resume_path", file.toString());
                    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        output.write(data, 0, count);
                        Utilities.SetLog("total ",String.valueOf(total) , WSkeys.log);
                        actualizaProgreso (total,lenghtOfFile);
                    }

                    Utilities.SetLog("contentType: ",contentType.toString(),true);
                    DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
                    downloadManager.addCompletedDownload(file.getName(), file.getName(), true,
                            contentType, file.getPath(), file.length(), true);

                    output.flush();

                    output.close();
                    input.close();


                    Uri uri = Uri.fromFile(file);
                    pdfView = (PDFView)findViewById(R.id.pdfView);
                    pdfView.fitToWidth();
                    pdfView.fromFile(file)
                            .defaultPage(0)
                            .load();

                }catch(IOException e){
                    e.printStackTrace();

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
            e.printStackTrace();
        }
    }
    public void  actualizaProgreso(long avance, long tamano){
        int avanceEntero = Math.round(((avance*100)/tamano));
        barra.setProgress(avanceEntero);
        textoProgreso.setText(String.valueOf(avanceEntero)+" %");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }
}
