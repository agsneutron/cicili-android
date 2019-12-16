package com.cicili.mx.cicili;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ValidateActivity extends AppCompatActivity {

    private TextInputEditText mCodeView;
    private View mProgressView;
    private View mValidateSMSFormView;
    private String token="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();


        if (bundle != null) {
            //si trae token
            token = bundle.getString("token");
        } else {
            finish();
        }

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mCodeView = (TextInputEditText) findViewById(R.id.code);
        mProgressView = (View)findViewById(R.id.login_progress);
        mValidateSMSFormView = (View)findViewById(R.id.validate_sms_form);

        Button mValidateButton = (Button) findViewById(R.id.validate_button);
        mValidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(RegisterClient.this,MainActivity.class);
                //startActivity(intent);
                attemptValidate();
            }
        });





    }



    /**
     * Attempts to  validate the account specified by the sms code
     * If there are form errors (smscode), the
     * errors are presented and no actual register attempt is made.
     */
    private void attemptValidate() {

        // Reset errors.
        mCodeView.setError(null);


        // Store values at the time of the login attempt.
        String code = mCodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid code, if the user entered one.
        if (!TextUtils.isEmpty(code) && !Utilities.isCodeValid(code)) {
            mCodeView.setError(getString(R.string.error_invalid_code));
            focusView = mCodeView;
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
            UserCodeTask(code);

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

            mValidateSMSFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mValidateSMSFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mValidateSMSFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mValidateSMSFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }


    }
    /**
     * Represents an asynchronous registration task used to register
     * the user.
     */
    public void UserCodeTask(final String mCode) {

        String url = WSkeys.URL_BASE + WSkeys.URL_VALIDATECODE + mCode;
        RequestQueue queue = Volley.newRequestQueue(ValidateActivity.this);
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserCode(response, mCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                //Log.e("El error", error.toString());
                Snackbar.make(mCodeView, R.string.errorlistener, Snackbar.LENGTH_SHORT)
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
                params.put("Authorization", token);
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);

    }

    public void ParserCode(String response, String code) throws JSONException {
        showProgress(false);
        //Log.e("CodeResponse", response);
        JSONObject respuesta = new JSONObject(response);

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)){
            String data = respuesta.getString(WSkeys.data);

            if (data.equals("true")){
                Snackbar.make(mCodeView, R.string.successvalidation, Snackbar.LENGTH_LONG)
                        .show();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // Add the buttons
                builder.setMessage(R.string.successvalidate);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Intent intent = new Intent(ValidateActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();

                dialog.show();


            }
            else{
                Snackbar.make(mCodeView, R.string.errorvalidation, Snackbar.LENGTH_LONG)
                        .show();
            }
        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(mCodeView, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }


}
