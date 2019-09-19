package com.cicili.mx.cicili;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.cicili.mx.cicili.domain.Client.getContext;

public class PedidoAceptadoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private Double latOrderAddress, lonOrderAddress;

    /***** Ejecutar tarea cada 5 segundos < **/
    Handler handler = new Handler();
    private final int TIEMPO = 5000;
    Double iLat = 0.00, iLon=0.00;
    private Marker mMarkerConductor=null;
    /***** > Ejecutar tarea cada 5 segundos **/

    MapFragment mapFragment;
    TextView vista;
    Application application = (Application) getContext();
    Client client = (Client) application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_aceptado);

        vista = (TextView) findViewById(R.id.name);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_ubicacion_detail);

        latOrderAddress = 0.0;
        lonOrderAddress = 0.0;
        //map
        getMyLocationPermision();
    }


    //*********  Ejecutar tarea cada 5 mins **************
    public void ejecutarTarea() {
        handler.postDelayed(new Runnable() {
            public void run() {


                // función a ejecutar
                //actualizarChofer(); // función para refrescar la ubicación del conductor, creada en otra línea de código
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
                try {

                    Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                //iLat=iLat+0.0001399;
                                //iLon=iLon+0.0001223;
                                Location getCurrentLocation = (Location) task.getResult();
                                Utilities.SetLog("Ubicacion: ", Double.toString(getCurrentLocation.getLatitude()) + " , " + Double.toString(getCurrentLocation.getLongitude()), WSkeys.log);

                                //ActualizarUbicacionTask(getCurrentLocation.getLatitude()+iLat,getCurrentLocation.getLongitude()-iLon);
                                ActualizarUbicacionTask(getCurrentLocation.getLatitude(),getCurrentLocation.getLongitude());

                            } else {
                                Utilities.SetLog("MAP-LOcATION", task.toString(), WSkeys.log);
                                Snackbar.make(vista, R.string.failedgetlocation, Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });


                } catch (SecurityException e) {
                    Utilities.SetLog("MAP", e.getMessage(), WSkeys.log);
                }



                handler.postDelayed(this, TIEMPO);
            }

        }, TIEMPO);

    }

    private void ActualizarUbicacionTask(final Double latitud, final Double longitud){
        moveCameratoCurrentLocation(WSkeys.CAMERA_ZOOM, new LatLng(latitud, longitud));
        AddMarkerConductor(latitud,longitud,"AGS","Pipas el pipo", 100.00,"2", 1);
    }


    public void ObtenerUbicacionConductorTask() {
        handler.postDelayed(new Runnable() {
            public void run() {

                // función a ejecutar
                try{
                    ubicacionConductor(); // función para refrescar la ubicación del conductor, creada en otra línea de código
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                handler.postDelayed(this, TIEMPO);
            }

        }, TIEMPO);

    }


    public void ubicacionConductor() throws JSONException{


        String url = WSkeys.URL_BASE + WSkeys.URL_UBICACION_CONDUCTOR + "127";//+ client.getOrder_id();

        RequestQueue queue = Volley.newRequestQueue(client.getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    UbicacionParserData(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(vista, R.string.errorlistener, Snackbar.LENGTH_SHORT)
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

    public void UbicacionParserData(JSONObject response) throws JSONException {
        //showProgress(false);
        JSONObject respuesta = response;

        Utilities.SetLog("ubicacion ", respuesta.toString(), WSkeys.log);
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)){
            JSONObject dataUbicacion = respuesta.getJSONObject(WSkeys.data);
            //JSONArray jousuario = respuesta.getJSONArray(WSkeys.data);

            //iLat=iLat+0.0001399;
            //iLon=iLon+0.0001223;
            //ActualizarUbicacionTask(dataUbicacion.getDouble("latitud")+iLat,dataUbicacion.getDouble("longitud")-iLon);
            ActualizarUbicacionTask(dataUbicacion.getDouble("latitud"),dataUbicacion.getDouble("longitud"));
            Snackbar.make(vista, "ubicación recibida", Snackbar.LENGTH_SHORT).show();


        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Utilities.SetLog("ERROR api ubicación: ",respuesta.getString(WSkeys.messageError),WSkeys.log);

            Snackbar.make(vista, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

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
                ActivityCompat.requestPermissions(PedidoAceptadoActivity.this, permissions, REQUEST_LOCATION_PERMISSION);
            }
        }
        else{
            ActivityCompat.requestPermissions(PedidoAceptadoActivity.this, permissions, REQUEST_LOCATION_PERMISSION);
        }
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

                //ejecutarTarea();
                ObtenerUbicacionConductorTask();

            }
        }));
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
                            latOrderAddress = getCurrentLocation.getLatitude();
                            lonOrderAddress = getCurrentLocation.getLongitude();
                            moveCameratoCurrentLocation(WSkeys.CAMERA_ZOOM, new LatLng(latOrderAddress, lonOrderAddress));
                            AddMarker(latOrderAddress, lonOrderAddress,"estatus","direccion");

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
    private void moveCameratoCurrentLocation(float zoom, LatLng ltln) {
        Utilities.SetLog("MAP_CAMERA", ltln.latitude + " " + ltln.longitude, WSkeys.log);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltln, zoom));

    }

    public void AddMarker(Double lat, Double lon, String conductor, String concesionario){
        mMap.addMarker(new MarkerOptions()
                .icon(Utilities.bitmapDescriptorFromVector(PedidoAceptadoActivity.this, R.drawable.ic_home_blue_24dp))
                .position(new LatLng(lat,lon))
                .title(concesionario)
                .snippet(conductor))
                .showInfoWindow();
    }

    public void AddMarkerConductor(Double lat, Double lon, String conductor, String concesionario,Double precio, String tiempo, Integer id){
        if (mMarkerConductor != null) {
            mMarkerConductor.remove();
        }
        Utilities.SetLog("pipa : ", lat + " " + lon, WSkeys.log);
        mMarkerConductor = mMap.addMarker(new MarkerOptions()
                .icon(bitmapDescriptorFromVector(PedidoAceptadoActivity.this, R.drawable.ic_pipa_1))
                .position(new LatLng(lat,lon))
                .title("Concesionario: "+concesionario)
                .snippet("Conductor: " + conductor + "\n" + "Precio: $"+ String.valueOf(precio) + "\n" + "Tiempo de Llegada: "+ tiempo));

        mMarkerConductor.showInfoWindow();
        mMarkerConductor.setTag(id);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
