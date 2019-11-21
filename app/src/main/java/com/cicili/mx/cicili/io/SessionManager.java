package com.cicili.mx.cicili.io;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.cicili.mx.cicili.LoginActivity;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPreferences;
    Editor editor;
    Context _context;
    int PRIV_MODE = 0;
    private static final String SP_NAME = "CiciliSessionPref";
    private static final String IS_LOGIN = "HasSession";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PSW = "psw";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        sharedPreferences = _context.getSharedPreferences(SP_NAME, PRIV_MODE);
        editor = sharedPreferences.edit();
    }

    //createnewsession
    public void createSession(String token, String email, String psw){

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PSW,psw);
        editor.commit();
    }

    //validatelogin
    public boolean checkLogin(){
        if(!this.hasSession()){
            //no session
            Intent intent = new Intent(_context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent);
            return true;
        }
        return false;
    }



    //getsession
    public HashMap<String, String> getSession(){

        HashMap<String, String> session_data = new HashMap<String, String>();
        session_data.put(KEY_TOKEN, sharedPreferences.getString(KEY_TOKEN, null));
        session_data.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL, null));
        session_data.put(KEY_PSW, sharedPreferences.getString(KEY_PSW, null));
        return session_data;
    }

    //drop session
    public void logoutSession(){
        editor.clear();
        editor.commit();
        Intent intent = new Intent(_context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(intent);
    }


    // Check for login
    public boolean hasSession(){
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }
}
