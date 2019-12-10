package com.cicili.mx.cicili;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterClient extends AppCompatActivity {


    // UI references.
    private TextInputEditText mEmailView;
    private TextInputEditText mPasswordView;
    private TextInputEditText mCPasswordView;
    private TextInputEditText mCellPhoneView;
    private View mProgressView;
    private View mRegisterFormView;
    private String token ="";
    //ProgressBar progressBar;
    //TextView strengthStatus;
    private TextInputLayout tilpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        mEmailView = (TextInputEditText) findViewById(R.id.email);
        mCellPhoneView = (TextInputEditText) findViewById(R.id.cellphone);
        mPasswordView = (TextInputEditText) findViewById(R.id.password);
        mCPasswordView = (TextInputEditText) findViewById(R.id.cpassword);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //strengthStatus = (TextView) findViewById(R.id.passwordStrength);
        tilpassword = (TextInputLayout) findViewById(R.id.password_text_input);


        mPasswordView.addTextChangedListener(passwordStrength);
        mCPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {

                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        mRegisterFormView = (View)findViewById(R.id.register_login_form);
        mProgressView = (View)findViewById(R.id.login_progress);


        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(RegisterClient.this,MainActivity.class);
                //startActivity(intent);

                attemptRegister();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private final TextWatcher passwordStrength = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            verifyPasswordStrength(s.toString());
        }
    };


    private void verifyPasswordStrength(String password) {

        //if (TextView.VISIBLE != strengthStatus.getVisibility())
        //    return;

        if (password.isEmpty()) {
            tilpassword.setError("");
            tilpassword.setHelperText(getString(R.string.helptextpsw));
            //progressBar.setProgress(0);
            return;
        }

        /*if(Utilities.PasswordStrength.calculateStrength(password).getValue() < Utilities.PasswordStrength.STRONG.getValue())
        {
            strengthStatus.setText("Password should contain min of 6 characters and at least 1 lowercase, 1 uppercase and 1 numeric value");

        }*/
        Utilities.PasswordStrength str = Utilities.PasswordStrength.calculateStrength(password);

        Utilities.SetLog("VALUE", String.valueOf(str.getValue()) + str, WSkeys.log);
        //strengthStatus.setText(String.valueOf(str.getValue()));

        //strengthStatus.setTextColor(str.getColor());

        //tilpassword.setError("La contraseña debe contener de 8 a 12 carácteres, al menos una mayúscula, una minúscula y un carácter especial.");
        //tilpassword.setErrorTextColor(ColorStateList.valueOf(str.getColor()));
        Utilities.SetLog("VALUE", String.valueOf(str.getValue()), WSkeys.log);
        if (str.getValue() >=2 ) {
            mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_tick_b, 0);
            Utilities.SetLog("VALUE if", String.valueOf(str.getValue()), WSkeys.log);

        }
        else {
            mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_close_b, 0);
            Utilities.SetLog("VALUE else", String.valueOf(str.getValue()), WSkeys.log);


        }
        /*progressBar.getProgressDrawable().setColorFilter(str.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        if (str.getValue() == 0) {
            progressBar.setProgress(25);
        } else if (str.getValue() == 1) {
            progressBar.setProgress(50);
        } else if (str.getValue()==2) {
            progressBar.setProgress(75);
        } else {
            progressBar.setProgress(100);
        }*/
    }
    /**
     * Attempts to  register the account specified by the cellphone,email and password.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        Utilities.hideKeyboardwithoutPopulate(RegisterClient.this);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mCPasswordView.setError(null);
        mCellPhoneView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String cpassword = mCPasswordView.getText().toString();
        String cellphone = mCellPhoneView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !Utilities.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.helptextpsw));
            focusView = mPasswordView;
            cancel = true;
        }

        Utilities.PasswordStrength str = Utilities.PasswordStrength.calculateStrength(password);
        if (str.getValue() <2 ) {
            mPasswordView.setError(getString(R.string.helptextpsw));
            mPasswordView.setText("");
            focusView = mPasswordView;
            cancel = true;
        }
        Utilities.SetLog("VALUE", String.valueOf(str.getValue()) + str, WSkeys.log);


        // Check for a valid password, if the user entered one.
        /*if (!TextUtils.isEmpty(cpassword) && !Utilities.isPasswordValid(cpassword)) {
            mCPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mCPasswordView;
            cancel = true;
        }*/

        //Check for a valid comfirm password
        if(TextUtils.isEmpty(cpassword)){
            mCPasswordView.setError(getString(R.string.error_field_required));
            focusView = mCPasswordView;
            cancel = true;
        }

        if(!cpassword.equals(password)){
            mCPasswordView.setError(getString(R.string.error_invalid_cpassword));
            focusView = mCPasswordView;
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

        if(!Utilities.isCellNumber(cellphone) || cellphone.length()<10){
            mCellPhoneView.setError(getString(R.string.error_invalid_cellnumber));
            focusView = mCellPhoneView;
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
            UserRegisterTask(email, password, cellphone);

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

                mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
                mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
    }


    /**
     * Represents an asynchronous registration task used to register
     * the user.
     */
    public void UserRegisterTask(final String mEmail, final String mPassword, final String mCellphone) {

        String url = WSkeys.URL_BASE + WSkeys.URL_REGISTER;
        RequestQueue queue = Volley.newRequestQueue(RegisterClient.this);
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserData(response, mEmail, mPassword, mCellphone);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Log.e("El error", error.toString());
                Snackbar.make(mEmailView, R.string.errorlistener, Snackbar.LENGTH_LONG)
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
                params.put(WSkeys.PEMAIL, mEmail);
                params.put(WSkeys.PPASSWORD, mPassword);
                params.put(WSkeys.PCELLPHONE, mCellphone);
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


    public void ParserData(String response, String userName, String userPassword, String userCellphone) throws JSONException {
        showProgress(false);
        Log.e("LoginResponse", response);
        JSONObject respuesta = new JSONObject(response);

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)){
                JSONObject jousuario = respuesta.getJSONObject(WSkeys.data);

                token = jousuario.getString(WSkeys.token);
                Snackbar.make(mEmailView, R.string.successregister, Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(RegisterClient.this,ValidateActivity.class);
                intent.putExtra("token",token);
                startActivity(intent);
        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
                Snackbar.make(mRegisterFormView, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                        .show();
        }
    }

}
