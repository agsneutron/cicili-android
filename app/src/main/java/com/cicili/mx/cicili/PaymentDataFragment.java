package com.cicili.mx.cicili;

import android.app.Application;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.cicili.mx.cicili.domain.Paises;
import com.cicili.mx.cicili.domain.PaymentData;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaymentDataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PaymentDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentDataFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Integer pos;
    Application application = (Application) Client.getContext();
    Client client = (Client) application;

    private OnFragmentInteractionListener mListener;

    ViewGroup viewGroup;
    View view;
    //private Spinner spinner;
    private Adapter adapter;
    private RadioGroup forma;
    private RadioButton uno, dos, tres; // visa, masterc,amex;
    private TextInputEditText titular,numero, vencimiento, cvv, tipotarjeta, banco;
    private Button registra_pago;
    private LinearLayoutCompat vtarjeta;
    private Boolean error =false;
    private Integer paisselected=0;
    private ArrayList<String> paisArray;
    private ArrayList<Paises> paisAux;
    private String LOG = "PAYMENTDATAFRAGMENT";



    public PaymentDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentDataFragment newInstance(String param1, String param2) {
        PaymentDataFragment fragment = new PaymentDataFragment();
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
                mParam1 = bundle.getString("ARG_PARAM1");
                pos = Integer.parseInt(mParam1);

                Utilities.SetLog(LOG, mParam1, WSkeys.log);
                Utilities.SetLog(LOG + "pos", String.valueOf(pos), WSkeys.log);
            }
            else{
                pos=null;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        view = inflater.inflate(R.layout.payment_data_fragment, container, false);
        viewGroup=container;
        //spinner = (Spinner) view.findViewById(R.id.spinner1);

        // Spinner click listener
        //spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        //LlenaPaises("p");
        // Spinner Drop down elements
        //List<String> categories = new ArrayList<String>();

        //categories.add("Paises");

        // Creating adapter for spinner
        //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        //spinner.setAdapter(dataAdapter);
        //tipo = (RadioGroup) view.findViewById(R.id.rgTipo);
        forma = (RadioGroup) view.findViewById(R.id.rgForma);
        vtarjeta = (LinearLayoutCompat) view.findViewById(R.id.viewTarjeta);
        /*forma.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (forma.getCheckedRadioButtonId() == R.id.uno){
                    vtarjeta.setVisibility(GONE);
                    Utilities.SetLog("Forma Selected",String.valueOf(forma.getCheckedRadioButtonId()), WSkeys.log);
                }
                else if ((forma.getCheckedRadioButtonId() == R.id.dos) || (forma.getCheckedRadioButtonId() == R.id.dos)){
                    vtarjeta.setVisibility(View.VISIBLE);
                    Utilities.SetLog("Forma Selected",String.valueOf(forma.getCheckedRadioButtonId()), WSkeys.log);

                }
            }
        });*/
        //titular = view.findViewById(R.id.titular);
        numero = view.findViewById(R.id.tarjeta);
        vencimiento = view.findViewById(R.id.vencimiento);
        tipotarjeta = view.findViewById(R.id.tipotarjeta);
        banco = view.findViewById(R.id.banco);

        cvv = view.findViewById(R.id.cvv);
        //uno = view.findViewById(R.id.uno);
        dos = view.findViewById(R.id.dos);
        tres = view.findViewById(R.id.tres);
        //visa = view.findViewById(R.id.visa);
        //masterc = view.findViewById(R.id.mc);
        //amex = view.findViewById(R.id.amx);
        registra_pago = (Button) view.findViewById(R.id.register_payment);




        //If is called the fragment to edit data
        if (pos != null) {
            //titular.setText(client.getPaymentDataArrayList().get(pos).getNombreTitular());
            numero.setText(String.valueOf(client.getPaymentDataArrayList().get(pos).getNumero()));
            vencimiento.setText(client.getPaymentDataArrayList().get(pos).getVencimiento());
            cvv.setText(String.valueOf(client.getPaymentDataArrayList().get(pos).getCvv()));
            tipotarjeta.setText(client.getPaymentDataArrayList().get(pos).getTipoTarjeta());
            banco.setText(client.getPaymentDataArrayList().get(pos).getBanco());
            /*if (client.getPaymentDataArrayList().get(pos).getTipoPago().equals(WSkeys.efectivo)){
                //forma.check(R.id.uno);
            }else*/
                if (client.getPaymentDataArrayList().get(pos).getTipoPago().equals(WSkeys.TDD)){
                forma.check(R.id.dos);
            }else if (client.getPaymentDataArrayList().get(pos).getTipoPago().equals(WSkeys.TDC)){
                forma.check(R.id.tres);
            }
            /*if (client.getPaymentDataArrayList().get(pos).getTipoTarjeta()!=null) {
                if (client.getPaymentDataArrayList().get(pos).getTipoTarjeta().equals(WSkeys.amex)) {
                    tipo.check(R.id.amx);
                } else if (client.getPaymentDataArrayList().get(pos).getTipoTarjeta().equals(WSkeys.mc)) {
                    tipo.check(R.id.mc);
                } else if (client.getPaymentDataArrayList().get(pos).getTipoTarjeta().equals(WSkeys.visa)) {
                    tipo.check(R.id.visa);
                }
            }*/


            registra_pago.setText("ACTUALIZAR");
        }



        vencimiento.addTextChangedListener(dateExpireValidation);






        numero.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if((Utilities.isFieldValid(numero)) && numero.length()>=6){
                    LlenaBanco(numero.getText().toString().substring(0,6));
                }
                else {
                    Snackbar.make(view, "Introduce un número de tarjeta.", Snackbar.LENGTH_LONG)
                            .show();
                }

            }
        });

        registra_pago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utilities.hideKeyboardwithoutPopulate(getActivity());
                View focusView = null;
                Integer fp = 0;
                Integer tp = 0;
                PaymentData paymentData = new PaymentData();
                error = false;

                /*if (forma.getCheckedRadioButtonId() == R.id.uno){
                    fp = WSkeys.efectivo;
                    paymentData.setTipoPago(fp);
                    Utilities.SetLog("efectivo",fp.toString(),WSkeys.log);
                }
                else */if ((forma.getCheckedRadioButtonId() == R.id.dos) || (forma.getCheckedRadioButtonId() == R.id.tres)){

                    if (forma.getCheckedRadioButtonId() == R.id.dos) {
                        fp=WSkeys.TDD;
                        Utilities.SetLog("tdd ",fp.toString(),WSkeys.log);
                    }
                    if (forma.getCheckedRadioButtonId() == R.id.tres){
                        fp = WSkeys.TDC;
                        Utilities.SetLog("tdc ",fp.toString(),WSkeys.log);

                    }
                    paymentData.setTipoPago(fp);
                    //String sTitular = titular.getText()+"";
                    String sNumero = numero.getText()+"";
                    String sVencimiento = vencimiento.getText()+"";
                    String sCvv = cvv.getText()+"";
                    String sTipoTarjeta = tipotarjeta.getText()+"";
                    String sBanco = banco.getText()+"";

                    /*if (sTitular.isEmpty() || sTitular.length()<10){
                        titular.setError(getString(R.string.error_field_required));
                        error = true;
                        focusView = titular;
                        Utilities.SetLog("titular",sTitular,WSkeys.log);
                    }
                    else{
                        paymentData.setNombreTitular(sTitular);
                    }*/
                    if (sNumero.isEmpty() ||sNumero.length() < 8){
                        numero.setError(getString(R.string.error_field_required));
                        error =  true;
                        focusView = numero;
                        Utilities.SetLog("numero",sNumero,WSkeys.log);
                    }else{
                        paymentData.setNumero(Long.parseLong(sNumero));
                    }
                    if(sVencimiento.equals("") || sVencimiento.length() < 5 ){
                        vencimiento.setError(getString(R.string.error_field_required));
                        error =  true;
                        focusView = vencimiento;
                        Utilities.SetLog("vencimiento",sVencimiento,WSkeys.log);
                    }
                    else{
                    paymentData.setVencimiento(sVencimiento);
                    }
                    if(sCvv.equals("") || sCvv.length()<3){
                        cvv.setError(getString(R.string.error_field_helpcvv));
                        error =  true;
                        focusView = cvv;
                        Utilities.SetLog("cvv----<",sCvv,WSkeys.log);
                    }else{
                    paymentData.setCvv(Integer.parseInt(sCvv));
                    }

                    if (sTipoTarjeta.isEmpty() || sTipoTarjeta.equals("")){
                        tipotarjeta.setError(getString(R.string.vefifycardnumber));
                        error = true;
                        focusView = tipotarjeta;
                        Utilities.SetLog("Tipo de Tarjeta",sTipoTarjeta,WSkeys.log);
                    }
                    else{
                        paymentData.setTipoTarjeta(sTipoTarjeta);
                    }

                    if (sBanco.isEmpty() || sBanco.equals("")){
                        banco.setError(getString(R.string.vefifycardnumber));
                        error = true;
                        focusView = banco;
                        Utilities.SetLog("Banco",sBanco,WSkeys.log);
                    }
                    else{
                        paymentData.setBanco(sBanco);
                    }
                    /*if(tipo.getCheckedRadioButtonId() == R.id.visa){
                        tp = WSkeys.visa;
                    }

                    if (tipo.getCheckedRadioButtonId() == R.id.mc){
                        tp = WSkeys.mc;
                    }
                    if (tipo.getCheckedRadioButtonId() == R.id.amx){
                        tp = WSkeys.amex;
                    }
                    if (tp==0){
                        Snackbar.make(spinner, getString(R.string.tipopago), Snackbar.LENGTH_LONG)
                                .show();
                        error = true;
                        Utilities.SetLog("tipo",tp.toString(),WSkeys.log);
                    }else{
                        paymentData.setTipoTarjeta(tp);
                    }*/

                    //paymentData.setPais(paisselected);


                    if (error){
                        if (focusView != null){
                            focusView.requestFocus();
                            Utilities.SetLog("ERRORFOCUS",focusView.toString(),WSkeys.log);
                        }

                    }

                }
                else{
                    Snackbar.make(view, getString(R.string.formapago), Snackbar.LENGTH_LONG)
                            .show();
                    error= true;

                    Utilities.SetLog("ERRORELSE","ERROR FORMA",WSkeys.log);

                }
                try {
                    if (!error){
                    PaymentDataTask(paymentData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

   /* @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        // paisselected = parent.getItemAtPosition(position).toString();
        //paisselected=position;
        if (position!=0) {
            paisselected = paisAux.get(position).getId();
        }

        // Showing selected spinner item
        Snackbar.make(spinner, "Seleccionado: " + paisselected, Snackbar.LENGTH_LONG)
                .show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
*/
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

    /**
     * Represents an asynchronous registration task used to register
     * the user.
     */
    public void PaymentDataTask(PaymentData paymentData) throws JSONException {

        Gson gson = new Gson();
        String json;
        String url;
        JSONObject params;

        if(pos != null) {
            //toupdate
            paymentData.setId(client.getPaymentDataArrayList().get(pos).getId());
            json = gson.toJson(paymentData);
            params = new JSONObject(json);
            //Log.e("PaymentValuesUpdate--", json);
            url = WSkeys.URL_BASE + WSkeys.URL_PAYMENTUPPDATE;
        }else{
            //toadd
            json = gson.toJson(paymentData);
            params = new JSONObject(json);
            //Log.e("PaymentValuePairs--", json);
            url =  WSkeys.URL_BASE + WSkeys.URL_PAYMENTDATA;
        }

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ParserPayment(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Log.e("El error", error.toString());
                Snackbar.make(numero, R.string.errorlistener, Snackbar.LENGTH_LONG)
                        .show();
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
                //Log.e("PARAMETROS", params.toString());*/
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

    public void ParserPayment(JSONObject respuesta) throws JSONException {

        //Log.e("CodeResponse", respuesta.toString());


        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)){
            String data = respuesta.getString(WSkeys.data);
            Gson gson = new Gson();
            //JSONObject jopayment = respuesta.getJSONObject(WSkeys.data);
            String jsonPayment = respuesta.getString("data");
            if(pos!=null){
                Utilities.UpdatePaymentData(jsonPayment,client,pos);
                Toast toast = Toast.makeText(getContext(),  R.string.successpaymentupdate, Toast.LENGTH_LONG);
                toast.show();
                Intent intent = new Intent(getContext(),MenuActivity.class);
                //intent.putExtra("active", client.getStatus());
                //client.setStatus("C");
                intent.putExtra("active", client.getStatus());
                startActivity(intent);
                getActivity().finish();
            }else {
                Utilities.AddPaymentData(jsonPayment, client);
                Toast toast = Toast.makeText(getContext(),  R.string.successpaymentvalidation, Toast.LENGTH_LONG);
                toast.show();
                /*Intent intent = new Intent(getContext(),PerfilData.class);
                startActivity(intent);
                getActivity().finish();*/
                getActivity().getSupportFragmentManager().beginTransaction().hide(this).commit();
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();
                getActivity().onBackPressed();
            }


            /*Snackbar.make(viewGroup.getChildAt(0), R.string.successpaymentvalidation, Snackbar.LENGTH_LONG)
                    .show();*/

            /*if (client.getStatus().equals(WSkeys.completo)){
                Intent intent = new Intent(getContext(),MenuActivity.class);
                intent.putExtra("active", WSkeys.completo);
                startActivity(intent);
                getActivity().finish();
            }else if(client.getStatus().equals(WSkeys.datos_direccion)){
                Fragment fragmentAddress = new AddressDataFragment();
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                manager.getBackStackEntryCount();
                transaction.remove(this);
                transaction.show(fragmentAddress).commit();
            }*/


        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(view, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public abstract class TextValidator implements TextWatcher {
        private final TextInputEditText textView;

        public TextValidator(TextInputEditText textView) {
            this.textView = textView;
        }

        public abstract void validate(TextInputEditText textView, String text);

        @Override
        final public void afterTextChanged(Editable s) {
            String text = textView.getText().toString();
            validate(textView, text);
        }

        @Override
        final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

        @Override
        final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }
    }

    public void  LlenaBanco(String numero){
        String url = WSkeys.URL_BASE + WSkeys.URL_BANKSEARCH+numero;
        Utilities.SetLog("paymentfr llena banco", url, WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Utilities.SetLog("PAYMENT COUNTRIES RESPONSE",response,WSkeys.log);
                    ParserBank(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Log.e("El error", error.toString());
                Snackbar.make(view, R.string.errorlistener, Snackbar.LENGTH_LONG)
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
                ////Log.e("PARAMETROS", params.toString());
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

    public void ParserBank(String response) throws JSONException {

        ////Log.e("CodeResponse", response);
        JSONObject jo_respuesta = new JSONObject(response);
        Utilities.SetLog("RESPONSEbank",jo_respuesta.toString(),WSkeys.log);


        // si el response regresa ok, entonces si inicia la sesión
        if (jo_respuesta.getInt("codeError") == (WSkeys.okresponse)) {
            banco.setText(jo_respuesta.getJSONObject("data").getString("banco"));
            tipotarjeta.setText(jo_respuesta.getJSONObject("data").getString("tipoTarjeta"));
        }
        else{
            Snackbar.make(viewGroup.getChildAt(0), jo_respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_LONG)
                    .show();
        }
    }


    /*public void LlenaPaises(final String text) {


        String url = WSkeys.URL_BASE + WSkeys.URL_COUNTRIES;
        Utilities.SetLog("paymentfr llena pais", url, WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Utilities.SetLog("PAYMENT COUNTRIES RESPONSE",response,WSkeys.log);
                    ParserCountry(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Log.e("El error", error.toString());
                Snackbar.make(spinner, R.string.errorlistener, Snackbar.LENGTH_SHORT)
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
                ////Log.e("PARAMETROS", params.toString());
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

    }*/

    /*public void ParserCountry(String response) throws JSONException {

        ////Log.e("CodeResponse", response);
        paisAux = new ArrayList<Paises>();
        paisArray = new ArrayList<String>();
        JSONObject jo_respuesta = new JSONObject(response);
        Utilities.SetLog("RESPONSEcountry",jo_respuesta.toString(),WSkeys.log);


        // si el response regresa ok, entonces si inicia la sesión
        if (jo_respuesta.getInt("codeError") == (WSkeys.okresponse)) {
            //obtener nivel data
            Gson gson = new Gson();
            JSONArray ja_country = jo_respuesta.getJSONArray(WSkeys.data);
            for(int i=0; i<ja_country.length(); i++) {
                if(i==0){
                    Paises pais = new Paises();
                    pais.setId(0);
                    pais.setText("Selecciona Pais");
                    paisAux.add(pais);
                    paisArray.add("Selecciona Pais");
                }else {
                    JSONObject jo_address = (JSONObject) ja_country.get(i);
                    Utilities.SetLog("jo_country", jo_address.toString(), WSkeys.log);
                    Paises paises = gson.fromJson(jo_address.toString(), Paises.class);
                    paisAux.add(paises);
                    paisArray.add(paises.getText());
                }
            }

            spinner.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,paisArray));

            *//*for(int i=0; i<respuesta.length(); i++){
                Paises paises = new Paises();
                try {
                    if(i==0){
                        paises.setId(0);
                        paises.setText("Selecciona Pais");
                        paisAux.add(paises);
                        paisArray.add("Selecciona Pais");
                    }
                    JSONObject jsonObject = (JSONObject) respuesta.get(i);
                    paises.setId(jsonObject.getInt("id"));
                    paises.setText(jsonObject.getString("text"));
                    paisAux.add(paises);
                    paisArray.add(jsonObject.getString("text"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            spinner.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,paisArray));
*//*
        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(numero, "Ocurrio un error al cargar los paises", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }*/

    private final TextWatcher dateExpireValidation = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if ((Utilities.isFieldValid(vencimiento)) && vencimiento.length()==2) {
                //text = text.concat("/");
                vencimiento.setText(String.format("%s/", s));
                vencimiento.setSelection(vencimiento.getText().length());
                vencimiento.addTextChangedListener(this);
                //vencimiento.setError("Agrega /");
            }

        }
    };
}

