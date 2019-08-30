package com.cicili.mx.cicili.io;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.cicili.mx.cicili.LoginActivity;
import com.cicili.mx.cicili.R;
import com.cicili.mx.cicili.domain.AddressData;
import com.cicili.mx.cicili.domain.AutotanquesDisponibles;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.PaymentData;
import com.cicili.mx.cicili.domain.Pedido;
import com.cicili.mx.cicili.domain.PedidoData;
import com.cicili.mx.cicili.domain.RfcData;
import com.cicili.mx.cicili.domain.WSkeys;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ariaocho on 27/06/19.
 */

public class Utilities {

    //se activa para finalizar la sesión y abrir layout de inicio de sesión
    public static void ExpiraSesion(Activity activity, String mensaje){
        Toast.makeText(activity,mensaje,Toast.LENGTH_LONG).show();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    //mostrar en run log mensajes para depuración
    public static void SetLog(String tittle, String msg, Boolean isenabled){
        if (isenabled) {
            System.out.println("<- EXCEC LOG ->   " + tittle + msg);
            //Log.e(tittle,msg);
        }
    }

    //Mostrar mensajes tipo TOAST
    public static void SetMessage(Activity activity, String mensaje){
        Toast.makeText(activity,mensaje,Toast.LENGTH_LONG).show();
    }

    //función para activar alertas tipo dialogo
    public static void ShowDialogAlert(String message, String tittle, final Activity activity){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setCancelable(false);
        dialog.setTitle(tittle);
        dialog.setMessage(message);
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        try {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    dialog.show();
                }
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //función para mostrar alertas
    public static void ShowAlert(String error, String tittle, Activity activity){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setCancelable(false);
        dialog.setTitle(tittle);
        dialog.setMessage(error);
        dialog.setPositiveButton("Aceptar", null);
        try {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    dialog.show();
                }
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    public static boolean isCodeValid(String code) {
        //TODO: Replace this with your own logic
        return code.length() > 4;
    }

    public static boolean isFieldValid(TextInputEditText field){
        if ((field != null) && (!TextUtils.isEmpty(field.getText()))){
            return true;
        }
        else{
            return false;
        }
    }

    public static void SetClientData(JSONObject jousuario, Client client) throws JSONException {
        Gson gson = new Gson();
        ArrayList<AddressData> direccionAux = new ArrayList<AddressData>();
        ArrayList<PaymentData> paymentAux = new ArrayList<PaymentData>();
        //accessdata
        client.setIdcte(jousuario.getInt(WSkeys.idcte));
        client.setStatus(jousuario.getString(WSkeys.status));
        //client.setToken(jousuario.getString(WSkeys.token));
        client.setEmail(jousuario.getString(WSkeys.email));
        client.setCellphone(jousuario.getString(WSkeys.cel));
        client.setUsername(jousuario.getString(WSkeys.email));

        //personal data
        client.setName(jousuario.getString(WSkeys.name));
        client.setLastname(jousuario.getString(WSkeys.apepat));
        client.setLastsname(jousuario.getString(WSkeys.apemat));
        client.setDate(jousuario.getString(WSkeys.fecnac));
        client.setPhoto(jousuario.getString(WSkeys.img));

        //payment data
        JSONArray ja_payment = jousuario.getJSONArray(WSkeys.formapago);
        if (ja_payment.length() > 0) {
            Utilities.SetLog("LOGIN jo_payment", ja_payment.toString(), WSkeys.log);
            for (int i = 0; i < ja_payment.length(); i++) {
                JSONObject jo_payment = (JSONObject) ja_payment.get(i);
                Utilities.SetLog("jo_payment", jo_payment.toString(), WSkeys.log);
                PaymentData paymentData = gson.fromJson(jo_payment.toString(), PaymentData.class);
                paymentAux.add(paymentData);
            }
            client.setPaymentDataArrayList(paymentAux);
        }

        //address data
        JSONArray ja_address = jousuario.getJSONArray(WSkeys.direcciones);
        if (ja_address.length() > 0) {
            Utilities.SetLog("LOGIN ja_address", ja_address.toString(), WSkeys.log);
            for(int i=0; i<ja_address.length(); i++) {
                JSONObject jo_address = (JSONObject) ja_address.get(i);
                Utilities.SetLog("jo_ddress",jo_address.toString(),WSkeys.log);
                AddressData addressData= gson.fromJson(jo_address.toString() , AddressData.class);
                direccionAux.add(addressData);
            }
            client.setAddressDataArrayList(direccionAux);
        }
    }

    public static void SetPerfilData(JSONObject jousuario, Client client) throws JSONException {
        Gson gson = new Gson();
        ArrayList<AddressData> direccionAux = new ArrayList<AddressData>();
        ArrayList<PaymentData> paymentAux = new ArrayList<PaymentData>();
        //accessdata
        client.setIdcte(jousuario.getInt(WSkeys.idcte));
        client.setStatus(jousuario.getString(WSkeys.status));
        //client.setToken(jousuario.getString(WSkeys.token));
        client.setEmail(jousuario.getString(WSkeys.email));
        client.setCellphone(jousuario.getString(WSkeys.cel));
        client.setUsername(jousuario.getString(WSkeys.email));

        //personal data
        client.setName(jousuario.getString(WSkeys.name));
        client.setLastname(jousuario.getString(WSkeys.apepat));
        client.setLastsname(jousuario.getString(WSkeys.apemat));
        client.setDate(jousuario.getString(WSkeys.fecnac));
        client.setPhoto(jousuario.getString(WSkeys.img));
        client.setSexo(jousuario.getString(WSkeys.sexo));

    }


        public static void SetClientPaymentData(JSONObject jousuario, Client client) throws JSONException {
        client.setIdcte(jousuario.getInt(WSkeys.idcte));
        client.setStatus(jousuario.getString(WSkeys.status));
        client.setToken(jousuario.getString(WSkeys.token));
        client.setEmail(jousuario.getString(WSkeys.email));
        client.setCellphone(jousuario.getString(WSkeys.cel));
        client.setUsername(jousuario.getString(WSkeys.email));


    }

    public static void AddAddressData(JSONObject jo_address, Client client){
        Gson gson = new Gson();
        ArrayList<AddressData> direccionAux = new ArrayList<AddressData>();
        AddressData addressData= gson.fromJson(jo_address.toString() , AddressData.class);
        direccionAux.add(addressData);
        client.getAddressDataArrayList().add(client.getAddressDataArrayList().size(),addressData);
    }

    public static void UpdateAddressData(JSONObject jo_address, Client client,int pos){
        Gson gson = new Gson();
        ArrayList<AddressData> direccionAux = new ArrayList<AddressData>();
        AddressData addressData= gson.fromJson(jo_address.toString() , AddressData.class);
        direccionAux.add(addressData);
        client.getAddressDataArrayList().set(pos,addressData);
    }

    public static void AddRfcData(JSONObject jo_rfc, Client client){
        Gson gson = new Gson();
        ArrayList<RfcData> rfcAux = new ArrayList<RfcData>();
        RfcData rfcData= gson.fromJson(jo_rfc.toString() , RfcData.class);
        rfcAux.add(rfcData);
        if(client.getRfcDataArrayList() != null){
        client.getRfcDataArrayList().add(client.getRfcDataArrayList().size(), rfcData);
        }
        else{
            client.setRfcDataArrayList(rfcAux);
        }
    }

    public static void UpdateRfcData(JSONObject jo_rfc, Client client,int pos){
        Gson gson = new Gson();
        ArrayList<RfcData> rfcAux = new ArrayList<RfcData>();
        RfcData rfcData= gson.fromJson(jo_rfc.toString() , RfcData.class);
        rfcAux.add(rfcData);
        client.getRfcDataArrayList().set(pos,rfcData);
    }

    public static  void SetRfcData(JSONObject jorfc,Client client) throws JSONException {
        ArrayList<RfcData> rfcAux = new ArrayList<RfcData>();
        Gson gson = new Gson();
        JSONArray ja_rfc = jorfc.getJSONArray(WSkeys.data);
        if (ja_rfc.length() > 0) {
            Utilities.SetLog("LOGIN ja_rfc", ja_rfc.toString(), WSkeys.log);
            for(int i=0; i<ja_rfc.length(); i++) {
                JSONObject jo_rfc = (JSONObject) ja_rfc.get(i);
                Utilities.SetLog("jo_rfc",jo_rfc.toString(),WSkeys.log);
                RfcData rfcData= gson.fromJson(jo_rfc.toString() , RfcData.class);
                rfcAux.add(rfcData);
            }
            client.setRfcDataArrayList(rfcAux);
        }
    }

    public static  void SetPedidoData(JSONObject jopedido,Client client) throws JSONException {
        ArrayList<PedidoData> pedidoAux = new ArrayList<PedidoData>();
        Gson gson = new Gson();
        JSONArray ja_pedido = jopedido.getJSONArray(WSkeys.data);
        if (ja_pedido.length() > 0) {
            Utilities.SetLog("SET ja_pedido", ja_pedido.toString(), WSkeys.log);
            for(int i=0; i<ja_pedido.length(); i++) {
                JSONObject jo_pedido = (JSONObject) ja_pedido.get(i);
                Utilities.SetLog("jo_pedido",jo_pedido.toString(),WSkeys.log);
                PedidoData pedidoData= gson.fromJson(jo_pedido.toString() , PedidoData.class);
                pedidoAux.add(pedidoData);
            }
            client.setPedidosDataArrayList(pedidoAux);
        }
    }

    public static void AddPaymentData(String js_payment, Client client){

        Gson gson = new Gson();
        PaymentData paymentData = gson.fromJson(js_payment , PaymentData.class);
        ArrayList<PaymentData> paymentAux = new ArrayList<PaymentData>();
        paymentAux.add(paymentData);
        client.getPaymentDataArrayList().add(client.getPaymentDataArrayList().size(),paymentData);

        Utilities.SetLog("PAYMENTDTA",String.valueOf(paymentData.getId()),WSkeys.log);

    }

    public static void UpdatePaymentData(String js_payment, Client client,int pos){
        Gson gson = new Gson();
        ArrayList<PaymentData> paymentAux = new ArrayList<PaymentData>();
        PaymentData paymentData= gson.fromJson(js_payment , PaymentData.class);
        paymentAux.add(paymentData);
        client.getPaymentDataArrayList().set(pos,paymentData);
        Utilities.SetLog("PAYMENTDTA",String.valueOf(paymentData.getId()),WSkeys.log);

    }

    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public enum PasswordStrength
    {

        WEAK(0, Color.RED), MEDIUM(1, Color.argb(255, 220, 185, 0)), STRONG(2, Color.GREEN), VERY_STRONG(3, Color.BLUE);

        //--------REQUIREMENTS--------
        static int REQUIRED_LENGTH = 8;
        static int MAXIMUM_LENGTH = 12;
        static boolean REQUIRE_SPECIAL_CHARACTERS = true;
        static boolean REQUIRE_DIGITS = true;
        static boolean REQUIRE_LOWER_CASE = true;
        static boolean REQUIRE_UPPER_CASE = true;

        int resId;
        int color;

        PasswordStrength(int resId, int color)
        {
            this.resId = resId;
            this.color = color;
        }

        public int getValue()
        {
            return resId;
        }

        public int getColor()
        {
            return color;
        }

        public static PasswordStrength calculateStrength(String password)
        {
            int currentScore = 0;
            boolean sawUpper = false;
            boolean sawLower = false;
            boolean sawDigit = false;
            boolean sawSpecial = false;

            for (int i = 0; i < password.length(); i++)
            {
                char c = password.charAt(i);

                if (!sawSpecial && !Character.isLetterOrDigit(c))
                {
                    currentScore += 1;
                    sawSpecial = true;
                }
                else
                {
                    if (!sawDigit && Character.isDigit(c))
                    {
                        currentScore += 1;
                        sawDigit = true;
                    }
                    else
                    {
                        if (!sawUpper || !sawLower)
                        {
                            if (Character.isUpperCase(c))
                                sawUpper = true;
                            else
                                sawLower = true;
                            if (sawUpper && sawLower)
                                currentScore += 1;
                        }
                    }
                }
            }

            if (password.length() > REQUIRED_LENGTH)
            {
                if ((REQUIRE_SPECIAL_CHARACTERS && !sawSpecial) || (REQUIRE_UPPER_CASE && !sawUpper) || (REQUIRE_LOWER_CASE && !sawLower) || (REQUIRE_DIGITS && !sawDigit))
                {
                    currentScore = 1;
                }
                else
                {
                    currentScore = 2;
                    if (password.length() > MAXIMUM_LENGTH)
                    {
                        currentScore = 3;
                    }
                }
            }
            else
            {
                currentScore = 0;
            }

            switch (currentScore)
            {
                case 0:
                    return WEAK;
                case 1:
                    return MEDIUM;
                case 2:
                    return STRONG;
                case 3:
                    return VERY_STRONG;
                default:
            }

            return VERY_STRONG;
        }

    }


}
