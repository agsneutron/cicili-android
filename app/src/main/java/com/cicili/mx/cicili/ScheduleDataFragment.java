package com.cicili.mx.cicili;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cicili.mx.cicili.domain.AddressData;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.Programa;
import com.cicili.mx.cicili.domain.RfcData;
import com.cicili.mx.cicili.domain.UsoCfdi;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleDataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleDataFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Integer pos;

    ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    private String LOG = "SCHEDULE_DATA_FRAGMENT";
    View view;
    Spinner spinnerSchedule;
    EditText dateSchedule;
    EditText timeSchedule;
    TextInputEditText input;
    Double cantidad, monto;
    AppCompatButton buttonSchedule;
    TextInputLayout labelinput;
    RadioGroup rgMontoLitro;
    RadioGroup rgFormaPago;
    String formapagoseleccionada="";
    Application application = (Application) Client.getContext();
    Client client = (Client) application;
    private ArrayList<String> direccionArray;
    
    //values to getDate / getTime
    Calendar c = Calendar.getInstance();
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);
    private static final String CERO = "0";
    private static final String BARRA = "-";
    private static final String DOS_PUNTOS = ":";

    private Integer selectedAddress=0;

    public ScheduleDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RfcDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleDataFragment newInstance(String param1, String param2) {
        ScheduleDataFragment fragment = new ScheduleDataFragment();
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
            mParam1=bundle.getString("ARG_PARAM1");
            pos = Integer.parseInt(mParam1);
            Utilities.SetLog(LOG,mParam1,WSkeys.log);
            Utilities.SetLog(LOG+ "pos",String.valueOf(pos),WSkeys.log);
            Utilities.SetLog(LOG+"ARGS",bundle.getString("ARG_PARAM1"),WSkeys.log);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        view = inflater.inflate(R.layout.schedule_data_fragment, container, false);

        spinnerSchedule = (Spinner) view.findViewById(R.id.spinnerSchedule);
        spinnerSchedule.requestFocus();
        //calle = view.findViewById(R.id.calle);
        //numext = view.findViewById(R.id.numext);
        //numint = view.findViewById(R.id.numint);
        /*favorito = view.findViewById(R.id.favorita);
        favorito.setChecked(false);*/
        dateSchedule = (EditText) view.findViewById(R.id.fecha);
        dateSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(dateSchedule);
            }
        });
        timeSchedule = (EditText) view.findViewById(R.id.hora);
        timeSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTime(timeSchedule);
            }
        });

        rgFormaPago = (RadioGroup) view.findViewById(R.id.rgFormaPago);
        rgFormaPago.check(R.id.tarjeta);


        input = (TextInputEditText) view.findViewById(R.id.input);
        labelinput = (TextInputLayout)view.findViewById(R.id.labelinput);

        rgMontoLitro = (RadioGroup) view.findViewById(R.id.rgMontoLitro);
        rgMontoLitro.check(R.id.monto);
        rgMontoLitro.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId() == R.id.litro){
                    labelinput.setHint("Litros");
                }

                if (radioGroup.getCheckedRadioButtonId() == R.id.monto){
                    labelinput.setHint("Monto");
                }
            }
        });


        buttonSchedule = view.findViewById(R.id.schedulebutton);
        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View focusView = null;
                Boolean error =  false;

                if(rgFormaPago.getCheckedRadioButtonId() == R.id.tarjeta){
                    formapagoseleccionada = WSkeys.dtarjeta;
                }

                if (rgFormaPago.getCheckedRadioButtonId() == R.id.efectivo){
                    formapagoseleccionada = WSkeys.defectivo;
                }

                if (input.getText().toString().equals("")){
                    Snackbar.make(view, "Indica los litros o monto de tu pedido", Snackbar.LENGTH_LONG)
                            .show();
                    focusView = input;
                    error=true;
                } else if (input.getText().toString().equals("0")){
                    Snackbar.make(view, R.string.error_invalid_ammount, Snackbar.LENGTH_LONG)
                            .show();
                    focusView = input;
                    error=true;
                }else if (TextUtils.isDigitsOnly(input.getText())){
                    if (Double.parseDouble(input.getText().toString())<200 && rgMontoLitro.getCheckedRadioButtonId()== R.id.monto){
                        Snackbar.make(view, R.string.error_invalid_ammount, Snackbar.LENGTH_LONG)
                                .show();
                        focusView = input;
                        error=true;
                    }
                }

                if(spinnerSchedule.getSelectedItemId()==0){
                    Snackbar.make(view, "Selecciona una dirección para enviar tu pedido ", Snackbar.LENGTH_LONG)
                            .show();
                    error=true;
                }
                if (dateSchedule.getText().equals("")){
                    dateSchedule.setError(getString(R.string.error_field_required));
                    error=true;
                    focusView = dateSchedule;
                }
                if (timeSchedule.getText().equals("")){
                    timeSchedule.setError(getString(R.string.error_field_required));
                    error=true;
                    focusView = timeSchedule;
                }

                if (!error){
                    Programa schedule = new Programa();
                    if(rgMontoLitro.getCheckedRadioButtonId() == R.id.litro){
                        //litros in input
                        if (input.getText().toString().equals("")){
                            cantidad = 0.0;
                        }else {
                            cantidad = Double.parseDouble(input.getText().toString());
                        }
                        monto=0.0;
                    }

                    if (rgMontoLitro.getCheckedRadioButtonId() == R.id.monto){
                       //monto in input
                        if (input.getText().toString().equals("")){
                            cantidad = 0.0;
                        }else {
                            monto = Double.parseDouble(input.getText().toString());
                        }

                        cantidad= 0.0;
                    }

                    AddressData addressData = new AddressData();
                    addressData.setId(selectedAddress);
                    schedule.setCantidad(cantidad);
                    schedule.setMonto(monto);
                    schedule.setDomicilio(addressData);
                    schedule.setFechaSolicitada(dateSchedule.getText().toString());
                    schedule.setHoraSolicitada(timeSchedule.getText().toString());
                    schedule.setFormaPago(formapagoseleccionada);

                    schedule.setLatitud(0.0);
                    schedule.setLongitud(0.0);


                    try {
                        progressDialog = ProgressDialog.show(getContext(), "Espera un momento por favor", "Estamos programando tu pedido.", true);

                        ScheduleDataTask(schedule);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });



        /*cp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if((Utilities.isFieldValid(cp)) && cp.length()>= WSkeys.cplenght){
                    LlenaColonia(cp.getText().toString());
                }
                else {
                    Snackbar.make(cp, "Introduce un Código Postal para mostrar las colonias.", Snackbar.LENGTH_LONG)
                                .show();
                }

            }
        });*/

        // Spinner click listener
        spinnerSchedule.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();

        categories.add("Selecciona una dirección");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerSchedule.setAdapter(dataAdapter);

        LlenaDirecciones(spinnerSchedule);

        //If is called the fragment to edit data
        /*if (pos != null) {
            rfc.setText(client.getRfcDataArrayList().get(pos).getRfc());
           *//* cp.setText(String.valueOf(client.getRfcDataArrayList().get(pos).getAsentamiento().getCp()));
            calle.setText(client.getRfcDataArrayList().get(pos).getCalle());
            numext.setText(client.getRfcDataArrayList().get(pos).getExterior());
            numint.setText(client.getRfcDataArrayList().get(pos).getInterior());
            selectedtown= client.getRfcDataArrayList().get(pos).getAsentamiento().getId();*//*
            selectedcfdi= client.getRfcDataArrayList().get(pos).getUsoCfdi().getId();
            Utilities.SetLog(LOG+"town",String.valueOf(selectedcfdi),WSkeys.log);
            LlenaCFDI();
            razonsocial.setText(client.getRfcDataArrayList().get(pos).getRazonSocial());
            button.setText("Actualizar");
        }*/
        // Inflate the layout for this fragment
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        Log.e("onItemSelected",String.valueOf(i));
        if (i!=0) {
            selectedAddress = client.getAddressDataArrayList().get(i-1).getId();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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


    public void LlenaDirecciones(final Spinner direcciones){

        direccionArray = new ArrayList<String>();

        direccionArray.add("Selecciona una dirección");
        if (client.getAddressDataArrayList() != null) {

            for (int i = 0; i < client.getAddressDataArrayList().size(); i++) {
                direccionArray.add(client.getAddressDataArrayList().get(i).getAlias());
            }
            direcciones.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, direccionArray));
        }
    }


    public void ScheduleDataTask(Programa scheduleData) throws JSONException {

        Gson gson = new Gson();
        String json;
        String url;
        JSONObject params;


        //to_shedule
        json = gson.toJson(scheduleData);
        params = new JSONObject(json);
        Log.e("ScheduleValuePairs--", json);
        url = WSkeys.URL_BASE + WSkeys.URL_PROGRAMA;

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ParserScheduleAnswer(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(view, R.string.errorlistener, Snackbar.LENGTH_SHORT)
                        .show();
                progressDialog.dismiss();
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

    public void ParserScheduleAnswer(JSONObject respuesta) throws JSONException {

        Utilities.SetLog("ParserSchedule",respuesta.toString(),WSkeys.log);
        progressDialog.dismiss();

        // si el response regresa ok, entonces si se programó el pedido
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)){

            String data = respuesta.getString(WSkeys.data);
            JSONObject jo_sheduled = respuesta.getJSONObject(WSkeys.data);

            Intent intent = new Intent(getActivity(), NewScheduledOrderActivity.class);
            Gson gson = new Gson();
            String json_pedido = gson.toJson(jo_sheduled);
            intent.putExtra("json_order",json_pedido);
            startActivity(intent);

            /*Snackbar.make(view, R.string.successschedulevalidation, Snackbar.LENGTH_LONG)
                    .show();
            Intent intent = new Intent(getContext(),MenuActivity.class);
            startActivity(intent);
            getActivity().finish();*/

        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(view, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }

    }

    private void getTime(final EditText time_picked){
        TimePickerDialog recogerHora = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog,new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Formateo el hora obtenido: antepone el 0 si son menores de 10
                String horaFormateada =  (hourOfDay < 10)? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                String minutoFormateado = (minute < 10)? String.valueOf(CERO + minute):String.valueOf(minute);
                //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario
                String AM_PM;
                if(hourOfDay < 12) {
                    AM_PM = "a.m.";
                } else {
                    AM_PM = "p.m.";
                }
                AM_PM ="";  //por el formato requerido eliminamos AM PM
                // hora con el formato deseado
                //time_picked.setText(String.format("%s%s%s %s", horaFormateada, DOS_PUNTOS, minutoFormateado, AM_PM)
                time_picked.setText(String.format("%s%s%s%s", horaFormateada, DOS_PUNTOS, minutoFormateado, AM_PM));

            }
            //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
        }   , hora, minuto, false);

        recogerHora.setTitle("Indica la hora de entrega.");
        recogerHora.show();
    }


    private void getDate(final EditText date_picked){
        DatePickerDialog recogerFecha = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                date_picked.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);


            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        },anio, mes, dia);
        //show widget
        Date newDate = c.getTime();
        recogerFecha.getDatePicker().setMinDate(newDate.getTime()-(newDate.getTime()%(24*60*60*1000)));
        recogerFecha.show();

    }
}
