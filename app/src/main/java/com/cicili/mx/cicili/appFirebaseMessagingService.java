package com.cicili.mx.cicili;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.cicili.mx.cicili.domain.AddressData;
import com.cicili.mx.cicili.domain.Client;
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

public class appFirebaseMessagingService extends FirebaseMessagingService{


    Application application = (Application) Client.getContext();
    Client client = (Client) application;
    SeguimientoPedido seguimientoPedido;
    Gson gson = new Gson();
    String sJSONObject;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null){

            if (remoteMessage.getData().get("tipo").toString().equals("3")){

                GsonBuilder gsonMapBuilder = new GsonBuilder();
                Gson gsonObject = gsonMapBuilder.create();
                sJSONObject = gsonObject.toJson(remoteMessage.getData());
                Utilities.SetLog("NOTIFICATION JSONObject", sJSONObject, WSkeys.log);
                seguimientoPedido = gson.fromJson(sJSONObject, SeguimientoPedido.class);
                client.setSeguimientoPedido(seguimientoPedido);
            }

            mostrarNotificacion(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),sJSONObject);

            /*Utilities.SetLog("NOTIFICATION",remoteMessage.toString(), WSkeys.log);
            Utilities.SetLog("NOTIFICATION data",remoteMessage.getData().toString(), WSkeys.log);
            Utilities.SetLog("NOTIFICATION data",remoteMessage.getData().get("idPedido").toString(), WSkeys.log);
            Utilities.SetLog("NOTIFICATION data",remoteMessage.getData().get("conductor").toString(), WSkeys.log);
            Utilities.SetLog("NOTIFICATION data",remoteMessage.getData().get("clave").toString(), WSkeys.log);
            Utilities.SetLog("NOTIFICATION data",remoteMessage.getData().get("placa").toString(), WSkeys.log);
            Utilities.SetLog("NOTIFICATION data",remoteMessage.getData().get("color").toString(), WSkeys.log);
            Utilities.SetLog("NOTIFICATION data",remoteMessage.getData().get("tiempo").toString(), WSkeys.log);
            Utilities.SetLog("NOTIFICATION data",remoteMessage.getData().get("monto").toString(), WSkeys.log);
        */


        }
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

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
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(new long[]{0,1000,500,1000})
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                //.setAutoCancel(true)
                .setSound(soundUri)
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

        notificationManager.notify(0,notificationBuilder.build());



    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Utilities.SetLog("Message Notification token: ",s,true);
    }

}
