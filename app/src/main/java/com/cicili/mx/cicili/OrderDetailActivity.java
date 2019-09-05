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
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.cicili.mx.cicili.domain.Client.getContext;

public class OrderDetailActivity extends AppCompatActivity implements  OnMapReadyCallback{

    TextView mIdView;
    TextView mContentView;
    TextView mDate;
    TextView mTime;
    TextView mCantidad;
    TextView mFormaPago;
    TextView mlbl1,mlbl2,mlbl3,mlbl4;
    MapFragment mapFragment;

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

        mIdView = (TextView) findViewById(R.id.item_number);
        mContentView = (TextView) findViewById(R.id.content);
        mDate = (TextView) findViewById(R.id.date);
        mTime = (TextView) findViewById(R.id.time);
        mCantidad = (TextView) findViewById(R.id.cantidad);
        mFormaPago = (TextView) findViewById(R.id.formaPago);
        mlbl1 = (TextView) findViewById(R.id.lbl1);
        mlbl2 = (TextView) findViewById(R.id.lbl2);
        mlbl3 = (TextView) findViewById(R.id.lbl3);
        mlbl4 = (TextView) findViewById(R.id.lbl4);

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
                            AddMarker(latOrderAddress, lonOrderAddress,client.getPedidosDataArrayList().get(pos).getNombreStatus(),client.getPedidosDataArrayList().get(pos).getDireccion());

                        } else {
                            Utilities.SetLog("MAP-LOcATION", task.toString(), WSkeys.log);

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
                Snackbar.make(mIdView, R.string.errorlistener, Snackbar.LENGTH_SHORT)
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
            PedidoDetail pedidoData= gson.fromJson( respuesta.getJSONObject(WSkeys.data).toString() , PedidoDetail.class);
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
            //map
            getMyLocationPermision();

        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(mIdView, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();

            Utilities.SetLog("ERRORPARSER",response,WSkeys.log);
        }
    }

}
