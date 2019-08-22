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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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
 * {@link RfcDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RfcDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RfcDetailFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private  String LOG = "RFC DETAIL FRAGMENT";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  Integer pos;

    private OnFragmentInteractionListener mListener;

    //widgets
    TextView street,town,state,cp, rfc, razonsocial, usocfdi;

    Application application = (Application) Client.getContext();
    Client client = (Client) application;
    View view;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    public RfcDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RfcDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RfcDetailFragment newInstance(String param1, String param2) {
        RfcDetailFragment fragment = new RfcDetailFragment();
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
        view = inflater.inflate(R.layout.fragment_rfc_detail, container, false);


        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        /////make map clear
        //getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        //getDialog().setContentView(R.layout.validate_geolocation_layout);////your custom content

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getDialog().getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        getDialog().getWindow().setAttributes(lp);
        //Find the textviews objects
        //street = (TextView) view.findViewById(R.id.street);
        rfc = (TextView) view.findViewById(R.id.rfc);
        //cp = (TextView) view.findViewById(R.id.cp);
        //town = (TextView) view.findViewById(R.id.town);
        razonsocial = (TextView) view.findViewById(R.id.razonsocial);
        usocfdi = (TextView) view.findViewById(R.id.usocfdi);

        Utilities.SetLog(LOG,mParam1,WSkeys.log);
        pos = Integer.parseInt(mParam1);
        Utilities.SetLog(LOG+ "pos",String.valueOf(pos),WSkeys.log);


        //Utilities.SetLog(LOG,client.getRfcDataArrayList().get(pos).getCalle(),WSkeys.log);
        //Utilities.SetLog(LOG,String.valueOf(client.getRfcDataArrayList().get(pos).getExterior()),WSkeys.log);
        //Utilities.SetLog(LOG,String.valueOf(client.getRfcDataArrayList().get(pos).getInterior()),WSkeys.log);
        Utilities.SetLog(LOG,client.getRfcDataArrayList().get(pos).getRfc(),WSkeys.log);
        Utilities.SetLog(LOG,client.getRfcDataArrayList().get(pos).getRazonSocial(),WSkeys.log);
        Utilities.SetLog(LOG,String.valueOf(client.getRfcDataArrayList().get(pos).getUsoCfdi().getId()),WSkeys.log);
        //Utilities.SetLog(LOG,String.valueOf(client.getRfcDataArrayList().get(pos).getAsentamiento().getCp()),WSkeys.log);
        //Utilities.SetLog(LOG,client.getRfcDataArrayList().get(pos).getAsentamiento().getNombre(),WSkeys.log);

        //street.setText(client.getRfcDataArrayList().get(pos).getCalle() + "," + String.valueOf(client.getRfcDataArrayList().get(pos).getExterior()) + " " + String.valueOf(client.getRfcDataArrayList().get(pos).getInterior()));
        rfc.setText(client.getRfcDataArrayList().get(pos).getRfc());
        razonsocial.setText(client.getRfcDataArrayList().get(pos).getRazonSocial());
        usocfdi.setText(String.valueOf(client.getRfcDataArrayList().get(pos).getUsoCfdi().getText()));
        //cp.setText(String.valueOf(client.getRfcDataArrayList().get(pos).getAsentamiento().getCp()));
        //town.setText(client.getRfcDataArrayList().get(pos).getAsentamiento().getNombre());

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent intent = new Intent(getContext(), PerfilData.class);
                intent.putExtra("active",WSkeys.datos_rfc);
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


    @Override
    public void onResume() {
        super.onResume();

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog,
                                 int keyCode, android.view.KeyEvent event) {
                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    // To dismiss the fragment when the back-button is pressed.
                    Fragment prev = getFragmentManager().findFragmentByTag("fragmentRfcDetail");
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

    }

}
