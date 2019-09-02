package com.cicili.mx.cicili;

import android.Manifest;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.cicili.mx.cicili.domain.Client;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.TextView;

import static com.cicili.mx.cicili.domain.Client.getContext;

public class AddressDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    //widgets
    TextView street,alias,town,state,cp;

    private  String LOG = "ADDRESS DETAIL FRAGMENT";
    Application application = (Application) getContext();
    Client client = (Client) application;


    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private Double latCurrentAddress, lonCurrentAdress;
    MapFragment mapFragment;
    private  Integer pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Find the textviews objects
        street = (TextView) findViewById(R.id.street);
        alias = (TextView) findViewById(R.id.alias);
        cp = (TextView) findViewById(R.id.cp);
        town = (TextView) findViewById(R.id.town);

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

        //Utilities.SetLog(LOG,mParam1, WSkeys.log);
        //pos = Integer.parseInt(mParam1);
        Utilities.SetLog(LOG+ "pos",String.valueOf(pos),WSkeys.log);

        Utilities.SetLog(LOG,String.valueOf(client.getAddressDataArrayList().get(pos).getId()),WSkeys.log);
        Utilities.SetLog(LOG,client.getAddressDataArrayList().get(pos).getCalle(),WSkeys.log);
        Utilities.SetLog(LOG,String.valueOf(client.getAddressDataArrayList().get(pos).getExterior()),WSkeys.log);
        Utilities.SetLog(LOG,String.valueOf(client.getAddressDataArrayList().get(pos).getInterior()),WSkeys.log);
        Utilities.SetLog(LOG,client.getAddressDataArrayList().get(pos).getAlias(),WSkeys.log);
        Utilities.SetLog(LOG,String.valueOf(client.getAddressDataArrayList().get(pos).getAsentamiento().getCp()),WSkeys.log);
        Utilities.SetLog(LOG,client.getAddressDataArrayList().get(pos).getAsentamiento().getNombre(),WSkeys.log);

        street.setText(String.format("%s,%s %s", client.getAddressDataArrayList().get(pos).getCalle(), String.valueOf(client.getAddressDataArrayList().get(pos).getExterior()), String.valueOf(client.getAddressDataArrayList().get(pos).getInterior())));
        alias.setText(client.getAddressDataArrayList().get(pos).getAlias());
        cp.setText(String.valueOf(client.getAddressDataArrayList().get(pos).getAsentamiento().getCp()));
        town.setText(client.getAddressDataArrayList().get(pos).getAsentamiento().getNombre());

        latCurrentAddress = client.getAddressDataArrayList().get(pos).getLatitud();
        lonCurrentAdress = client.getAddressDataArrayList().get(pos).getLongitud();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_FAD);

        //map
        getMyLocationPermision();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent intent = new Intent(getContext(), PerfilData.class);
                intent.putExtra("active",WSkeys.datos_direccion);
                intent.putExtra("id",pos);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //AddressDetailActivity.this.finish();



                //getActivity().getFragmentManager().beginTransaction().remove().commit();

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
                            moveCameratoCurrentLocation(WSkeys.CAMERA_ZOOM, new LatLng(latCurrentAddress, lonCurrentAdress));
                            AddMarker(latCurrentAddress, lonCurrentAdress,alias.getText().toString(),street.getText().toString());

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
                .icon(Utilities.bitmapDescriptorFromVector(AddressDetailActivity.this, R.drawable.ic_home_blue_24dp))
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
                ActivityCompat.requestPermissions(AddressDetailActivity.this, permissions, REQUEST_LOCATION_PERMISSION);
            }
        }
        else{
            ActivityCompat.requestPermissions(AddressDetailActivity.this, permissions, REQUEST_LOCATION_PERMISSION);
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

    /*@Override
    public void onResume() {
        super.onResume();

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog,
                                 int keyCode, android.view.KeyEvent event) {
                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    // To dismiss the fragment when the back-button is pressed.
                    Fragment prev = getFragmentManager().findFragmentByTag("fragmenAddressDetail");
                    if (prev != null) {
                        DialogFragment df = (DialogFragment) prev;
                        df.dismiss();
                    }

                    return true;
                }
                // Otherwise, do nothing else
                else return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SupportMapFragment f = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map_FAD);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }*/


}
