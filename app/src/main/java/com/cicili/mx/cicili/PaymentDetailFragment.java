package com.cicili.mx.cicili;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.PaymentData;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaymentDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PaymentDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentDetailFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Application application = (Application) Client.getContext();
    Client client = (Client) application;

    private OnFragmentInteractionListener mListener;

    View view;
    private Spinner spinner;
    private Adapter adapter;
    private TextView tipocta, tipotrj;
    private TextView titular,numero,vencimiento, cvv,pais;
    private String LOG = "PaymentDetail";
    private  Integer pos;


    public PaymentDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentDetailFragment newInstance(String param1, String param2) {
        PaymentDetailFragment fragment = new PaymentDetailFragment();
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
        view = inflater.inflate(R.layout.fragment_payment_detail, container, false);


        //Find the textviews objects
        titular = (TextView) view.findViewById(R.id.titular);
        tipocta = (TextView) view.findViewById(R.id.tipocta);
        tipotrj = (TextView) view.findViewById(R.id.tipotrj);
        cvv = (TextView) view.findViewById(R.id.cvv);
        numero = (TextView) view.findViewById(R.id.numt);
        vencimiento = (TextView) view.findViewById(R.id.vencimiento);
        pais = (TextView) view.findViewById(R.id.pais);

        Utilities.SetLog(LOG,mParam1, WSkeys.log);
        pos = Integer.parseInt(mParam1);
        Utilities.SetLog(LOG+ "pos", String.valueOf(pos), WSkeys.log);


        //Utilities.SetLog(LOG,client.getPaymentDataArrayList().get(pos).getNombreTitular(),WSkeys.log);
        Utilities.SetLog(LOG,String.valueOf(client.getPaymentDataArrayList().get(pos).getTipoTarjeta()),WSkeys.log);
        Utilities.SetLog(LOG,String.valueOf(client.getPaymentDataArrayList().get(pos).getTipoPago()),WSkeys.log);
        Utilities.SetLog(LOG,String.valueOf(client.getPaymentDataArrayList().get(pos).getCvv()),WSkeys.log);
        Utilities.SetLog(LOG,client.getPaymentDataArrayList().get(pos).getVencimiento(),WSkeys.log);
        Utilities.SetLog(LOG,String.valueOf(client.getPaymentDataArrayList().get(pos).getNumero()),WSkeys.log);
        //Utilities.SetLog(LOG,String.valueOf(client.getPaymentDataArrayList().get(pos).getPais()),WSkeys.log);

        //titular.setText(client.getPaymentDataArrayList().get(pos).getNombreTitular());
        if (client.getPaymentDataArrayList().get(pos).getTipoPago().equals(WSkeys.TDD)){
            tipocta.setText(R.string.tdd);
        }else if (client.getPaymentDataArrayList().get(pos).getTipoPago().equals(WSkeys.TDC)){
            tipocta.setText(R.string.TDC);
        }
        //tipocta.setText(String.valueOf(client.getPaymentDataArrayList().get(pos).getTipoPago()));
        //tipotrj.setText(String.valueOf(client.getPaymentDataArrayList().get(pos).getTipoTarjeta()));
        if (client.getPaymentDataArrayList().get(pos).getTipoTarjeta()!=null) {
            if (client.getPaymentDataArrayList().get(pos).getTipoTarjeta().equals(WSkeys.amex)) {
                tipotrj.setText(R.string.amx);
            } else if (client.getPaymentDataArrayList().get(pos).getTipoTarjeta().equals(WSkeys.mc)) {
                tipotrj.setText(R.string.mc);
            } else if (client.getPaymentDataArrayList().get(pos).getTipoTarjeta().equals(WSkeys.visa)) {
                tipotrj.setText(R.string.visa);
            }
        }
        cvv.setText(String.valueOf(String.valueOf(client.getPaymentDataArrayList().get(pos).getCvv())));
        numero.setText(String.valueOf(client.getPaymentDataArrayList().get(pos).getNumero()));
        vencimiento.setText(client.getPaymentDataArrayList().get(pos).getVencimiento());
        //pais.setText(String.valueOf(client.getPaymentDataArrayList().get(pos).getPais()));


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent intent = new Intent(getContext(), PerfilData.class);
                intent.putExtra("active",WSkeys.datos_pago);
                intent.putExtra("id",mParam1);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getDialog().dismiss();
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
            public boolean onKey(android.content.DialogInterface dialog,
                                 int keyCode, android.view.KeyEvent event) {
                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    // To dismiss the fragment when the back-button is pressed.
                    Fragment prev = getFragmentManager().findFragmentByTag("fragmentPaymentDetail");
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
        //SupportMapFragment f = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        //if (f != null)
        //    getFragmentManager().beginTransaction().remove(f).commit();
    }
}
