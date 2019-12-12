package com.cicili.mx.cicili;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.ComunicaCC;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.InputMessage;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageChatActivity extends AppCompatActivity implements MessageReceiverCallback{

    Application application = (Application) Client.getContext();
    Client client = (Client) application;

    private CircleImageView fotoPerfil;
    private TextView nombre;
    private TextView nombreSub;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private ImageButton btnEnviar;
    private AdapterMessageChat adapter;
    private InputMessage messageData;
    private ImageButton btnEnviarFoto;
    private ImageView imgBack;

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
        setContentView(R.layout.activity_message_chat);

        client.setContextChat(this);

        fotoPerfil = findViewById(R.id.fotoPerfil);
        nombre = findViewById(R.id.nombre);
        nombreSub = findViewById(R.id.nombreSub);
        rvMensajes = findViewById(R.id.rvMensajes);
        txtMensaje = findViewById(R.id.txtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        imgBack = findViewById(R.id.img_back);
        btnEnviarFoto = (ImageButton) findViewById(R.id.btnEnviarFoto);
        fotoPerfilCadena = "";

        /*database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("chat");//Sala de chat (nombre)
        storage = FirebaseStorage.getInstance();*/
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();



        if (bundle != null) {
            uso = bundle.getString("uso");

            nombre.setText(String.format("%s ", client.getName()));
            nombreSub.setText("");

            id = bundle.getString("idPedido");
            order = bundle.getString("idPedido");
            URL_list = WSkeys.URL_COMUNICACION_C_C;
            URL_seguimiento = WSkeys.URL_COMUNICACION_C_C;
            Utilities.SetLog("uso 3", id, WSkeys.log);

            byte[] decodedString = Base64.decode(client.getPhoto().substring(client.getPhoto().indexOf(",") + 1).getBytes(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Utilities.SetLog("IMAGEN CLIENTE",client.getPhoto().substring(client.getPhoto().indexOf(",") + 1),WSkeys.log);

            fotoPerfil.setImageBitmap(decodedByte);

            try {
                ObtenerMensajesChat();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        adapter = new AdapterMessageChat(this);
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

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

        ComunicaCC messageData = new ComunicaCC();
        messageData.setMensaje(message);
        messageData.setIdPedido(Integer.parseInt(id));
        json = gson.toJson(messageData);

        params = new JSONObject(json);


        String url = WSkeys.URL_BASE + URL_seguimiento;
        Utilities.SetLog("GUARDA SEGUIMIENTO", url, WSkeys.log);
        Utilities.SetLog("DATA SEGUIMIENTO", json, WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(Client.getContext());
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
                //Log.e("El error", error.toString());
                Snackbar.make(nombre, R.string.errorlistener, Snackbar.LENGTH_LONG)
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
                ////Log.e("PARAMETROS", params.toString());
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
        ////Log.e("CodeResponse", response);


        // si el response regresa ok, entonces si inicia la sesión
        if (response.getInt("codeError") == (WSkeys.okresponse)) {
            //ontener nivel de data
            Utilities.SetLog("RESPONSE_GUARDADA", response.getString(WSkeys.data), WSkeys.log);
            JSONArray ja_data = new JSONArray(response.getString(WSkeys.data));
            adapter.clearMensajes();
            Gson gson = new Gson();
            if (ja_data.length() > 0) {
                Utilities.SetLog("CHAT ja_data", ja_data.toString(), WSkeys.log);
                for (int i = 0; i < ja_data.length(); i++) {
                    JSONObject jo_message = (JSONObject) ja_data.get(i);
                    Utilities.SetLog("jo_msg", jo_message.toString(), WSkeys.log);
                    messageData = gson.fromJson(jo_message.toString(), InputMessage.class);
                    adapter.addMensaje(messageData);
                }
            }


        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else {
            Snackbar.make(nombre, response.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                    .show();
        }
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
            // si lista viene vacia
            else {
             //   Snackbar.make(nombre, response.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
             //           .show();
            }
        }
    }

    public void ParserListMsg(String respuesta) throws JSONException {


        if (!respuesta.equals("")) {
            //obtener nivel de data
            Utilities.SetLog("ParserListMsg", respuesta, WSkeys.log);

            JSONObject data = new JSONObject(respuesta);
            JSONArray ja_data = new JSONArray(data.getString("mensajes"));
            Gson gson = new Gson();
            if (ja_data.length() > 0) {
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
                Snackbar.make(nombre, "Error al generar la lista de mensajes", Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    }

    // obtener listado de mensajes
    public void ObtenerMensajesChat() throws JSONException {

        String url = WSkeys.URL_BASE + WSkeys.URL_MENSAJES_CHAT + id;
        RequestQueue queue = Volley.newRequestQueue(Client.getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ParserList(response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utilities.SetLog("ERROR RESPONSE",error.toString(),WSkeys.log);
                Snackbar.make(nombre, R.string.errorlistener, Snackbar.LENGTH_LONG)
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

    @Override
    public void onBackPressed() {
        client.setContextChat(null);
        super.onBackPressed();
    }

    @Override
    public void getReceiverEstatusPedido(String status, String mensaje) {
        try {
            ObtenerMensajesChat();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}