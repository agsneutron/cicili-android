package com.cicili.mx.cicili;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.PaymentData;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private TextInputEditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String token="";
    private String token_firebase="";
    Application application = (Application) Client.getContext();
    Client client = (Client) application;
    Boolean validated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        mEmailView = (TextInputEditText) findViewById(R.id.email);


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });



        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterClient.class);
                startActivity(intent);
            }
        });
        Button mForgotPswButton = (Button) findViewById(R.id.forgotpsw_button);
        mForgotPswButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RequestPassword.class);
                startActivity(intent);
            }
        });


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Utilities.SetLog("getInstanceId failed: ",task.getException().toString(),true);
                            return;
                        }

                        // Get new Instance ID token
                        token_firebase = task.getResult().getToken();
                        Utilities.SetLog("TOKEN FIREBASE desde Login: ",token_firebase,true);

                        // Log and toast
                        //String msg = getString(R.string., token);
                        //Toast.makeText(MenuActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !Utilities.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Utilities.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            UserLoginTask(email, password);

        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }




    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public void UserLoginTask(final String mEmail, final String mPassword) {

        String url = WSkeys.URL_BASE + WSkeys.URL_LOGIN;
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserData(response, mEmail, mPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Log.e("El error", error.toString());
                Snackbar.make(mEmailView, R.string.errorlistener, Snackbar.LENGTH_SHORT)
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
                params.put(WSkeys.PUSERNAME, mEmail);
                params.put(WSkeys.PPASSWORD, mPassword);
                params.put(WSkeys.TOKENFIREBASE, token_firebase);
                Log.e("PARAMETROS", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);

    }


    public void ParserData(String response, String userName, String userPassword) throws JSONException {
        showProgress(false);
        //Log.e("LoginResponse", response);
        JSONObject respuesta = new JSONObject(response);
        Utilities.SetLog("LOGIN response",response,true);

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)){
            JSONObject jousuario = respuesta.getJSONObject(WSkeys.data);
            //JSONArray jousuario = respuesta.getJSONArray(WSkeys.data);

            Snackbar.make(mEmailView, R.string.successlogin, Snackbar.LENGTH_SHORT).show();

            //Utilities.SetLog("LOGIN idcte",Integer.toString(jousuario.getInt(WSkeys.idcte)).toString(),true);
            client.setIdcte(jousuario.getInt(WSkeys.idcte));
            client.setStatus(jousuario.getString(WSkeys.status));
            client.setToken(jousuario.getString(WSkeys.token));
            client.setEmail(jousuario.getString(WSkeys.email));
            client.setCellphone(jousuario.getString(WSkeys.cel));
            client.setUsername(userName);
            client.setSexo(jousuario.getString(WSkeys.sexo));
            client.setPhoto(jousuario.getString(WSkeys.img));
            Utilities.SetLog("LOGIN IMAGEN",jousuario.getString(WSkeys.img),WSkeys.log);
            //client.setSexo("");
            Utilities.SetClientData(jousuario,client);


            if (client.getStatus().equals(WSkeys.verifica_codigo)) {
                // dialog to validate SMS

                DialogValidate(userName);
            }


            /*if (client.getStatus().equals(WSkeys.datos_personales)){

                Intent intent = new Intent(LoginActivity.this, PerfilData.class);
                intent.putExtra("active",WSkeys.datos_personales);
                startActivity(intent);
            }
            if(client.getStatus().equals(WSkeys.datos_pago)){
                Intent intent = new Intent(LoginActivity.this, PerfilData.class);
                intent.putExtra("active",WSkeys.datos_pago);
                startActivity(intent);
            }

            if(client.getStatus().equals(WSkeys.datos_direccion)){
                Intent intent = new Intent(LoginActivity.this, PerfilData.class);
                intent.putExtra("active",WSkeys.datos_direccion);
                startActivity(intent);
            }*/



            if(client.getStatus().equals(WSkeys.completo)) {
                Utilities.SetClientData(jousuario, client);

                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }

        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(mEmailView, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    /**
    * SECTION TO VALIDATE SMS WHEN USER DIDN`T VALIDATE THE SMS
    * */


    public void DialogValidate(final String sUser){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setCancelable(true)
                .setView(R.layout.validate_sms_layout) //<-- layout containing EditText
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //All of the fun happens inside the CustomListener now.
                        //I had to move it to enable data validation.
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Validar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //All of the fun happens inside the CustomListener now.
                        //I had to move it to enable data validation.

                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextInputEditText mcode = (TextInputEditText) alertDialog.findViewById(R.id.code);
        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new LoginActivity.CustomListener(alertDialog,sUser,mcode));
    }

    class CustomListener implements View.OnClickListener {
        private final Dialog dialog;
        private final String user;
        private final TextInputEditText mcode;
        public CustomListener(Dialog dialog,String sUser,TextInputEditText mcode ) {
            this.dialog = dialog;
            this.user = sUser;
            this.mcode = mcode;
        }
        @Override
        public void onClick(View v) {
            // put your code here
            //TextInputEditText mcode = (TextInputEditText)v.findViewById(R.id.code);

            String mValue = mcode.getText().toString()+"";
            if(mValue.isEmpty()){
                mcode.setError("Campo requerido");
                //dialog.dismiss();
            }else{
                UserCodeTask(mValue,dialog,user);
            }
        }
    }
    /**
     * Represents an asynchronous registration task used to register
     * the user.
     */
    public void UserCodeTask(final String mCode, final DialogInterface dialog, final String sUser) {

        String url = WSkeys.URL_BASE + WSkeys.URL_VALIDATETMPPASSWORD;
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserCode(response, dialog, mCode, sUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(mEmailView, R.string.errorlistener, Snackbar.LENGTH_SHORT)
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
                params.put(WSkeys.user, sUser);
                params.put(WSkeys.tmppsw, mCode);
                Log.e("PARAMETROS", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type", "application/json; charset=utf-8");
                //params.put("Authorization", token);
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);

    }

    public void ParserCode(String response, DialogInterface dialog, String code, String user) throws JSONException {

        Log.e("CodeResponse", response);
        JSONObject respuesta = new JSONObject(response);

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)){
            String data = respuesta.getString(WSkeys.data);

            if (data.equals("true")){
                Snackbar.make(mEmailView, R.string.successpendingvalidation, Snackbar.LENGTH_LONG)
                        .show();
                //Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                //intent.putExtra("user",user);
                //startActivity(intent);
                //DialogPersonal(user);
                client.setStatus(WSkeys.datos_personales);
            }
            else{
                dialog.dismiss();
                Snackbar.make(mEmailView, R.string.errorvalidation, Snackbar.LENGTH_LONG)
                        .show();
            }
        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(mEmailView, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }


    //END SECTION TO VALIDATE SMS





}

