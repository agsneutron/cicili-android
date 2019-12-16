package com.cicili.mx.cicili;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddressDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddressDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddressDetailFragment extends DialogFragment implements OnMapReadyCallback{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private  String LOG = "ADDRESS DETAIL FRAGMENT";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  Integer pos;

    private OnFragmentInteractionListener mListener;

    //widgets
    TextView street,alias,town,state,cp;

    Application application = (Application) Client.getContext();
    Client client = (Client) application;
    View view;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private Double latCurrentAddress, lonCurrentAdress;
    SupportMapFragment mapFragment;


    public AddressDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddressDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddressDetailFragment newInstance(String param1, String param2) {
        AddressDetailFragment fragment = new AddressDetailFragment();
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
            Utilities.SetLog(LOG,mParam1,WSkeys.log);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_address_detail, container, false);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        /////make map clear
        //getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        //getDialog().setContentView(R.layout.validate_geolocation_layout);////your custom content

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();


        lp.copyFrom(getDialog().getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        getDialog().getWindow().setAttributes(lp);

        //Find the textviews objects
        street = (TextView) view.findViewById(R.id.street);
        alias = (TextView) view.findViewById(R.id.alias);
        cp = (TextView) view.findViewById(R.id.cp);
        town = (TextView) view.findViewById(R.id.town);

        Utilities.SetLog(LOG,mParam1,WSkeys.log);
        pos = Integer.parseInt(mParam1);
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
        mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map_FAD);

        //map
        getMyLocationPermision();


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent intent = new Intent(getContext(), PerfilData.class);
                intent.putExtra("active",WSkeys.datos_direccion);
                intent.putExtra("id",mParam1);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getDialog().dismiss();


                //getActivity().getFragmentManager().beginTransaction().remove().commit();

            }
        });

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
                .icon(Utilities.bitmapDescriptorFromVector(getActivity(), R.drawable.ic_home_blue_24dp))
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

    @Override
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
    }

}
