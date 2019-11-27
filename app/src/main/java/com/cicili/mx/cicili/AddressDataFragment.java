package com.cicili.mx.cicili;

import android.Manifest;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cicili.mx.cicili.domain.AddressData;
import com.cicili.mx.cicili.domain.Asentamiento;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddressDataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddressDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddressDataFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Integer pos;

    private OnFragmentInteractionListener mListener;

    private String LOG = "ADDRESS_DATA_FRAGMENT";
    View view;
    Spinner colonia;
    TextInputEditText alias,calle,numint,numext,cp;
    SwitchCompat favorito;
    TextView switchtxt;
    AppCompatButton button;
    Application application = (Application) Client.getContext();
    Client client = (Client) application;

    private ArrayList<String> asentamientoArray;
    private ArrayList<Asentamiento> asentamientoAux;
    private Integer asentamientosel;
    private String asentamientoname = "";

    //mapa
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private Double LatFTA=0.0 ,LonFTA=0.0;
    private Integer selectedtown=0;

    public AddressDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddressDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddressDataFragment newInstance(String param1, String param2) {
        AddressDataFragment fragment = new AddressDataFragment();
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

            if (bundle.getString("ARG_PARAM1")!=null) {
                Utilities.SetLog(LOG, bundle.toString(), WSkeys.log);
                Utilities.SetLog(LOG, mParam1, WSkeys.log);
                Utilities.SetLog(LOG + "pos", String.valueOf(pos), WSkeys.log);
                mParam1 = bundle.getString("ARG_PARAM1");
                pos = Integer.parseInt(mParam1);
                Utilities.SetLog(LOG + "ARGS", bundle.getString("ARG_PARAM1"), WSkeys.log);
            }
            else {
                pos=null;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        view = inflater.inflate(R.layout.address_data_fragment, container, false);
        alias = view.findViewById(R.id.alias);
        cp = view.findViewById(R.id.cp);
        colonia = (Spinner) view.findViewById(R.id.spinner1);
        calle = view.findViewById(R.id.calle);
        numext = view.findViewById(R.id.numext);
        numint = view.findViewById(R.id.numint);
        favorito = view.findViewById(R.id.favorita);
        favorito.setChecked(false);
        switchtxt = view.findViewById(R.id.switchtxt);

        button = view.findViewById(R.id.register_address);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View focusView = null;
                Boolean error =  false;
                Integer valFavorito = 0;
                AddressData addressData = new AddressData();
                if (!Utilities.isFieldValid(alias)){
                    alias.setError(getString(R.string.error_field_required));
                    error=true;
                    focusView = alias;
                }
                if (!Utilities.isFieldValid(cp)){
                    cp.setError(getString(R.string.error_field_required));
                    error=true;
                    focusView = cp;
                }
                if(colonia.getSelectedItemId()==0){
                    Snackbar.make(colonia, "Selecciona una Colonia", Snackbar.LENGTH_LONG)
                            .show();
                    error=true;
                    focusView = colonia;
                }
                if (!Utilities.isFieldValid(calle)){
                    calle.setError(getString(R.string.error_field_required));
                    error=true;
                    focusView = calle;
                }
                if (!Utilities.isFieldValid(numext)){
                    numext.setError(getString(R.string.error_field_required));
                    error=true;
                    focusView = numext;
                }


                if(favorito.isChecked()){
                    valFavorito =1;
                } else {
                    valFavorito =0;
                }



                if (!error){
                    Asentamiento asentamiento = new Asentamiento();
                    asentamiento.setId(asentamientosel);
                    asentamiento.setText(asentamientoname);
                    addressData.setAlias(alias.getText().toString());
                    addressData.setCp(cp.getText().toString());
                    addressData.setAsentamiento(asentamiento);
                    addressData.setCalle(calle.getText().toString());
                    addressData.setExterior(numext.getText().toString());
                    addressData.setInterior(numint.getText().toString());
                    addressData.setFavorito(valFavorito);
                    DialogValidate(addressData.getAlias(), addressData);
                    // addressData.setLatitud(LatFTA);
                    //addressData.setLongitud(LonFTA);
                }
            }
        });
        cp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(cp.getText().length()==5 && (Utilities.isFieldValid(cp))) {
                    LlenaColonia(cp.getText().toString());
                }
            }
        });


        cp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        });

        // Spinner click listener
        colonia.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();

        categories.add("Colonia");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        colonia.setAdapter(dataAdapter);

        //switch
        favorito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    switchtxt.setText("Si");
                }
                else {
                    switchtxt.setText("No");
                }
            }
        });




        //If is called the fragment to edit data
        if (pos != null) {
            alias.setText(client.getAddressDataArrayList().get(pos).getAlias());
            cp.setText(String.valueOf(client.getAddressDataArrayList().get(pos).getAsentamiento().getCp()));
            //colonia.setSelection(client.getAddressDataArrayList().get(pos).getAsentamiento().getId());
            calle.setText(client.getAddressDataArrayList().get(pos).getCalle());
            numext.setText(client.getAddressDataArrayList().get(pos).getExterior());
            numint.setText(client.getAddressDataArrayList().get(pos).getInterior());
            selectedtown= client.getAddressDataArrayList().get(pos).getAsentamiento().getId();
            Utilities.SetLog(LOG+"town",String.valueOf(selectedtown),WSkeys.log);
            LlenaColonia(cp.getText().toString());
            LatFTA = client.getAddressDataArrayList().get(pos).getLatitud();
            LonFTA = client.getAddressDataArrayList().get(pos).getLongitud();
            if (client.getAddressDataArrayList().get(pos).getFavorito().equals(1)){
                favorito.setChecked(true);
            }else{
                favorito.setChecked(false);
            }



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
            asentamientosel = asentamientoAux.get(i).getId();
            asentamientoname = asentamientoAux.get(i).getText();

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



    public void LlenaColonia(final String text){


        String url = WSkeys.URL_BASE + WSkeys.URL_SEARCHBYCP + text;
        Utilities.SetLog("CALLLLENACOLONIAS",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserTown(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(alias, R.string.errorlistener, Snackbar.LENGTH_SHORT)
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

    public void ParserTown(String response) throws JSONException {

        //Log.e("CodeResponse", response);
        asentamientoAux = new ArrayList<Asentamiento>();
        asentamientoArray = new ArrayList<String>();
        JSONObject respuesta = new JSONObject(response);
        Integer posselected =0;

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)) {
            //obtener nivel data
            String data = respuesta.getString(WSkeys.data);
            JSONObject jo_data = new JSONObject(data);
            //ontener nivel de
            //Utilities.SetLog("RESPONSEASENTAMIENTOS",data,WSkeys.log);
            JSONArray ja_asentamientos = jo_data.getJSONArray(WSkeys.asentamientos);
            Utilities.SetLog("ASENTAMIENTOSARRAY",ja_asentamientos.toString(),WSkeys.log);
            for(int i=0; i<ja_asentamientos.length(); i++){
                Asentamiento asentamiento = new Asentamiento();
                try {
                    if(i==0){
                        asentamiento.setId(0);
                        asentamiento.setText("Selecciona Colonia");
                        asentamientoAux.add(asentamiento);
                        asentamientoArray.add("Selecciona Colonia");
                    }
                    JSONObject jsonObject = (JSONObject) ja_asentamientos.get(i);
                    asentamiento.setId(jsonObject.getInt("id"));
                    asentamiento.setText(jsonObject.getString("text"));
                    asentamientoAux.add(asentamiento);
                    asentamientoArray.add(jsonObject.getString("text"));
                    Utilities.SetLog(LOG+"id",String.valueOf(jsonObject.getInt("id")) + "-" + String.valueOf(selectedtown)+ "-"+i,WSkeys.log);
                    if(jsonObject.getInt("id") == selectedtown){
                        posselected = i+1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            colonia.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,asentamientoArray));
            colonia.setSelection(posselected);
        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(alias, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }


    public void AddressDataTask(AddressData addressData, final Dialog dialog) throws JSONException {

        Gson gson = new Gson();
        String json;
        String url;
        JSONObject params;

        if(pos != null) {
            //toupdate
            addressData.setId(client.getAddressDataArrayList().get(pos).getId());
            json = gson.toJson(addressData);
            params = new JSONObject(json);
            Log.e("AddressValuesUpdate--", json);
            url = WSkeys.URL_BASE + WSkeys.URL_ADDRESUPPDATE;
        }else{

            //toadd
            json = gson.toJson(addressData);
            params = new JSONObject(json);
            Log.e("AddressValuePairs--", json);
            url = WSkeys.URL_BASE + WSkeys.URL_ADDRESSDATA;

        }

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dialog.dismiss();
                    ParserAddress(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(calle, R.string.errorlistener, Snackbar.LENGTH_SHORT)
                        .show();
                dialog.dismiss();
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

    public void ParserAddress(JSONObject respuesta) throws JSONException {

        Utilities.SetLog("ParserAddress",respuesta.toString(),WSkeys.log);


        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)){

            String data = respuesta.getString(WSkeys.data);
            JSONObject jo_address = respuesta.getJSONObject(WSkeys.data);
            if(pos!=null){
                Utilities.UpdateAddressData(jo_address,client,pos);
                Toast toast = Toast.makeText(getContext(),  R.string.successaddressupdate, Toast.LENGTH_LONG);
                toast.show();
                Intent intent = new Intent(getContext(),MenuActivity.class);
                startActivity(intent);
                getActivity().finish();
            }else {
                Utilities.AddAddressData(jo_address, client);
                Toast toast = Toast.makeText(getContext(),  R.string.successaddressvalidation, Toast.LENGTH_LONG);
                toast.show();
                getActivity().getSupportFragmentManager().beginTransaction().hide(this).commit();
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();
                getActivity().onBackPressed();
            }
            //Snackbar.make(calle, R.string.successaddressvalidation, Snackbar.LENGTH_LONG)
            //        .show();



        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(calle, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }

    }

    private void setPosition(Double lat, Double lon, String alias){

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(new LatLng(lat, lon));

        // Setting the title for the marker.
        // This will be displayed on taping the marker
        markerOptions.title("Ubicación de la dirección: " + alias).visible(true);

        // Clears the previously touched position
        mMap.clear();

        // Animating to the touched position
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), WSkeys.CAMERA_ZOOM));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Placing a marker on the touched position
        mMap.addMarker(markerOptions).setDraggable(true);
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(WSkeys.CAMERA_ZOOM), 2000, null);

        LatFTA = lat;
        LonFTA = lon;
    }

    private void setPinForLocation(final String alias){
        Utilities.SetLog(LOG, mMap.getCameraPosition().toString(),WSkeys.log);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title("Ubicación de la dirección:" + " " + alias).visible(true);

                // Clears the previously touched position
                mMap.clear();

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                mMap.addMarker(markerOptions).setDraggable(true);

                LatFTA=latLng.latitude;
                LonFTA=latLng.longitude;
            }
        });
    }

    private void getDeviceCurrentLocation() {
        Snackbar.make(alias, R.string.gettingDeviceLocation, Snackbar.LENGTH_SHORT)
                .show();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            Location getCurrentLocation = (Location) task.getResult();
                            moveCameratoCurrentLocation(WSkeys.CAMERA_ZOOM, new LatLng(getCurrentLocation.getLatitude(), getCurrentLocation.getLongitude()));

                        } else {
                            Utilities.SetLog("MAP-LOCATION", task.toString(), WSkeys.log);
                            Snackbar.make(alias, R.string.failedgetlocation, Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });

            }
        } catch (SecurityException e) {
            Utilities.SetLog("MAP", e.getMessage(), WSkeys.log);
        }
    }

    private void moveCameratoCurrentLocation(float zoom, LatLng ltln) {
        Utilities.SetLog("MAP_CAMERA", ltln.latitude + " " + ltln.longitude, WSkeys.log);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltln, zoom));

    }


    private void getMyLocationPermision() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;

            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION_PERMISSION);
            }
        }
        else{
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Utilities.SetLog("REQUESTPERMISSIONRESULT","in",WSkeys.log);
        mLocationPermissionGranted = false;

        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:{
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length;i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Utilities.SetLog("FAILDE PRERMISSIONS",String.valueOf(grantResults[i]),WSkeys.log);
                            return;
                        }
                    }
                    Utilities.SetLog("REQUESTPERMISSIONRESULT","GRANTED",WSkeys.log);
                    mLocationPermissionGranted = true;

                }
            }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //END MAP

    /**
     * SECTION TO VALIDATE SMS WHEN USER DIDN`T VALIDATE THE SMS
     * */


    public void DialogValidate(final String sUser, final  AddressData addressData){
        getMyLocationPermision();


        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        /////make map clear
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        dialog.setContentView(R.layout.validate_geolocation_layout);////your custom content

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        AppCompatTextView tv_alias = (AppCompatTextView) dialog.findViewById(R.id.alias);
        tv_alias.setText("Ubicación de: " +  addressData.getAlias());


        final MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(getActivity());

        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                mMap = googleMap;

                if (mLocationPermissionGranted) {
                    Utilities.SetLog(LOG, "mLocationPermissionGranted", WSkeys.log);
                    //getDeviceCurrentLocation();
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);


                    if (pos != null) {
                        setPosition(LatFTA,LonFTA,addressData.getAlias());

                    } else {

                        //SETPINFORLOCATIONNAME
                        Geocoder geocoder = new Geocoder(getActivity());
                        List<Address> list = new ArrayList<>();
                        String searchaddress = addressData.getAsentamiento().getPais() + ", " + addressData.getAsentamiento().getEstado() + ", " + addressData.getAsentamiento().getMunicipio() + ", " + addressData.getCp() + ", " + addressData.getAsentamiento().getText() + ", " + addressData.getCalle();
                        Utilities.SetLog("SEARCH ADDRESS", searchaddress, WSkeys.log);
                        try {
                            list = geocoder.getFromLocationName(searchaddress, 1);
                            Utilities.SetLog("list found", list.toString(), WSkeys.log);


                        } catch (IOException e) {
                            Utilities.SetLog(LOG, e.toString(), WSkeys.log);

                        }
                        if (list.size() > 0) {
                            Address address = list.get(0);
                            Utilities.SetLog("MAP_CAMERA_LIST", address.getLatitude() + " " + address.getLongitude(), WSkeys.log);
                            setPosition(address.getLatitude(), address.getLongitude(),addressData.getAlias());
                        } else {
                            getDeviceCurrentLocation();
                        }

                    }
                    setPinForLocation(addressData.getAlias());
                }
                /*LatLng posisiabsen = new LatLng(lat, lng); ////your lat lng
                googleMap.addMarker(new MarkerOptions().position(posisiabsen).title("La dirección de tu unev"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);*/

            }
        });

        Button dialogButton = (Button) dialog.findViewById(R.id.save_address);

        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dialog.dismiss();
                try {
                    if (LatFTA == 0.0 || LonFTA == 0.0) {
                        Snackbar.make(calle, "En el mapa ubica tu dirección.", Snackbar.LENGTH_LONG)
                                .show();

                    } else {
                        addressData.setLatitud(LatFTA);
                        addressData.setLongitud(LonFTA);
                        AddressDataTask(addressData, dialog);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

        dialog.show();
    }

}
