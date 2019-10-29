package com.cicili.mx.cicili;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.ComunicaCC;
import com.cicili.mx.cicili.domain.Message;
import com.cicili.mx.cicili.domain.SeguimientoData;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.InputMessage;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cicili.mx.cicili.domain.Client.getContext;

public class MessageActivity extends AppCompatActivity {

    Application application = (Application) Client.getContext();
    Client client = (Client) application;

    private CircleImageView fotoPerfil;
    private TextView nombre;
    private TextView nombreSub;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private ImageButton btnEnviar;
    private AdapterMessage adapter;
    private InputMessage messageData;
    private ImageButton btnEnviarFoto;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_PERFIL = 2;
    private String fotoPerfilCadena;
    private String id, order, URL_list, URL_seguimiento;
    String uso = "";
    //USO SEGUIMIENTO ACL


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        fotoPerfil = findViewById(R.id.fotoPerfil);
        nombre = findViewById(R.id.nombre);
        nombreSub = findViewById(R.id.nombreSub);
        rvMensajes = findViewById(R.id.rvMensajes);
        txtMensaje = findViewById(R.id.txtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviarFoto = (ImageButton) findViewById(R.id.btnEnviarFoto);
        fotoPerfilCadena = "";

        /*database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("chat");//Sala de chat (nombre)
        storage = FirebaseStorage.getInstance();*/
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();



        if (bundle != null) {
            uso = bundle.getString("uso");
            if (uso.equals("1")) {
                nombre.setText(String.format("%s ", client.getName()));
                nombreSub.setText(String.format(" Categoría: %s \n Aclaración: %s", bundle.getString("categoria"), bundle.getString("aclaracion")));

                id = bundle.getString("id");
                order = bundle.getString("idPedido");
                URL_list = WSkeys.URL_OBTENER_SEGUIMIENTO_ACLARACION + id;
                URL_seguimiento = WSkeys.URL_DAR_SEGUIMIENTO_ACLARACION;

            } else if (uso.equals("2")) {
                nombre.setText(String.format("%s ", client.getName()));
                nombreSub.setText(String.format(" Categoría: %s \n Pregunta: %s", bundle.getString("categoria"), bundle.getString("aclaracion")));

                id = "0";
                order = "0";
                URL_list = WSkeys.URL_OBTENER_SEGUIMIENTO_ACLARACION + id;
                URL_seguimiento = WSkeys.URL_DAR_SEGUIMIENTO_ACLARACION;

            } else if (uso.equals("3")) {
                nombre.setText(String.format("%s ", client.getName()));
                nombreSub.setText("");

                id = bundle.getString("idPedido");
                order = bundle.getString("idPedido");
                URL_list = WSkeys.URL_COMUNICACION_C_C;
                URL_seguimiento = WSkeys.URL_COMUNICACION_C_C;
                Utilities.SetLog("uso 3", id, WSkeys.log);
            }

            byte[] decodedString = Base64.decode(client.getPhoto().substring(client.getPhoto().indexOf(",") + 1).getBytes(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Utilities.SetLog("IMAGEN CLIENTE",client.getPhoto().substring(client.getPhoto().indexOf(",") + 1),WSkeys.log);

            fotoPerfil.setImageBitmap(decodedByte);


            LlenaLista(id, order);
        }

        adapter = new AdapterMessage(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //databaseReference.push().setValue(new OutputMessage(txtMensaje.getText().toString(),nombre.getText().toString(),fotoPerfilCadena,"1", ServerValue.TIMESTAMP));
                try {
                    SendMessage(txtMensaje.getText().toString(), nombre.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                txtMensaje.setText("");
            }
        });

        /*btnEnviarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_SEND);
            }
        });

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_PERFIL);
            }
        });*/

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        /*databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                InputMessage m = dataSnapshot.getValue(InputMessage.class);
                adapter.addMensaje(m);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
    }

    private void setScrollbar() {
        rvMensajes.scrollToPosition(adapter.getItemCount() - 1);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_SEND && resultCode == RESULT_OK){
            Uri u = data.getData();
            storageReference = storage.getReference("imagenes_chat");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri u = taskSnapshot.getDownloadUrl();
                    MensajeEnviar m = new MensajeEnviar("Kevin te ha enviado una foto",u.toString(),nombre.getText().toString(),fotoPerfilCadena,"2",ServerValue.TIMESTAMP);
                    databaseReference.push().setValue(m);
                }
            });
        }else if(requestCode == PHOTO_PERFIL && resultCode == RESULT_OK){
            Uri u = data.getData();
            storageReference = storage.getReference("foto_perfil");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri u = taskSnapshot.getDownloadUrl();
                    fotoPerfilCadena = u.toString();
                    MensajeEnviar m = new MensajeEnviar("Kevin ha actualizado su foto de perfil",u.toString(),nombre.getText().toString(),fotoPerfilCadena,"2",ServerValue.TIMESTAMP);
                    databaseReference.push().setValue(m);
                    Glide.with(MainActivity.this).load(u.toString()).into(fotoPerfil);
                }
            });
        }
    }*/

    public void SendMessage(String message, String name) throws JSONException {

        Gson gson = new Gson();
        String json;
        JSONObject params;
        if(uso.equals("3")){
            ComunicaCC messageData = new ComunicaCC();
            messageData.setMensaje(message);
            messageData.setIdPedido(Integer.parseInt(id));
            json = gson.toJson(messageData);
        }
        else {
            SeguimientoData seguimientoData = new SeguimientoData();
            seguimientoData.setTexto(message);
            seguimientoData.setAclaracion(Integer.parseInt(id));
            json = gson.toJson(seguimientoData);
        }
        params = new JSONObject(json);


        String url = WSkeys.URL_BASE + URL_seguimiento;
        Utilities.SetLog("GUARDA SEGUIMIENTO", url, WSkeys.log);
        Utilities.SetLog("DATA SEGUIMIENTO", json, WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ParserPushList(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("El error", error.toString());
                Snackbar.make(nombre, R.string.errorlistener, Snackbar.LENGTH_SHORT)
                        .show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put(WSkeys.PEMAIL, mCode);
                //Log.e("PARAMETROS", params.toString());
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

    public void ParserPushList(JSONObject response) throws JSONException {

        Utilities.SetLog("RESPONSE_GUARDA", response.toString(), WSkeys.log);
        //Log.e("CodeResponse", response);


        // si el response regresa ok, entonces si inicia la sesión
        if (response.getInt("codeError") == (WSkeys.okresponse)) {
            //ontener nivel de data
            Utilities.SetLog("RESPONSE_GUARDADA", response.getString(WSkeys.data), WSkeys.log);
            JSONArray ja_data = new JSONArray(response.getString(WSkeys.data));
            Gson gson = new Gson();
            if (ja_data.length() > 0) {
                Utilities.SetLog("LOGIN ja_data", ja_data.toString(), WSkeys.log);
                for (int i = 0; i < ja_data.length(); i++) {
                    JSONObject jo_message = (JSONObject) ja_data.get(i);
                    Utilities.SetLog("jo_msg", jo_message.toString(), WSkeys.log);
                    messageData = gson.fromJson(jo_message.toString(), InputMessage.class);
                   // adapter.clearMensajes();
                   // adapter.addMensaje(messageData);
                    LlenaLista(id, order);

                }

            }


        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else {
            Snackbar.make(nombre, response.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    public void LlenaLista(String id, String order) {
        String url = WSkeys.URL_BASE + URL_list;

        Utilities.SetLog("OBTIENE SEGUIMIENTO", url, WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserList(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(nombre, R.string.errorlistener, Snackbar.LENGTH_SHORT)
                        .show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=utf-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put(WSkeys.PEMAIL, mCode);
                //Log.e("PARAMETROS", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Authorization", client.getToken());
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);

    }

    public void ParserList(String respuesta) throws JSONException {

        Utilities.SetLog("RESPONSE_LIST", respuesta, WSkeys.log);
        JSONObject response = new JSONObject(respuesta);
        // si el response regresa ok, entonces si inicia la sesión
        if (response.getInt("codeError") == (WSkeys.okresponse)) {
            //obtener nivel de data
            Utilities.SetLog("RESPONSELISTELEMENTS", response.getString(WSkeys.data), WSkeys.log);

            JSONArray ja_data = new JSONArray(response.getString(WSkeys.data));
            Gson gson = new Gson();
            if (ja_data.length() > 0) {
                Utilities.SetLog("LOGIN ja_data", ja_data.toString(), WSkeys.log);
                adapter.clearMensajes();
                for (int i = 0; i < ja_data.length(); i++) {
                    JSONObject jo_message = (JSONObject) ja_data.get(i);
                    Utilities.SetLog("jo_msgLlenalist", jo_message.toString(), WSkeys.log);
                    messageData = gson.fromJson(jo_message.toString(), InputMessage.class);
                    adapter.addMensaje(messageData);
                }

            }
            // si ocurre un error al registrar la solicitud se muestra mensaje de error
            else {
                Snackbar.make(nombre, response.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}