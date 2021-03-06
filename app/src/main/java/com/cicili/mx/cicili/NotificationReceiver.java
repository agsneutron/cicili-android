package com.cicili.mx.cicili;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cicili.mx.cicili.io.Utilities;


public class NotificationReceiver extends BroadcastReceiver {



    public interface OnNotificationReceiverListener{
        void onButtonClicked(Context context, Intent intent);
    }

    private final OnNotificationReceiverListener onNotificationReceiverListener;

    public NotificationReceiver(OnNotificationReceiverListener onNotificationReceiverListener) {
        this.onNotificationReceiverListener = onNotificationReceiverListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //String message = intent.getStringExtra("toastMessage");
        //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Utilities.SetLog("click boton : ","Pedido",true);
        if (intent.getAction().contentEquals(MenuActivity.BUTTON_ACTION)){
            Utilities.SetLog("entra boton : ","Pedido",true);
            onNotificationReceiverListener.onButtonClicked(context, intent);
        }

    }
}
