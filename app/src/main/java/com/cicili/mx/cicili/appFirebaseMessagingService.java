package com.cicili.mx.cicili;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cicili.mx.cicili.domain.AddressData;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.Pedido;
import com.cicili.mx.cicili.domain.PedidoActivo;
import com.cicili.mx.cicili.domain.SeguimientoPedido;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.cicili.mx.cicili.domain.ChannelsNotification.CHANNEL_1_ID;
import static com.cicili.mx.cicili.domain.Client.getContext;

public class appFirebaseMessagingService extends FirebaseMessagingService {


    Application application = (Application) getContext();
    Client client = (Client) application;
    SeguimientoPedido seguimientoPedido;

    Gson gson = new Gson();
    String sJSONObject;

    MessageReceiverCallback interfaceNotification;
    MessageReceiverCallback interfaceNotificationPipas;
    MessageReceiverCallback interfaceNotificationChat;
    MessageReceiverCallback interfaceNotificationNewOrder;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Utilities.SetLog("NOTIFICATION RemoteMessage", remoteMessage.getMessageId(), WSkeys.log);


        if (remoteMessage.getData() != null) {
            Utilities.SetLog("NOTIFICATION NOTIFICATION", remoteMessage.getData().toString(), WSkeys.log);

            if (interfaceNotification == null) {
                interfaceNotification = (MessageReceiverCallback) client.getMessageContext();
            }

            switch (Integer.parseInt(remoteMessage.getData().get("status"))) {
                case 2:

                    try {
                        ValidaPedidoActivo(remoteMessage.getData().get("title"),remoteMessage.getData().get("body") );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
                case 5:
                    interfaceNotification.getReceiverEstatusPedido(remoteMessage.getData().get("status"), remoteMessage.getData().get("body"));
                    break;
                case 6:
                    client.setComision(remoteMessage.getData().get("comision").toString());
                    client.setTotal(remoteMessage.getData().get("monto").toString());
                    interfaceNotification.getReceiverEstatusPedido(remoteMessage.getData().get("status"), remoteMessage.getData().get("body"));
                    break;
                case 7:
                    interfaceNotification.getReceiverEstatusPedido(remoteMessage.getData().get("status"), remoteMessage.getData().get("body"));
                    break;
                case 8:
                    interfaceNotification.getReceiverEstatusPedido(remoteMessage.getData().get("status"), remoteMessage.getData().get("body"));
                    break;
                case 9:

                    if (interfaceNotificationNewOrder == null && client.getContextNewOrder() != null) {

                        interfaceNotificationNewOrder = (MessageReceiverCallback) client.getContextNewOrder();
                        interfaceNotificationNewOrder.getReceiverEstatusPedido(remoteMessage.getData().get("status"), sJSONObject);
                        client.setContextNewOrder(null);
                    }

                    if (interfaceNotification != null && client.getMessageContext() != null) {
                        Utilities.SetLog("NOTIFICATION ESTATUS9CNTX", remoteMessage.getData().toString(), WSkeys.log);

                        interfaceNotification.getReceiverEstatusPedido(remoteMessage.getData().get("status"), remoteMessage.getData().get("body"));
                    }



                    break;
                case 10:
                    interfaceNotification.getReceiverEstatusPedido(remoteMessage.getData().get("status"), remoteMessage.getData().get("body"));
                    break;
                case 11:
                    if (interfaceNotificationPipas == null && client.getContextMap() != null) {
                        interfaceNotificationPipas = (MessageReceiverCallback) client.getContextMap();
                        interfaceNotificationPipas.getReceiverEstatusPedido("11", "Nuevas Pipas");
                    }

                    break;
                case 20:
                    GsonBuilder gsonChatBuilder = new GsonBuilder();
                    Gson gsonObjectChat = gsonChatBuilder.create();
                    sJSONObject = gsonObjectChat.toJson(remoteMessage.getData());
                    if (interfaceNotificationChat == null && client.getContextChat() != null) {
                        Utilities.SetLog("NOTIFICATION GETCONTEX?", remoteMessage.getData().toString(), WSkeys.log);

                        interfaceNotificationChat = (MessageReceiverCallback) client.getContextChat();
                        interfaceNotificationChat.getReceiverEstatusPedido(remoteMessage.getData().get("status"), sJSONObject);
                    }
                    else{
                        if(getApplicationContext()!=null) {
                            Utilities.SetLog("uso 3 pedido", remoteMessage.getData().get("idPedido"), WSkeys.log);
                            Intent intent = new Intent(getApplicationContext(), MessageChatActivity.class);
                            intent.putExtra("idPedido", String.valueOf(remoteMessage.getData().get("idPedido")));
                            intent.putExtra("uso", "3");
                            startActivity(intent);
                        }
                    }


                     break;

            }

        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            Utilities.SetLog("NOTIFICATION DATA", remoteMessage.getData().toString(), WSkeys.log);

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getData() != null) {
            Utilities.SetLog("Message Notification Body: ",remoteMessage.getData().get("body"),true);
        }
    }

    private void mostrarNotificacion(String title, String body,String data){

        //++++++++++++++++++++++++++++++++
        Intent intent = new Intent(this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Intent intentButton = new Intent(MenuActivity.BUTTON_ACTION);
        intentButton.putExtra("data",data);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,MenuActivity.REQUEST_BUTTON, intentButton , PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);



        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_1_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle(title)
                .setContentText(body)
                //.setAutoCancel(true)
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setVibrate(new long[]{0,1000,500,1000})
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setFullScreenIntent(pendingIntent, true)
                .setAutoCancel(true)
                //.setExtras(bundle)
                .setContentIntent(pendingIntent);
        //.addAction(R.mipmap.ic_launcher, "Toast", actionIntent);


        notificationBuilder.addAction(R.mipmap.ic_launcher,"Ver Pedido",actionIntent);




        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");


            notificationManager.createNotificationChannel(channel1);

        }

        notificationManager.notify(1,notificationBuilder.build());



    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Utilities.SetLog("Message Notification token: ",s,true);
    }


    public void ValidaPedidoActivo(final String title, final String body) throws JSONException {

        String url = WSkeys.URL_BASE + WSkeys.URL_PEDIDO_ACTIVO;
        Utilities.SetLog("APPFB-PEDIDOACTIVO",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ParserPedidoActivo(response,title,body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utilities.SetLog("ERROR RESPONSE",error.toString(),WSkeys.log);

                Toast.makeText(getContext(),R.string.errorlistener,Toast.LENGTH_LONG).show();
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

    public void ParserPedidoActivo(JSONObject response, String title, String body) throws JSONException {
        Gson gson = new Gson();
        Utilities.SetLog("PARSER-APPFB_ACTIVO",response.toString(),WSkeys.log);

        // si el response regresa ok, entonces si inicia la sesión
        if (response.getInt("codeError") == (WSkeys.okresponse)) {
            JSONObject jo_data = response.getJSONObject(WSkeys.data);
            //pedidoActivo = gson.fromJson(jo_data.toString(), PedidoActivo.class);
            seguimientoPedido = gson.fromJson(jo_data.toString(), SeguimientoPedido.class);
            seguimientoPedido.setTipo("3");
            client.setSeguimientoPedido(seguimientoPedido);
            Utilities.SetLog("PARSER-STATUS_ACTIVO",seguimientoPedido.getStatus(),WSkeys.log);

            if (interfaceNotificationNewOrder == null && client.getContextNewOrder() != null) {

                interfaceNotificationNewOrder = (MessageReceiverCallback) client.getContextNewOrder();
                interfaceNotificationNewOrder.getReceiverEstatusPedido(seguimientoPedido.getStatus(), jo_data.toString());
                client.setContextNewOrder(null);
            }

            mostrarNotificacion(title,body , sJSONObject);


            Intent intent = new Intent(getApplicationContext(), PedidoAceptadoActivity.class);
            String json_pedido = gson.toJson(seguimientoPedido);
            intent.putExtra("pedido_data",json_pedido);
            intent.putExtra("idPedido",seguimientoPedido.getId());
            intent.putExtra("pedido_data",json_pedido);
            intent.putExtra("status",seguimientoPedido.getStatus());
            startActivity(intent);

        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else if (response.getInt("codeError") == (WSkeys.no_error_ok)) {

            Utilities.SetLog("APPFB_ACTIVO",response.getString(WSkeys.messageError),WSkeys.log);
        }else{
            Toast.makeText(getContext(),response.getString(WSkeys.messageError),Toast.LENGTH_LONG).show();
        }
    }
}
