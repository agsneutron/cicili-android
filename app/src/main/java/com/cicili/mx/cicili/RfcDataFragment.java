package com.cicili.mx.cicili;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.RfcData;
import com.cicili.mx.cicili.domain.UsoCfdi;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RfcDataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RfcDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RfcDataFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Integer pos;

    private OnFragmentInteractionListener mListener;

    private String LOG = "RFC_DATA_FRAGMENT";
    View view;
    Spinner usoCFDI;
    TextInputEditText rfc,calle,numint,numext,cp, razonsocial;
    SwitchCompat favorito;
    AppCompatButton button;
    Application application = (Application) Client.getContext();
    Client client = (Client) application;


    private ArrayList<String> cfdiArray;
    private ArrayList<UsoCfdi> cfdiAux;
    private Integer cfdisel;
    private String cfdiname = "";
    private Integer selectedcfdi=0;

    public RfcDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RfcDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RfcDataFragment newInstance(String param1, String param2) {
        RfcDataFragment fragment = new RfcDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            Bundle bundle=getArguments();
            mParam1=bundle.getString("ARG_PARAM1");
            pos = Integer.parseInt(mParam1);
            Utilities.SetLog(LOG,mParam1,WSkeys.log);
            Utilities.SetLog(LOG+ "pos",String.valueOf(pos),WSkeys.log);
            Utilities.SetLog(LOG+"ARGS",bundle.getString("ARG_PARAM1"),WSkeys.log);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        view = inflater.inflate(R.layout.rfc_data_fragment, container, false);
        rfc = view.findViewById(R.id.rfc);
        //cp = view.findViewById(R.id.cp);
        razonsocial = view.findViewById(R.id.razonsocial);
        usoCFDI = (Spinner) view.findViewById(R.id.spinner1);
        //calle = view.findViewById(R.id.calle);
        //numext = view.findViewById(R.id.numext);
        //numint = view.findViewById(R.id.numint);
        /*favorito = view.findViewById(R.id.favorita);
        favorito.setChecked(false);*/

        razonsocial.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    usoCFDI.setFocusable(true);
                    usoCFDI.setFocusableInTouchMode(true);
                    usoCFDI.requestFocus();
                    return true;
                }
                return false;
            }
        });

        button = view.findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View focusView = null;
                Boolean error =  false;
                Integer valFavorito = 0;
                RfcData rfcData = new RfcData();
                if (!Utilities.isFieldValid(rfc)){
                    rfc.setError(getString(R.string.error_field_required));
                    error=true;
                    focusView = rfc;
                }
                /*if (!Utilities.isFieldValid(cp)){
                    cp.setError(getString(R.string.error_field_required));
                    error=true;
                    focusView = cp;
                }*/
                if(usoCFDI.getSelectedItemId()==0){
                    Snackbar.make(view, "Selecciona el uso del CFDI", Snackbar.LENGTH_LONG)
                            .show();
                    error=true;
                    focusView = usoCFDI;
                }
                /*if (!Utilities.isFieldValid(calle)){
                    calle.setError(getString(R.string.error_field_required));
                    error=true;
                    focusView = calle;
                }*/
                if (!Utilities.isFieldValid(razonsocial)){
                    razonsocial.setError(getString(R.string.error_field_required));
                    error=true;
                    focusView = razonsocial;
                }
                /*if (!Utilities.isFieldValid(numext)){
                    numext.setError(getString(R.string.error_field_required));
                    error=true;
                    focusView = numext;
                }*/


                /*if(favorito.isChecked()){
                    valFavorito =1;
                } else {
                    valFavorito =0;
                }*/



                if (!error){
                    UsoCfdi usoCfdi = new UsoCfdi();
                    usoCfdi.setId(cfdisel);
                    rfcData.setRfc(rfc.getText().toString());
                    rfcData.setRazonSocial(razonsocial.getText().toString());
                    rfcData.setUsoCfdi(usoCfdi);
                    try {
                        RfcDataTask(rfcData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        LlenaCFDI();

        /*cp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if((Utilities.isFieldValid(cp)) && cp.length()>= WSkeys.cplenght){
                    LlenaColonia(cp.getText().toString());
                }
                else {
                    Snackbar.make(cp, "Introduce un Código Postal para mostrar las colonias.", Snackbar.LENGTH_LONG)
                                .show();
                }

            }
        });*/

        // Spinner click listener
        usoCFDI.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();

        categories.add("Uso de CFDI");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        usoCFDI.setAdapter(dataAdapter);



        //If is called the fragment to edit data
        if (pos != null) {
            rfc.setText(client.getRfcDataArrayList().get(pos).getRfc());
           /* cp.setText(String.valueOf(client.getRfcDataArrayList().get(pos).getAsentamiento().getCp()));
            calle.setText(client.getRfcDataArrayList().get(pos).getCalle());
            numext.setText(client.getRfcDataArrayList().get(pos).getExterior());
            numint.setText(client.getRfcDataArrayList().get(pos).getInterior());
            selectedtown= client.getRfcDataArrayList().get(pos).getAsentamiento().getId();*/
            selectedcfdi= client.getRfcDataArrayList().get(pos).getUsoCfdi().getId();
            Utilities.SetLog(LOG+"town",String.valueOf(selectedcfdi),WSkeys.log);
            LlenaCFDI();
            razonsocial.setText(client.getRfcDataArrayList().get(pos).getRazonSocial());
            button.setText("Actualizar");
        }
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        Log.e("onItemSelected",String.valueOf(i));
        if (i!=0) {
            cfdisel = cfdiAux.get(i).getId();
            cfdiname = cfdiAux.get(i).getText();

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    public void LlenaCFDI(){


        String url = WSkeys.URL_BASE + WSkeys.URL_CFDI;
        Utilities.SetLog("LLENAUSOCFDI",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserCFDI(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(view, R.string.errorlistener, Snackbar.LENGTH_SHORT)
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
                params.put("Authorization", client.getToken());
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);

    }

    public void ParserCFDI(String response) throws JSONException {

        Utilities.SetLog("RESPONSE_CFDI",response,WSkeys.log);
        //Log.e("CodeResponse", response);
        cfdiAux = new ArrayList<UsoCfdi>();
        cfdiArray = new ArrayList<String>();
        JSONObject respuesta = new JSONObject(response);
        Integer posselected =0;

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)) {
            //ontener nivel de data
            //Utilities.SetLog("RESPONSEASENTAMIENTOS",data,WSkeys.log);
            JSONArray ja_usocfdi = respuesta.getJSONArray(WSkeys.data);
            Utilities.SetLog("CFDIARRAY",ja_usocfdi.toString(),WSkeys.log);
            for(int i=0; i<ja_usocfdi.length(); i++){
                UsoCfdi usoCfdi = new UsoCfdi();
                try {
                    if(i==0){
                        usoCfdi.setId(0);
                        usoCfdi.setText("Selecciona uso de CFDI");
                        cfdiAux.add(usoCfdi);
                        cfdiArray.add("Selecciona uso de CFDI");
                    }
                    JSONObject jsonObject = (JSONObject) ja_usocfdi.get(i);
                    usoCfdi.setId(jsonObject.getInt("id"));
                    usoCfdi.setText(jsonObject.getString("text"));
                    cfdiAux.add(usoCfdi);
                    cfdiArray.add(jsonObject.getString("text"));
                    Utilities.SetLog(LOG+"id",String.valueOf(jsonObject.getInt("id")) + "-" + String.valueOf(selectedcfdi)+ "-"+i,WSkeys.log);
                    if(jsonObject.getInt("id") == selectedcfdi){
                        posselected = i+1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            usoCFDI.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,cfdiArray));
            usoCFDI.setSelection(posselected);
        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(view, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }


    public void RfcDataTask(RfcData rfcData) throws JSONException {

        Gson gson = new Gson();
        String json;
        String url;
        JSONObject params;

        if(pos != null) {
            //toupdate
            rfcData.setId(client.getRfcDataArrayList().get(pos).getId());
            json = gson.toJson(rfcData);
            params = new JSONObject(json);
            Log.e("RFCValuesUpdate--", json);
            url = WSkeys.URL_BASE + WSkeys.URL_RFCUPDATE;
        }else{

            //toadd
            json = gson.toJson(rfcData);
            params = new JSONObject(json);
            Log.e("RFCValuePairs--", json);
            url = WSkeys.URL_BASE + WSkeys.URL_RFCDATA;

        }

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ParserRFC(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(view, R.string.errorlistener, Snackbar.LENGTH_SHORT)
                        .show();
            }
        }) {
            @Override
            public String getBodyContentType()
            {
                return "application/json";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                /*params.put(WSkeys.user, client.getUsername());
                params.put(WSkeys.name, vname);
                params.put(WSkeys.apepat, vpat);
                params.put(WSkeys.apemat, vmat);
                params.put(WSkeys.fechanacimiento, vnac);
                Log.e("PARAMETROS", params.toString());*/
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Authorization", client.getToken());
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);

    }

    public void ParserRFC(JSONObject respuesta) throws JSONException {

        Utilities.SetLog("ParserRFC",respuesta.toString(),WSkeys.log);


        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)){

            String data = respuesta.getString(WSkeys.data);
            JSONObject jo_rfc = respuesta.getJSONObject(WSkeys.data);
            if(pos!=null){
                Utilities.UpdateRfcData(jo_rfc,client,pos);
            }else {
                Utilities.AddRfcData(jo_rfc, client);
            }
            Snackbar.make(view, R.string.successrfcvalidation, Snackbar.LENGTH_LONG)
                    .show();
            Intent intent = new Intent(getContext(),MenuActivity.class);
            startActivity(intent);
            getActivity().finish();


        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(view, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }

    }

}
