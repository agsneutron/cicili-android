package com.cicili.mx.cicili;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

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
import com.cicili.mx.cicili.domain.PedidoData;
import com.cicili.mx.cicili.domain.PedidoDetail;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.cicili.mx.cicili.domain.Client.getContext;

public class OrderDetailActivity extends AppCompatActivity implements  OnMapReadyCallback{

    String json_order="";
    TextView mIdView;
    TextView mContentView;
    TextView mDate;
    TextView mTime;
    TextView mCantidad;
    TextView mFormaPago;
    TextView mlbl1,mlbl2,mlbl3,mlbl4;
    MapFragment mapFragment;
    Button aclarar, facturar, cancelar;
    PedidoDetail pedidoData;
    String motivo_seleccionado="";
    String motivo_texto="";
    ArrayList<String> motivoArray = new ArrayList<String>();
    ArrayList<MotivoCancela> motivoAux = new ArrayList<MotivoCancela>();

    LinearLayout linearLayout;
    LinearLayout bottom_sheet;
    BottomSheetBehavior bsb;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private Double latOrderAddress, lonOrderAddress;
    private  Integer pos;

    private  String LOG = "ORDERS DETAIL ACT";
    Application application = (Application) getContext();
    Client client = (Client) application;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mIdView = findViewById(R.id.item_number);
        mContentView = findViewById(R.id.content);
        mDate = findViewById(R.id.date);
        mTime = findViewById(R.id.time);
        mCantidad = findViewById(R.id.cantidad);
        mFormaPago = findViewById(R.id.formaPago);
        mlbl1 = findViewById(R.id.lbl1);
        mlbl2 = findViewById(R.id.lbl2);
        mlbl3 = findViewById(R.id.lbl3);
        mlbl4 = findViewById(R.id.lbl4);
        aclarar = findViewById(R.id.aclaracion);
        facturar = findViewById(R.id.facturar);
        cancelar = findViewById(R.id.cancelar);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_order_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String id="";



        if (bundle != null) {
            //si trae fragment por activar
            id =  bundle.getString("ARG_PARAM1");
            pos = Integer.parseInt(id);
            Utilities.SetLog("LOG ID",id,WSkeys.log);
        }
        else{
            finish();
        }

        LlenaPedido();
        latOrderAddress = 0.0; // client.getPedidosDataArrayList().get(pos).getLatitud();
        lonOrderAddress = 0.0; //client.getPedidosDataArrayList().get(pos).getLongitud();

        facturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FacturaPedido(pos);
            }
        });

        aclarar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AclararPedido(pos);
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        //bottomsheet
        Spinner motivos = (Spinner) findViewById(R.id.motivos);
        LlenaMotivos(motivos);

        motivos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("onItemSelected",String.valueOf(i));

                motivo_seleccionado = String.valueOf(motivoAux.get(i).getId());
                motivo_texto = String.valueOf(motivoAux.get(i).getText());


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        bottom_sheet = (LinearLayout)findViewById(R.id.bottomSheetCancela);
        bsb = BottomSheetBehavior.from(bottom_sheet);
        bsb.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                String nuevoEstado = "";



                switch(newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        nuevoEstado = "STATE_COLLAPSED";
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nuevoEstado = "STATE_EXPANDED";
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        nuevoEstado = "STATE_HIDDEN";
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        nuevoEstado = "STATE_DRAGGING";
                        break;

                    case BottomSheetBehavior.STATE_SETTLING:
                        nuevoEstado = "STATE_SETTLING";
                        break;
                }

                Log.i("BottomSheets", "Nuevo estado: " + nuevoEstado);

                Button btnCancelaPedido = (Button) findViewById(R.id.cancela_pedido);
                btnCancelaPedido.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean cancel = false;
                        View focusView = null;
                        String error="";

                        // Check for a valid l/p, if the user entered one.
                        if (motivo_seleccionado.equals("")) {
                            // litros.setError(getString(R.string.error_invalid_value));
                            // focusView = litros;
                            error=getString(R.string.error_invalid_motivo);
                            cancel = true;
                        }


                        if (cancel) {
                            // There was an error
                            //focusView.requestFocus();
                            Toast toast = Toast.makeText(getContext(),  error, Toast.LENGTH_LONG);
                            toast.show();
                            //Snackbar.make(view, error, Snackbar.LENGTH_SHORT).show();
                            Utilities.SetLog("in cancel pedido", error, WSkeys.log);
                        }
                        else{

                            //ejecuta  cancela pedido
                            try {
                                //CancelOrderTask(String.valueOf(motivo_seleccionado),String.valueOf(order));
                                Utilities.SetLog("in cancel motivo", motivo_seleccionado, WSkeys.log);
                                Utilities.SetLog("in cancel pedido", String.valueOf(pos), WSkeys.log);
                                if(!String.valueOf(pos).equals("")) {
                                    CancelOrderTask(motivo_seleccionado, String.valueOf(pos));
                                }
                                else{
                                    Toast toast = Toast.makeText(getContext(),  "Espera a que se asigne tu pedido", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Utilities.SetLog("ejecuta cancela pedido", "", WSkeys.log);

                        }

                    }
                });

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheets", "Offset: " + slideOffset);
            }




            public void CancelOrderTask(final String motivo, final String order) throws JSONException {

                Map<String, String> params = new HashMap<String, String>();
                params.put(WSkeys.pedido, order);
                params.put(WSkeys.motivo, motivo);
                Log.e("PARAMETROSCANCEL_B", params.toString());


                String url = WSkeys.URL_BASE + WSkeys.URL_CANCELA+ "?"+WSkeys.pedido+"="+order+"&"+WSkeys.motivo+"="+motivo+"";
                Utilities.SetLog("CANCELA",url,WSkeys.log);

                RequestQueue queue = Volley.newRequestQueue(getContext());
                //JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
                StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            ParserCancela(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Utilities.SetLog("ERROR RESPONSE",error.toString(),WSkeys.log);
                        Snackbar.make(linearLayout, R.string.errorlistener, Snackbar.LENGTH_LONG)
                                .show();
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put(WSkeys.pedido, order);
                        params.put(WSkeys.motivo, motivo);
                        return new JSONObject(params).toString().getBytes();
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(WSkeys.pedido, order);
                        params.put(WSkeys.motivo, motivo);
                        Log.e("PARAMETROSCANCEL", params.toString());
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

            public void ParserCancela(String response) throws JSONException {

                Utilities.SetLog("PARSER-CANCELA",response.toString(),WSkeys.log);
                JSONObject response_object = new JSONObject(response);

                // si el response regresa ok, entonces si inicia la sesión
                if (response_object.getInt("codeError") == (WSkeys.okresponse)) {

                    Intent intent = new Intent(OrderDetailActivity.this, CancelaActivity.class);
                    intent.putExtra("cancel_result",response_object.getString("data"));
                    intent.putExtra("cause",motivo_texto);
                    intent.putExtra("order",json_order);
                    intent.putExtra("from","solicitado");

                    startActivity(intent);

                    finish();
                }
                // si ocurre un error al registrar la solicitud se muestra mensaje de error
                else{
                    Snackbar.make(linearLayout, response_object.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    //MAP
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceCurrentLocation();
        }
    }


    private void getDeviceCurrentLocation() {


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            Location getCurrentLocation = (Location) task.getResult();
                            moveCameratoCurrentLocation(WSkeys.CAMERA_ZOOM, new LatLng(latOrderAddress, lonOrderAddress));
                            AddMarker(latOrderAddress, lonOrderAddress,pedidoData.getNombreStatus(),pedidoData.getDireccion());

                        } else {
                            Utilities.SetLog("MAP-LOCATION", task.toString(), WSkeys.log);

                        }
                    }
                });

            }
        } catch (SecurityException e) {
            Utilities.SetLog("MAP", e.getMessage(), WSkeys.log);
        }
    }

    public void AddMarker(Double lat, Double lon, String conductor, String concesionario){
        mMap.addMarker(new MarkerOptions()
                .icon(Utilities.bitmapDescriptorFromVector(OrderDetailActivity.this, R.drawable.ic_home_blue_24dp))
                .position(new LatLng(lat,lon))
                .title(concesionario)
                .snippet(conductor))
                .showInfoWindow();
    }

    private void moveCameratoCurrentLocation(float zoom, LatLng ltln) {
        Utilities.SetLog("MAP_CAMERA", ltln.latitude + " " + ltln.longitude, WSkeys.log);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltln, zoom));

    }

    private void initMyMap() {
        //MAP
        //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync((new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gmMap) {
                mMap = gmMap;

                if (mLocationPermissionGranted) {
                    getDeviceCurrentLocation();
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                    //mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setZoomGesturesEnabled(true);
                    //mMap.getUiSettings().isMapToolbarEnabled();
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                }

            }
        }));
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
                initMyMap();
            } else {
                ActivityCompat.requestPermissions(OrderDetailActivity.this, permissions, REQUEST_LOCATION_PERMISSION);
            }
        }
        else{
            ActivityCompat.requestPermissions(OrderDetailActivity.this, permissions, REQUEST_LOCATION_PERMISSION);
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
                    initMyMap();
                }
            }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void LlenaPedido(){


        String url = WSkeys.URL_BASE + WSkeys.URL_CONSULTA_PEDIDO_ID+pos;
        Utilities.SetLog("LLENAPEDIDOs",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserPedido(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(mIdView, R.string.errorlistener, Snackbar.LENGTH_LONG)
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

    public void ParserPedido(String response) throws JSONException {

        Utilities.SetLog("RESPONSE_PEDIDOLIST",response,WSkeys.log);
        //Log.e("CodeResponse", response);
        Gson gson = new Gson();
        JSONObject respuesta = new JSONObject(response);

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)) {
            //ontener nivel de data
            //Utilities.SetLog("RESPONSEASENTAMIENTOS",data,WSkeys.log);
            //JSONArray ja_usocfdi = respuesta.getJSONArray(WSkeys.data);
            pedidoData= gson.fromJson( respuesta.getJSONObject(WSkeys.data).toString() , PedidoDetail.class);
            //setvalues to textviews

            mDate.setText(pedidoData.getFechaSolicitada());
            mTime.setText(pedidoData.getHoraSolicitada());
            mCantidad.setText(String.valueOf(pedidoData.getCantidad()));
            mIdView.setText(String.valueOf(pedidoData.getMonto()));
            mFormaPago.setText(pedidoData.getFormaPago());
            mlbl1.setText(String.format("%s %s", pedidoData.getAlias(), pedidoData.getDireccion()));
            mlbl2.setText(pedidoData.getNombreConcesionario());
            mlbl3.setText(pedidoData.getNombreConductor());
            mlbl4.setText(pedidoData.getPlaca());
            latOrderAddress = pedidoData.getLatitud();
            lonOrderAddress = pedidoData.getLongitud();

            if (latOrderAddress==null){
                latOrderAddress=0.0;
            }
            if (lonOrderAddress==null){
                lonOrderAddress=0.0;
            }

            if (pedidoData.getStatus()!=0){
                aclarar.setVisibility(View.VISIBLE);
                facturar.setVisibility(View.VISIBLE);
            }else{
                cancelar.setVisibility(View.VISIBLE);
            }
            //map
            getMyLocationPermision();

        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(mIdView, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                    .show();

            Utilities.SetLog("ERRORPARSER",response,WSkeys.log);
        }
    }

    public void FacturaPedido(Integer pos){


        String url = WSkeys.URL_BASE + WSkeys.URL_FACTURA+pos;
        Utilities.SetLog("PIDE FACTURA",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserFactura(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(mIdView, R.string.errorlistener, Snackbar.LENGTH_LONG)
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

    public void ParserFactura(String response) throws JSONException {

        Utilities.SetLog("RESPONSE_Factura",response,WSkeys.log);
        //Log.e("CodeResponse", response);
        Gson gson = new Gson();
        JSONObject respuesta = new JSONObject(response);

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)) {
            //ontener nivel de data
            //Utilities.SetLog("RESPONSEASENTAMIENTOS",data,WSkeys.log);
            //JSONArray ja_usocfdi = respuesta.getJSONArray(WSkeys.data);
            Snackbar.make(mIdView, respuesta.getString(WSkeys.data), Snackbar.LENGTH_LONG)
                    .show();

        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(mIdView, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                    .show();

            Utilities.SetLog("ERRORPARSER",response,WSkeys.log);
        }
    }


    public void AclararPedido(Integer pos){
        Intent intent = new Intent(OrderDetailActivity.this, Aclaracion.class);
        intent.putExtra("order",pos);
        startActivity(intent);
    }


    public void LlenaMotivos(final Spinner motivos){


        String url = WSkeys.URL_BASE + WSkeys.URL_MOTIVO_CANCELA;
        Utilities.SetLog("LLENA motivo CANCELA",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserMotivos(response, motivos);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(linearLayout, R.string.errorlistener, Snackbar.LENGTH_LONG)
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

    public void ParserMotivos(String response, Spinner motivos) throws JSONException {

        Utilities.SetLog("RESPONSE_MOTIVOS",response,WSkeys.log);
        //Log.e("CodeResponse", response);


        JSONObject respuesta = new JSONObject(response);
        Integer posselected =0;

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)) {
            //ontener nivel de data
            //Utilities.SetLog("RESPONSEASENTAMIENTOS",data,WSkeys.log);
            JSONArray ja_usocfdi = respuesta.getJSONArray(WSkeys.data);
            Utilities.SetLog("MOTIVOSARRAY",ja_usocfdi.toString(),WSkeys.log);
            for(int i=0; i<ja_usocfdi.length(); i++){
                MotivoCancela motivoCancela = new MotivoCancela();
                try {

                    JSONObject jsonObject = (JSONObject) ja_usocfdi.get(i);
                    motivoCancela.setId(jsonObject.getInt("id"));
                    motivoCancela.setText(jsonObject.getString("text"));
                    motivoAux.add(motivoCancela);
                    motivoArray.add(jsonObject.getString("text"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            motivos.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,motivoArray));
            motivos.setSelection(posselected);
        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(linearLayout, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                    .show();
        }
    }
}
