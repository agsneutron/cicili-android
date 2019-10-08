package com.cicili.mx.cicili;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
import com.cicili.mx.cicili.directionhelpers.FetchURL;
import com.cicili.mx.cicili.directionhelpers.TaskLoadedCallback;
import com.cicili.mx.cicili.domain.AddressData;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.Pedido;
import com.cicili.mx.cicili.domain.SeguimientoPedido;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cicili.mx.cicili.domain.Client.getContext;

public class PedidoAceptadoActivity extends AppCompatActivity implements OnMapReadyCallback , TaskLoadedCallback {

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

    private Polyline currentPolyline;

    MapFragment mapFragment;
    TextView vista;
    Application application = (Application) getContext();
    Client client = (Client) application;

    /** PEDIDO DATA **/
    String pedido_data="";
    TextView monto;
    TextView lbl1,lbl2,lbl3,lbl4;
    TextView time;
    Gson gson = new Gson();
    SeguimientoPedido seguimientoPedido = client.getSeguimientoPedido();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_aceptado);
        String nombreEstatus="";
        String status="";

        vista = (TextView) findViewById(R.id.name);
        monto = (TextView) findViewById(R.id.cantidad);
        time = (TextView) findViewById(R.id.time);
        lbl1 = (TextView) findViewById(R.id.lbl1);
        lbl2 = (TextView) findViewById(R.id.lbl2);
        lbl3 = (TextView) findViewById(R.id.lbl3);
        lbl4 = (TextView) findViewById(R.id.lbl4);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_ubicacion_detail);

        mapFragment.getMapAsync(this);

        latOrderAddress = 0.0;
        lonOrderAddress = 0.0;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            //recuperar datos de pedido
            pedido_data = bundle.getString("pedido_data");
            status = bundle.getString("status");
            Utilities.SetLog("PEDIDO ACEPTADO DATA",pedido_data, WSkeys.log);


            //seguimientoPedido= gson.fromJson(pedido_data , SeguimientoPedido.class);
            //seguimientoPedido= client.getSeguimientoPedido();

            monto.setText(seguimientoPedido.getMonto());
            latOrderAddress = Double.parseDouble(seguimientoPedido.getLatitud());
            lonOrderAddress = Double.parseDouble(seguimientoPedido.getLongitud());
            time.setText(seguimientoPedido.getTiempo());
            lbl1.setText(seguimientoPedido.getConductor());
            lbl2.setText(seguimientoPedido.getColor());
            lbl3.setText(seguimientoPedido.getPlaca());

            if (seguimientoPedido.getTipo().toString().equals("3")) {
                switch (Integer.parseInt(status)) {
                    case 1:
                        nombreEstatus = "Solicitado";
                        break;
                    case 2:
                        nombreEstatus = "Aceptado";
                        break;
                    case 3:
                        nombreEstatus = "En Camino";
                        break;
                    case 4:
                        nombreEstatus = "Preparando Carga";
                        break;
                    case 5:
                        nombreEstatus = "Cargando";
                        break;
                    case 6:
                        nombreEstatus = "Cargado";
                        break;
                    case 7:
                        nombreEstatus = "Pagado";
                        break;
                    case 8:
                        nombreEstatus = "Programado";
                        break;
                    case 9:
                        nombreEstatus = "Cancelado";
                        break;
                    default:
                        nombreEstatus = "Facturado";
                        break;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // Add the buttons
                builder.setMessage("Pedido : " + nombreEstatus);
                builder.setPositiveButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button

                        Intent intent = new Intent(PedidoAceptadoActivity.this, PedidoAceptadoActivity.class);
                        startActivity(intent);
                    }
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();

                dialog.show();
            }
        }
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
        AddMarkerConductor(latitud,longitud,seguimientoPedido.getConductor(),seguimientoPedido.getConcesionario(), Double.parseDouble(seguimientoPedido.getMonto()),seguimientoPedido.getTiempo(), Integer.parseInt(seguimientoPedido.getIdPedido()));
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


        String url = WSkeys.URL_BASE + WSkeys.URL_UBICACION_CONDUCTOR + client.getOrder_id();

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

            /*if (iLat == 0) {
                iLat = dataUbicacion.getDouble("latitud");
                iLon = dataUbicacion.getDouble("longitud");
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(iLat, iLon), new LatLng(latOrderAddress, lonOrderAddress))
                        .width(5)
                        .color(Color.RED));
            }*/
            iLat = dataUbicacion.getDouble("latitud");
            iLon = dataUbicacion.getDouble("longitud");

            String respuestaDirecctions = new FetchURL(PedidoAceptadoActivity.this).execute(getUrl(new LatLng(iLat, iLon), new LatLng(latOrderAddress, lonOrderAddress), "driving"), "driving").toString();

            Utilities.SetLog("DATA Directions: ",respuestaDirecctions,WSkeys.log);

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

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {

        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onTaskDoneData(String data) {
        Utilities.SetLog("result onTaskDoneData: ", data.toString(), WSkeys.log);
    }



}
