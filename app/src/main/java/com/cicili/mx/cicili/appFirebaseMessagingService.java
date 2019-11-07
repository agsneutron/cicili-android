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

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.cicili.mx.cicili.domain.AddressData;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.Pedido;
import com.cicili.mx.cicili.domain.SeguimientoPedido;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.cicili.mx.cicili.domain.ChannelsNotification.CHANNEL_1_ID;

public class appFirebaseMessagingService extends FirebaseMessagingService {


    Application application = (Application) Client.getContext();
    Client client = (Client) application;
    SeguimientoPedido seguimientoPedido;

    Gson gson = new Gson();
    String sJSONObject;

    MessageReceiverCallback interfaceNotification;
    MessageReceiverCallback interfaceNotificationPipas;
    MessageReceiverCallback interfaceNotificationChat;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Utilities.SetLog("NOTIFICATION RemoteMessage", remoteMessage.getMessageId(), WSkeys.log);


        if (remoteMessage.getNotification() != null) {
            Utilities.SetLog("NOTIFICATION NOTIFICATION", remoteMessage.getData().toString(), WSkeys.log);

            if (interfaceNotification == null) {
                interfaceNotification = (MessageReceiverCallback) client.getMessageContext();
            }

            switch (Integer.parseInt(remoteMessage.getData().get("status"))) {
                case 2:

                    GsonBuilder gsonMapBuilder = new GsonBuilder();
                    Gson gsonObject = gsonMapBuilder.create();
                    sJSONObject = gsonObject.toJson(remoteMessage.getData());
                    Utilities.SetLog("NOTIFICATION JSONObject", sJSONObject, WSkeys.log);
                    seguimientoPedido = gson.fromJson(sJSONObject, SeguimientoPedido.class);
                    client.setSeguimientoPedido(seguimientoPedido);

                    mostrarNotificacion(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), sJSONObject);

                    break;
                case 5:
                    interfaceNotification.getReceiverEstatusPedido(remoteMessage.getData().get("status"), remoteMessage.getNotification().getBody());
                    break;
                case 6:
                    client.setComision(remoteMessage.getData().get("comision").toString());
                    client.setTotal(remoteMessage.getData().get("monto").toString());
                    interfaceNotification.getReceiverEstatusPedido(remoteMessage.getData().get("status"), remoteMessage.getNotification().getBody());
                    break;
                case 7:
                    interfaceNotification.getReceiverEstatusPedido(remoteMessage.getData().get("status"), remoteMessage.getNotification().getBody());
                    break;
                case 8:
                    interfaceNotification.getReceiverEstatusPedido(remoteMessage.getData().get("status"), remoteMessage.getNotification().getBody());
                    break;
                case 9:
                    interfaceNotification.getReceiverEstatusPedido(remoteMessage.getData().get("status"), remoteMessage.getNotification().getBody());
                    break;
                case 10:
                    interfaceNotification.getReceiverEstatusPedido(remoteMessage.getData().get("status"), remoteMessage.getNotification().getBody());
                    break;
                case 11:
                    if (interfaceNotificationPipas == null && client.getContextMap() != null) {
                        interfaceNotificationPipas = (MessageReceiverCallback) client.getContextMap();
                    }
                    interfaceNotificationPipas.getReceiverEstatusPedido("11", "Nuevas Pipas");
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
        if (remoteMessage.getNotification() != null) {
            Utilities.SetLog("Message Notification Body: ",remoteMessage.getNotification().getBody(),true);
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


}
