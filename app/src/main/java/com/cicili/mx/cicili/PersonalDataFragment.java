package com.cicili.mx.cicili;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
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
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.PersonalData;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonalDataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonalDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalDataFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    Application application = (Application) Client.getContext();
    Client client = (Client) application;
    private String mParam1;
    private String mParam2;
    TextInputEditText mname ;
    TextInputEditText mpat ;
    TextInputEditText mmat;
    EditText mnac;
    EditText ibCalendario;
    RadioGroup sexo;
    Button bRegister;
    View view;
    ViewGroup viewgroup;
    String ts ="";
    Button picture;
    private static final String CERO = "0";
    private static final String BARRA = "-";
    private static final int SETPHOTO = 100;


    //values to getDate
    Calendar c = Calendar.getInstance();
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    private OnFragmentInteractionListener mListener;

    public PersonalDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonalDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalDataFragment newInstance(String param1, String param2) {
        PersonalDataFragment fragment = new PersonalDataFragment();
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

        view = inflater.inflate(R.layout.personal_data_fragment, container, false);
        // Inflate the layout for this fragment
         mname = (TextInputEditText) view.findViewById(R.id.name);
         mpat = (TextInputEditText) view.findViewById(R.id.apppat);
         mmat = (TextInputEditText) view.findViewById(R.id.appmat);
         mnac = (EditText) view.findViewById(R.id.fecha);
         sexo = (RadioGroup) view.findViewById(R.id.rgSexo);
         picture = (Button) view.findViewById(R.id.picture);
         viewgroup = container;


        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SETPHOTO);
            }
        });

        ibCalendario = (EditText) view.findViewById(R.id.fecha);
        ibCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(mnac);
            }
        });

        bRegister = (Button) view.findViewById(R.id.register_personal);


        if (!client.getName().equals("")){
            mname.setText(client.getName());
            mpat.setText(client.getLastname());
            mmat.setText(client.getLastsname());
            mnac.setText(client.getDate());
            if (!client.getSexo().equals("")) {
                switch (client.getSexo()) {
                    case WSkeys.hombre:
                        sexo.check(R.id.hombre);
                        break;
                    case WSkeys.mujer:
                        sexo.check(R.id.mujer);
                        break;
                    case WSkeys.noindicar:
                        sexo.check(R.id.no_indicar);
                        break;
                }
            }
            bRegister.setText("Actualizar");
        }

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utilities.hideKeyboardwithoutPopulate(getActivity());
                // put your code here
                //TextInputEditText mcode = (TextInputEditText)v.findViewById(R.id.code);
                mname.setError(null);
                mpat.setError(null);
                mmat.setError(null);
                mnac.setError(null);
                Boolean cancel = false;

                String vname = mname.getText().toString()+"";
                String vpat = mpat.getText().toString()+"";
                String vmat = mmat.getText().toString()+"";
                String vnac = mnac.getText().toString()+"";


                if(vname.isEmpty()) {
                    mname.setError("Campo requerido");
                    cancel=true;
                }

                if(vpat.isEmpty()) {
                    mpat.setError("Campo requerido");
                    cancel=true;
                }

                if(vmat.isEmpty()) {
                    mmat.setError("Campo requerido");
                    cancel=true;
                }

                if(vnac.isEmpty()) {
                    mnac.setError("Campo requerido");
                    cancel=true;
                }

                if(sexo.getCheckedRadioButtonId() == R.id.hombre){
                    ts = WSkeys.hombre;
                }

                if (sexo.getCheckedRadioButtonId() == R.id.mujer){
                    ts = WSkeys.mujer;
                }

                if (sexo.getCheckedRadioButtonId() == R.id.no_indicar){
                    ts = WSkeys.noindicar;
                }

                if (ts.equals("")){
                    Snackbar.make(view, getString(R.string.e_sexo), Snackbar.LENGTH_LONG)
                            .show();
                    cancel = true;
                    Utilities.SetLog("SEXO",ts,WSkeys.log);
                }else{
                    client.setSexo(ts);
                }

                if (!cancel){
                    try {
                        PersonalDataTask(vname,vpat,vmat, vnac);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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


    /**
     * Represents an asynchronous registration task used to register
     * the user.
     */
    public void PersonalDataTask(final String vname, final String vpat, final String vmat, final String vnac) throws JSONException {

        JSONObject params = new JSONObject();
        params.put(WSkeys.name, vname);
        params.put(WSkeys.user, client.getUsername());
        params.put(WSkeys.name, vname);
        params.put(WSkeys.apepat, vpat);
        params.put(WSkeys.apemat, vmat);
        params.put(WSkeys.fechanacimiento, vnac);
        params.put(WSkeys.sexo, ts);
        params.put(WSkeys.img,client.getPhoto());



        Log.e("nameValuePairs--",params.toString());

        String url = WSkeys.URL_BASE + WSkeys.URL_PERSONALDATA;



        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ParserPersonal(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("El error", error.toString());
                Snackbar.make(mname, R.string.errorlistener, Snackbar.LENGTH_SHORT)
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

    public void ParserPersonal(JSONObject respuesta) throws JSONException {

        Log.e("RESP-PERSONALDATA", respuesta.toString());


        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)){
            String data = respuesta.getString(WSkeys.data);


                JSONObject jousuario = respuesta.getJSONObject(WSkeys.data);
                if (bRegister.getText().equals("Actualizar")){
                    Utilities.SetPerfilData(jousuario,client);
                    Toast toast = Toast.makeText(getContext(),  R.string.successpersonalupdate, Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(getContext(),MenuActivity.class);
                    intent.putExtra("active", "C");
                    startActivity(intent);
                    getActivity().finish();
                }else {
                    Utilities.SetClientData(jousuario, client);
                    Toast toast = Toast.makeText(getContext(),  R.string.successpersonalvalidation, Toast.LENGTH_LONG);
                    toast.show();
                    getActivity().getSupportFragmentManager().beginTransaction().hide(this).commit();
                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();
                    getActivity().onBackPressed();
                }


        } // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(view, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }


    //END SECTION TO REGISTER PERSONAL DATA

    private void getDate(final EditText mnac){
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
                mnac.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case SETPHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    if(selectedImage !=null){
                        InputStream imageStream = null;

                        try {
                            imageStream = getContext().getContentResolver().openInputStream(selectedImage);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        /*ByteArrayOutputStream output = new ByteArrayOutputStream();
                        byte[] bytes;
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        try {
                            while ((bytesRead = imageStream.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bytes = output.toByteArray();
                        String encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
*/
                        Bitmap selectedImagebt = BitmapFactory.decodeStream(imageStream);
                        String encodedImage = encodeImage(selectedImagebt);
                        client.setPhoto(encodedImage);
                    }
                }
        }
    }

    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,10,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.NO_WRAP);  //NO_WRAP

        return encImage;


    }


}
