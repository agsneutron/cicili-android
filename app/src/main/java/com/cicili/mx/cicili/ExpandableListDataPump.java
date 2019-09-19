package com.cicili.mx.cicili;

import android.app.Application;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.MotivoCancela;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cicili.mx.cicili.domain.Client.getContext;

public class ExpandableListDataPump {

    Application application = (Application) Client.getContext();
    Client client = (Client) application;
    List<String> categories;
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();



        List<String> categoria1 = new ArrayList<String>();
        categoria1.add("Pregunta 1");
        categoria1.add("Pregunta 2");
        categoria1.add("Pregunta 3");
        categoria1.add("Pregunta 4");
        categoria1.add("Pregunta 5");

        List<String> categoria2 = new ArrayList<String>();
        categoria2.add("Pregunta 1");
        categoria2.add("Pregunta 2");
        categoria2.add("Pregunta 3");
        categoria2.add("Pregunta 4");
        categoria2.add("Pregunta 5");

        List<String> categoria3 = new ArrayList<String>();
        categoria3.add("Pregunta 1");
        categoria3.add("Pregunta 2");
        categoria3.add("Pregunta 3");
        categoria3.add("Pregunta 4");
        categoria3.add("Pregunta 5");

        expandableListDetail.put("CATEGORIA 1", categoria1);
        expandableListDetail.put("CATEGORIA 2", categoria2);
        expandableListDetail.put("CATEGORIA 3", categoria3);
        return expandableListDetail;
    }
}
