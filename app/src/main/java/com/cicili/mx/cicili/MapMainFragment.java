package com.cicili.mx.cicili;

import android.Manifest;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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
import com.android.volley.toolbox.Volley;
import com.cicili.mx.cicili.domain.AddressData;
import com.cicili.mx.cicili.domain.AutotanquesCercanos;
import com.cicili.mx.cicili.domain.AutotanquesDisponibles;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.Pedido;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapMainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapMainFragment extends Fragment implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Integer pipaSeleccionada;

    private OnFragmentInteractionListener mListener;
    Application application = (Application) Client.getContext();
    Client client = (Client) application;
    Spinner direcciones;
    private ArrayList<String> direccionArray;
    private ArrayList<AddressData> direccionAux;
    private ArrayList<String> autotanquesDisponiblesArray;
    Gson gson = new Gson();
    Integer direccionSeleccionada=0;
    Double latitudPedido, longitudPedido;
    LinearLayout bottom_sheet;
    BottomSheetBehavior bsb;
    TextView name_usuario;
    LinearLayout featuredlayout;
    LinearLayout bottom_sheetmascercano;
    BottomSheetBehavior bsb_mascercano;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Boolean mLocationPermissionGranted = false;
    ArrayList<AutotanquesCercanos> autotanquesCercanosAux = new ArrayList<AutotanquesCercanos>();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    View view;

    public MapMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapMainFragment newInstance(String param1, String param2) {
        MapMainFragment fragment = new MapMainFragment();
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
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map_main, container, false);

        name_usuario = view.findViewById(R.id.name_usuario);
        name_usuario.setText(client.getName());
        direcciones = (Spinner) view.findViewById(R.id.spinner1);
        LlenaDirecciones(direcciones);

        direcciones.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        //map
        getMyLocationPermision();


        //pedido mascercano

        bottom_sheetmascercano = (LinearLayout)view.findViewById(R.id.bottomSheetCercano);
        bsb_mascercano = BottomSheetBehavior.from(bottom_sheetmascercano);
        bsb_mascercano.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                String nuevoEstado = "";
                final RadioGroup rgFormaPago = (RadioGroup) view.findViewById(R.id.rgFormaPago_mc);
                rgFormaPago.check(R.id.tarjeta_mc);
                String formapagoseleccionada="";
                final TextInputEditText litros = (TextInputEditText) view.findViewById(R.id.litros_mc);
                final TextInputEditText precio = (TextInputEditText) view.findViewById(R.id.precio_mc);

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

                Log.i("BottomSheetsMASCERCANA", "Nuevo estado: " + nuevoEstado);


                if(rgFormaPago.getCheckedRadioButtonId() == R.id.tarjeta_mc){
                    formapagoseleccionada = WSkeys.dtarjeta;
                }

                if (rgFormaPago.getCheckedRadioButtonId() == R.id.efectivo_mc){
                    formapagoseleccionada = WSkeys.defectivo;
                }


                final String finalFormapagoseleccionada = formapagoseleccionada;
                Button btnConformaPedido = (Button) view.findViewById(R.id.btnConfirmaMasCercano);
                btnConformaPedido.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean cancel = false;
                        View focusView = null;
                        String error="";

                        // Check for a valid l/p, if the user entered one.
                        if (TextUtils.isEmpty(litros.getText()) && TextUtils.isEmpty(precio.getText())) {
                            Utilities.SetLog("EMPTY PRECIO_o_LITROS", String.valueOf(precio.getText()), WSkeys.log);
                            // litros.setError(getString(R.string.error_invalid_value));
                            // focusView = litros;
                            error=getString(R.string.error_invalid_value);
                            cancel = true;
                        }

                        // Check for a valid password, if the user entered one.
                        if (String.valueOf(litros.getText()).equals("0") || String.valueOf(precio.getText()).equals("0")) {
                            Utilities.SetLog("CEROS PRECIO_o_LITROS", String.valueOf(precio.getText()), WSkeys.log);
                            error=getString(R.string.error_invalid_value);
                            cancel = true;
                        }

                        if (finalFormapagoseleccionada.equals("")){
                            //focusView = rgFormaPago;
                            error="Indica la forma de pago";
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

                            //ejecuta pedido
                            Pedido pedido = new Pedido();
                            NumberFormat nf = NumberFormat.getInstance();
                            Double cantidad = Double.valueOf(0);
                            Double monto = Double.valueOf(0);
                            try {
                                cantidad  = nf.parse(litros.getText().toString()).doubleValue();
                                monto = nf.parse(precio.getText().toString()).doubleValue();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Utilities.SetLog("MAS CERCANO PEDIR", finalFormapagoseleccionada, WSkeys.log);
                            AddressData addressData = new AddressData();
                            addressData.setId(direccionSeleccionada);
                            pedido.setCantidad(cantidad);
                            pedido.setMonto(monto);
                            pedido.setDomicilio(addressData);
                            pedido.setLatitud(latitudPedido);
                            pedido.setLongitud(longitudPedido);
                            pedido.setFormaPago(finalFormapagoseleccionada);
                            pedido.setIdAutotanque(0);
                            Intent intent = new Intent(getActivity(), NewOrderActivity.class);
                            Gson gson = new Gson();
                            String json_pedido = gson.toJson(pedido);
                            intent.putExtra("json_order",json_pedido);
                            startActivity(intent);
                            Utilities.SetLog("MO_MC-JSONORDER", json_pedido, WSkeys.log);

                        }

                    }
                });

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheets", "Offset: " + slideOffset);
            }


        });

        featuredlayout = (LinearLayout) view.findViewById(R.id.featuredlayout);
        featuredlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.SetLog("DIRECCIONSELECCIONADA", String.valueOf(direccionSeleccionada), WSkeys.log);
                if(direccionSeleccionada>0){
                    bsb_mascercano.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else{
                    Utilities.SetLog("no_DIRECCIONSELECCIONADA", String.valueOf(direccionSeleccionada), WSkeys.log);

                    Snackbar.make(direcciones, R.string.error_noaddressselected, Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
        //endmascercano

        //bottomsheet pedido

        bottom_sheet = (LinearLayout)view.findViewById(R.id.bottomSheet);
        bsb = BottomSheetBehavior.from(bottom_sheet);
        bsb.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                String nuevoEstado = "";
                TextView concesionario = (TextView) view.findViewById(R.id.concesionario);
                TextView conductor = (TextView) view.findViewById(R.id.conductor);
                TextView costoxlitro = (TextView) view.findViewById(R.id.costo);
                TextView tiempo = (TextView) view.findViewById(R.id.tiempo);
                final RadioGroup rgFormaPago = (RadioGroup) view.findViewById(R.id.rgFormaPago);
                rgFormaPago.check(R.id.tarjeta);
                String formapagoseleccionada="";
                final TextInputEditText litros = (TextInputEditText) view.findViewById(R.id.litros);
                final TextInputEditText precio = (TextInputEditText) view.findViewById(R.id.precio);

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



                concesionario.setText(client.getAutotanquesCercanosArrayList().get(pipaSeleccionada).getConcesionario());
                conductor.setText(client.getAutotanquesCercanosArrayList().get(pipaSeleccionada).getConductor());
                costoxlitro.setText(String.valueOf(client.getAutotanquesCercanosArrayList().get(pipaSeleccionada).getPrecio()));
                tiempo.setText(String.valueOf(client.getAutotanquesCercanosArrayList().get(pipaSeleccionada).getTiempoLlegada()));
                Utilities.SetLog("MO_PS-BS_PIPA", String.valueOf(pipaSeleccionada), WSkeys.log);
                Utilities.SetLog("MO_PS-BS_AUT", String.valueOf(client.getAutotanquesCercanosArrayList().get(pipaSeleccionada).getId()), WSkeys.log);


                if(rgFormaPago.getCheckedRadioButtonId() == R.id.tarjeta){
                    formapagoseleccionada = WSkeys.dtarjeta;
                }

                if (rgFormaPago.getCheckedRadioButtonId() == R.id.efectivo){
                    formapagoseleccionada = WSkeys.defectivo;
                }



                litros.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        Double nuevoprecio;
                        if (!TextUtils.isEmpty(litros.getText())) {
                            if (Double.valueOf(litros.getText().toString()) > 0) {
                                nuevoprecio = client.getAutotanquesCercanosArrayList().get(pipaSeleccionada).getPrecio() * Double.valueOf(litros.getText().toString());
                                precio.setText(String.valueOf(nuevoprecio));
                            }
                        }
                        else{
                            litros.setText("0");
                        }
                    }
                });

                precio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        Double calculaLitros;
                        if (!TextUtils.isEmpty(precio.getText())){
                            if (Double.valueOf(precio.getText().toString())>0){
                                calculaLitros = (Double.valueOf(precio.getText().toString()) / client.getAutotanquesCercanosArrayList().get(pipaSeleccionada).getPrecio());
                                litros.setText(String.valueOf(calculaLitros));
                            }
                        }
                        else{
                            precio.setText("0");
                        }
                    }
                });

                Button btnConformaPedido = (Button) view.findViewById(R.id.btnConfirma);
                final String finalFormapagoseleccionada = formapagoseleccionada;
                btnConformaPedido.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean cancel = false;
                        View focusView = null;
                        String error="";

                        // Check for a valid l/p, if the user entered one.
                        if (TextUtils.isEmpty(litros.getText()) || String.valueOf(litros.getText()).equals("0")) {
                           // litros.setError(getString(R.string.error_invalid_value));
                           // focusView = litros;
                            error=getString(R.string.error_invalid_value);
                            cancel = true;
                        }

                        // Check for a valid password, if the user entered one.
                        if (TextUtils.isEmpty(precio.getText()) || String.valueOf(precio.getText()).equals("0")) {
                            Utilities.SetLog("ERROR PRECIO", String.valueOf(precio.getText()), WSkeys.log);
                            //precio.setError(getString(R.string.error_invalid_value));
                            //focusView = precio;
                            error=getString(R.string.error_invalid_value);
                            cancel = true;
                        }

                        if (finalFormapagoseleccionada.equals("")){
                            //focusView = rgFormaPago;
                            error="Indica la forma de pago";
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

                            //ejecuta pedido
                            Pedido pedido = new Pedido();
                            NumberFormat nf = NumberFormat.getInstance();
                            Double cantidad = Double.valueOf(0);
                            Double monto = Double.valueOf(0);
                            try {
                                 cantidad  = nf.parse(litros.getText().toString()).doubleValue();
                                 monto = nf.parse(precio.getText().toString()).doubleValue();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }




                            AddressData addressData = new AddressData();
                            addressData.setId(direccionSeleccionada);
                            pedido.setCantidad(cantidad);
                            pedido.setMonto(monto);
                            pedido.setDomicilio(addressData);
                            pedido.setLatitud(latitudPedido);
                            pedido.setLongitud(longitudPedido);
                            pedido.setFormaPago(finalFormapagoseleccionada);
                            pedido.setIdAutotanque(client.getAutotanquesCercanosArrayList().get(pipaSeleccionada).getId());
                            Intent intent = new Intent(getActivity(), NewOrderActivity.class);
                            Gson gson = new Gson();
                            String json_pedido = gson.toJson(pedido);
                            intent.putExtra("json_order",json_pedido);
                            startActivity(intent);
                            Utilities.SetLog("MO_PS-JSONORDER", json_pedido, WSkeys.log);
                            Utilities.SetLog("MO_PS-JSONORDER_PIPA", String.valueOf(pipaSeleccionada), WSkeys.log);
                            Utilities.SetLog("MO_PS-JSONORDER_AUT", String.valueOf(client.getAutotanquesCercanosArrayList().get(pipaSeleccionada).getId()), WSkeys.log);

                        }

                    }
                });

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheets", "Offset: " + slideOffset);
            }


        });

        return view;
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
        Snackbar.make(direcciones, R.string.gettingDeviceLocation, Snackbar.LENGTH_SHORT)
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
                            try {
                               ConsultaPrincipal(new LatLng(getCurrentLocation.getLatitude(), getCurrentLocation.getLongitude()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utilities.SetLog("MAP-LOcATION", task.toString(), WSkeys.log);
                            Snackbar.make(direcciones, R.string.failedgetlocation, Snackbar.LENGTH_SHORT)
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

    private void initMyMap() {
        //MAP
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync((new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gmMap) {
                mMap = gmMap;

                if (mLocationPermissionGranted) {
                    getDeviceCurrentLocation();
                    if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
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
                    mMap.setMyLocationEnabled(true);
                    //mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.getUiSettings().setZoomGesturesEnabled(true);
                    mMap.getUiSettings().isMapToolbarEnabled();
                    mMap.getUiSettings().setAllGesturesEnabled(true);

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            marker.getTag();

                           Integer selectedItem = (Integer) marker.getTag();
                            Utilities.SetLog("MAP SELECTED PIPA",String.valueOf(marker.getTag()),WSkeys.log);
                            pipaSeleccionada = ((Integer) marker.getTag()).intValue();

                            bsb.setState(BottomSheetBehavior.STATE_EXPANDED);

                            // OrderIntentFragment orderIntentFragment = new OrderIntentFragment();
                            //orderIntentFragment = OrderIntentFragment.newInstance(selectedItem,"");
                            //orderIntentFragment.setCancelable(false);
                            //orderIntentFragment.show(getFragmentManager(),"fragmenOrderIntent");
                            return false;
                        }
                    });

                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            marker.getTag();

                            Integer selectedItem = (Integer) marker.getTag();
                            Utilities.SetLog("MAP SELECTED PIPA",String.valueOf(marker.getTag()),WSkeys.log);
                            pipaSeleccionada = ((Integer) marker.getTag()).intValue();

                            bsb.setState(BottomSheetBehavior.STATE_EXPANDED);

                            // OrderIntentFragment orderIntentFragment = new OrderIntentFragment();
                            //orderIntentFragment = OrderIntentFragment.newInstance(selectedItem,"");
                            //orderIntentFragment.setCancelable(false);
                            //orderIntentFragment.show(getFragmentManager(),"fragmenOrderIntent");
                        }
                    });

                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                        @Override
                        public View getInfoWindow(Marker arg0) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {

                            LinearLayout info = new LinearLayout(getContext());
                            info.setOrientation(LinearLayout.VERTICAL);

                            TextView title = new TextView(getContext());
                            title.setTextColor(Color.BLACK);
                            title.setGravity(Gravity.CENTER);
                            title.setTypeface(null, Typeface.BOLD);
                            title.setText(marker.getTitle());

                            TextView snippet = new TextView(getContext());
                            snippet.setTextColor(Color.GRAY);
                            snippet.setText(marker.getSnippet());

                            info.addView(title);
                            info.addView(snippet);

                            return info;
                        }
                    });
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
                    initMyMap();
                }
            }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
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

    public void LlenaDirecciones(final Spinner direcciones){

        direccionArray = new ArrayList<String>();

        if (client.getAddressDataArrayList() != null) {

            direccionArray.add("Selecciona una dirección");
            for (int i = 0; i < client.getAddressDataArrayList().size(); i++) {
                direccionArray.add(client.getAddressDataArrayList().get(i).getAlias());
            }
            direcciones.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, direccionArray));
        }
    }

    public void ConsultaPrincipal(final LatLng ltln) throws JSONException {

        String url = WSkeys.URL_BASE + WSkeys.URL_MAINSEARCH+"?"+WSkeys.latitude+"="+ltln.latitude+"&"+WSkeys.longitude+"="+ltln.longitude;
        Utilities.SetLog("MAINSEARCH",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ParserMain(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utilities.SetLog("ERROR RESPONSE",error.toString(),WSkeys.log);
                Snackbar.make(direcciones, R.string.errorlistener, Snackbar.LENGTH_SHORT)
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
                params.put(WSkeys.latitude, String.valueOf(ltln.latitude));
                params.put(WSkeys.longitude, String.valueOf(ltln.longitude));
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

    public void ParserMain(JSONObject response) throws JSONException {

        Utilities.SetLog("PARSER-MAIN",response.toString(),WSkeys.log);

        // si el response regresa ok, entonces si inicia la sesión
        if (response.getInt("codeError") == (WSkeys.okresponse)) {
            JSONArray ja_data = response.getJSONArray(WSkeys.data);
            //Utilities.SetLog("ASENTAMIENTOSARRAY",ja_direcciones.toString(),WSkeys.log);
            for(int i=0; i<ja_data.length(); i++){
                AddressData direccion = new AddressData();
                try {
                    JSONObject jo_data = (JSONObject) ja_data.get(i);
                    Utilities.SetLog("DATA ARRAY",jo_data.toString(),WSkeys.log);
                    //Utilities.SetLog("JO_CONCESIONARIO",jo_data.getJSONObject(WSkeys.concesionario).toString(),WSkeys.log);
                    //Utilities.SetLog("JO_PERFILCONDUCTOR",jo_data.getJSONObject(WSkeys.perfilconductor).toString(),WSkeys.log);
                    //Utilities.SetLog("JO_PERFILCONDUCTOR_CND",jo_data.getJSONObject(WSkeys.perfilconductor).getJSONObject(WSkeys.conductor).toString(),WSkeys.log);
                    //Utilities.SetLog("JO_AUTOTANQUE",jo_data.getJSONObject(WSkeys.autotanque).toString(),WSkeys.log);
                    Gson gson = new Gson();
                    AutotanquesCercanos autotanquesData= gson.fromJson(jo_data.toString() , AutotanquesCercanos.class);
                    autotanquesCercanosAux.add(autotanquesData);

                    //autotanquesDisponiblesAux = new ArrayList<AutotanquesDisponibles>();
                    //AutotanquesDisponibles autotanquesDisponibles= gson.fromJson(jo_data.toString() , AutotanquesDisponibles.class);
                    //autotanquesDisponiblesAux.add(autotanquesDisponibles);
                    //AddMarker(autotanquesDisponiblesAux.get(i).getAutotanque().getLatitud(),autotanquesDisponiblesAux.get(i).getAutotanque().getLongitud(),autotanquesDisponiblesAux.get(i).getPerfilConductor().getConductor().getNombre(),autotanquesDisponiblesAux.get(i).getConcecionario().getNombre());
                    //AddMarker(jo_data.getJSONObject(WSkeys.autotanque).getDouble("latitud"),jo_data.getJSONObject(WSkeys.autotanque).getDouble("longitud"),jo_data.getJSONObject(WSkeys.perfilconductor).getJSONObject(WSkeys.conductor).getString("nombre"),jo_data.getJSONObject(WSkeys.concesionario).getString("nombre"), jo_data.getDouble("precio"), i);
                    AddMarker(jo_data.getDouble("latitud"),jo_data.getDouble("longitud"),jo_data.getString("conductor"),jo_data.getString("concesionario"), jo_data.getDouble("precio"),jo_data.getString("tiempoLlegada"), i);
                    //jo_data.getJSONObject(WSkeys.concesionario);
                    //jo_data.getJSONObject(WSkeys.conductor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            client.setAutotanquesCercanosArrayList(autotanquesCercanosAux);

            //direcciones.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,direccionArray));

        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(direcciones, response.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


        if (i!=0) {
            //client.getAddressDataArrayList().get(i).getLatitud();
            //client.getAddressDataArrayList().get(i).getLongitud();
            Log.e("onItemSelected",String.valueOf(i));
            Log.e("Selected--idaddress",String.valueOf(client.getAddressDataArrayList().get(i-1).getId()));
            Log.e("Selected--alias",client.getAddressDataArrayList().get(i-1).getAlias());
            try {
                //mMap.clear();
                ConsultaPrincipal(new LatLng(client.getAddressDataArrayList().get(i-1).getLatitud(), client.getAddressDataArrayList().get(i-1).getLongitud()));
                MoveCameraSelectedDirection(client.getAddressDataArrayList().get(i-1).getLatitud(), client.getAddressDataArrayList().get(i-1).getLongitud(),client.getAddressDataArrayList().get(i-1).getAlias());
                direccionSeleccionada = client.getAddressDataArrayList().get(i-1).getId();
                latitudPedido = client.getAddressDataArrayList().get(i-1).getLatitud();
                longitudPedido= client.getAddressDataArrayList().get(i-1).getLongitud();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void AddMarker(Double lat, Double lon, String conductor, String concesionario,Double precio, String tiempo, Integer id){
        Marker mMarker = mMap.addMarker(new MarkerOptions()
                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_pipa_1))
                .position(new LatLng(lat,lon))
                .title("Concesionario: "+concesionario)
                .snippet("Conductor: " + conductor + "\n" + "Precio: $"+ String.valueOf(precio) + "\n" + "Tiempo de Llegada: "+ tiempo));

        mMarker.showInfoWindow();
        mMarker.setTag(id);
    }

    public void MoveCameraSelectedDirection(Double lat, Double lon, String alias){
        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(lat,lon))
                .zoom(10)
                .bearing(0)
                .tilt(45)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);

        mMap.addMarker(new MarkerOptions()
                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_home_blue_24dp))
                .position(new LatLng(lat,lon))
                .title("Dirección")
                .snippet(alias))
        .showInfoWindow();



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


/* mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.clear(); //clear old markers

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(37.4219999,-122.0862462))
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.4629101,-122.2449094))
                        .title("Iron Man")
                        .snippet("His Talent : Plenty of money"));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.3092293,-122.1136845))
                        .title("Captain America"));

            }





        });*/