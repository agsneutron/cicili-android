package com.cicili.mx.cicili;

import android.Manifest;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cicili.mx.cicili.domain.AddressData;
import com.cicili.mx.cicili.domain.AutotanquesCercanos;
import com.cicili.mx.cicili.domain.AutotanquesDisponibles;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.MotivoCancela;
import com.cicili.mx.cicili.domain.PaymentData;
import com.cicili.mx.cicili.domain.Pedido;
import com.cicili.mx.cicili.domain.PedidoActivo;
import com.cicili.mx.cicili.domain.SeguimientoPedido;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.cicili.mx.cicili.domain.Client.getContext;


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
    Spinner direcciones, pipas;
    private ArrayList<String> direccionArray;
    private ArrayList<String> pipasArray;
    private ArrayList<AddressData> direccionAux;
    private ArrayList<String> autotanquesDisponiblesArray;
    Gson gson = new Gson();
    Integer direccionSeleccionada=0;
    Double latitudPedido, longitudPedido, monto_c, litro_c, latCurrent, lonCurrent;
    LinearLayout bottom_sheet;
    BottomSheetBehavior bsb;
    TextView name_usuario, label_pedido;
    LinearLayout featuredlayout, populatlayout;
    LinearLayout bottom_sheetmascercano;
    LinearLayoutCompat layoutDirecciones, layoutPedidoActivo;
    BottomSheetBehavior bsb_mascercano;
    MaterialButton btn_pedidoActivo;
    PedidoActivo pedidoActivo = new PedidoActivo();
    SeguimientoPedido seguimientoPedido = new SeguimientoPedido();
    String motivo_seleccionado="";
    String motivo_texto="";
    LinearLayout bottom_sheet_cancelar;
    BottomSheetBehavior bsb_cancelar;
    String json_order="";
    ArrayList<String> motivoArray = new ArrayList<String>();
    ArrayList<MotivoCancela> motivoAux = new ArrayList<MotivoCancela>();


    /***** Ejecutar tarea cada 5 segundos < **/
    Handler handler = new Handler();
    private final int TIEMPO = 2000;
    Double iLat = 0.00, iLon=0.00;
    private Marker mMarkerConductor=null;
    /***** > Ejecutar tarea cada 5 segundos **/


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
        pipas = (Spinner) view.findViewById(R.id.spinner2);

        label_pedido = view.findViewById(R.id.labelpedido);

        direcciones.setOnItemSelectedListener(this);
        pipas.setOnItemSelectedListener(this);


        layoutPedidoActivo = view.findViewById(R.id.LayoutPedidoActivo);
        try {
            ValidaPedidoActivo();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        btn_pedidoActivo = view.findViewById(R.id.btnActivo);
        layoutDirecciones = view.findViewById(R.id.LayoutDireccion);
        if (client.getOrder_id() !=null && client.getOrder_id() != ""){
            Intent intent = new Intent(getContext(), PedidoAceptadoActivity.class);
            Gson gson = new Gson();
            String data = gson.toJson(client.getSeguimientoPedido());
            Utilities.SetLog("intent idpedido: ", client.getOrder_id(), WSkeys.log);
            Utilities.SetLog("intent DATA",data, WSkeys.log);
            intent.putExtra("idPedido",client.getOrder_id());
            intent.putExtra("pedido_data",data);
        }


        //map
        getMyLocationPermision();
        //pedido mascercano

        bottom_sheetmascercano = (LinearLayout)view.findViewById(R.id.bottomSheetCercano);
        bsb_mascercano = BottomSheetBehavior.from(bottom_sheetmascercano);
        bsb_mascercano.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                String nuevoEstado = "";
                final RadioGroup rgFormaPago = view.findViewById(R.id.rgFormaPago_mc);
                rgFormaPago.check(R.id.tarjeta_mc);
                String formapagoseleccionada="";
                final RadioGroup rgMontoLitro = view.findViewById(R.id.rgMontoLitro_mc);
                rgMontoLitro.check(R.id.litro_mc);
                final TextInputEditText input_monto_litros = view.findViewById(R.id.input_mc);
                //final TextInputEditText calculo_monto_litro = (TextInputEditText) view.findViewById(R.id.calculo_input_mc);
                //final TextInputLayout calculoinput = (TextInputLayout)view.findViewById(R.id.calculoinput_mc);
                final TextInputLayout labelinput = (TextInputLayout)view.findViewById(R.id.labelinput_mc);
                switch(newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        nuevoEstado = "STATE_COLLAPSED";
                        layoutDirecciones.setVisibility(View.VISIBLE);
                        pipas.setSelection(0,true);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nuevoEstado = "STATE_EXPANDED";
                        layoutDirecciones.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        nuevoEstado = "STATE_HIDDEN";
                        layoutDirecciones.setVisibility(View.VISIBLE);
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



                rgMontoLitro.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        if(radioGroup.getCheckedRadioButtonId() == R.id.litro_mc){
                            labelinput.setHint("Litros");
                            //calculoinput.setHint("Monto");
                            input_monto_litros.setText(input_monto_litros.getText().toString());
                            if (input_monto_litros.getText().toString().isEmpty()){
                                monto_c=0.0;
                                litro_c = 0.0;
                            }else {
                                litro_c = Double.parseDouble(input_monto_litros.getText().toString());
                                Utilities.SetLog("in cancel m 0", input_monto_litros.getText().toString(), WSkeys.log);
                                monto_c = 0.0;
                            }
                        }

                        if (radioGroup.getCheckedRadioButtonId() == R.id.monto_mc){
                            labelinput.setHint("Monto");
                            //calculoinput.setHint("Litros");
                            input_monto_litros.setText(input_monto_litros.getText().toString());
                            if (input_monto_litros.getText().toString().isEmpty()){
                                monto_c=0.0;
                                litro_c = 0.0;
                            }else {
                                monto_c = Double.parseDouble(input_monto_litros.getText().toString());
                                Utilities.SetLog("in cancel l 0", input_monto_litros.getText().toString(), WSkeys.log);
                                litro_c = 0.0;
                            }
                        }
                    }
                });



                final String finalFormapagoseleccionada = formapagoseleccionada;
                Button btnConformaPedido = (Button) view.findViewById(R.id.btnConfirmaMasCercano);
                btnConformaPedido.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean cancel = false;
                        View focusView = null;
                        String error="";

                        // Check for a valid l/p, if the user entered one.
                        if (TextUtils.isEmpty(input_monto_litros.getText()) || String.valueOf(input_monto_litros.getText()).equals("0")) {
                            // litros.setError(getString(R.string.error_invalid_value));
                            // focusView = litros;
                            error=getString(R.string.error_invalid_value);
                            cancel = true;
                        }



                        if (rgMontoLitro.getCheckedRadioButtonId() == R.id.monto_mc){
                            // Check for a valid ammount.
                            if (monto_c < 200.00) {
                                Snackbar.make(view, R.string.error_invalid_ammount, Snackbar.LENGTH_SHORT).show();
                                cancel = true;
                            }
                        }

                        if(rgMontoLitro.getCheckedRadioButtonId() == R.id.litro_mc){
                            litro_c = Double.parseDouble(input_monto_litros.getText().toString());
                            monto_c =0.0;
                        }

                        if (rgMontoLitro.getCheckedRadioButtonId() == R.id.monto_mc){
                            monto_c = Double.parseDouble(input_monto_litros.getText().toString());
                            litro_c = 0.0;
                        }

                        // Check for a valid password, if the user entered one.
                        /*if (TextUtils.isEmpty(calculo_monto_litro.getText()) || String.valueOf(calculo_monto_litro.getText()).equals("0")) {
                            Utilities.SetLog("ERROR PRECIO", String.valueOf(calculo_monto_litro.getText()), WSkeys.log);
                            //precio.setError(getString(R.string.error_invalid_value));
                            //focusView = precio;
                            error=getString(R.string.error_invalid_value);
                            cancel = true;
                        }*/

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

                            //monto_c  = nf.parse(litros.getText().toString()).doubleValue();
                            // monto = nf.parse(precio.getText().toString()).doubleValue();


                            Utilities.SetLog("MAS CERCANO MoNTO", String.valueOf(monto_c), WSkeys.log);
                            Utilities.SetLog("MAS CERCANO Litros", String.valueOf(litro_c), WSkeys.log);
                            Utilities.SetLog("MAS CERCANO PEDIR", finalFormapagoseleccionada, WSkeys.log);
                            AddressData addressData = new AddressData();
                            addressData.setId(direccionSeleccionada);
                            pedido.setCantidad(litro_c);
                            pedido.setMonto(monto_c);
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

        featuredlayout = view.findViewById(R.id.featuredlayout);
        featuredlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.SetLog("DIRECCIONSELECCIONADA", String.valueOf(direccionSeleccionada), WSkeys.log);
                if(pipas.getSelectedItemId()>0){
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

        /*populatlayout = view.findViewById(R.id.populatlayout);
        populatlayout.setVisibility(View.GONE);
        populatlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (direcciones.getSelectedItem().equals("") || direcciones.getSelectedItem().equals("0") || pipas.getSelectedItem().equals("") || pipas.getSelectedItem().equals("0") ){
                    Snackbar.make(view, R.string.error_invalid_selection, Snackbar.LENGTH_SHORT).show();
                }
                else{
                    bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

            }
        });*/

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
                final RadioGroup rgMontoLitro = (RadioGroup) view.findViewById(R.id.rgMontoLitro);
                rgMontoLitro.check(R.id.litro);
                String formapagoseleccionada="";
                final TextInputEditText input_monto_litros = (TextInputEditText) view.findViewById(R.id.input);
                final TextInputEditText calculo_monto_litro = (TextInputEditText) view.findViewById(R.id.calculo_input);
                final TextInputLayout calculoinput = (TextInputLayout)view.findViewById(R.id.calculoinput);
                final TextInputLayout labelinput = (TextInputLayout)view.findViewById(R.id.labelinput);


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

                rgMontoLitro.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        if(radioGroup.getCheckedRadioButtonId() == R.id.litro){
                            labelinput.setHint("Litros");
                            calculoinput.setHint("Monto");
                            input_monto_litros.setText(input_monto_litros.getText().toString());
                        }

                        if (radioGroup.getCheckedRadioButtonId() == R.id.monto){
                            labelinput.setHint("Monto");
                            calculoinput.setHint("Litros");
                            input_monto_litros.setText(input_monto_litros.getText().toString());
                        }
                    }
                });



                input_monto_litros.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        Double nuevoprecio;
                        Double calculaLitros;
                        if(!input_monto_litros.getText().toString().isEmpty()) {
                            if (rgMontoLitro.getCheckedRadioButtonId() == R.id.litro) {

                                if (Double.valueOf(input_monto_litros.getText().toString()) > 0) {
                                    nuevoprecio = client.getAutotanquesCercanosArrayList().get(pipaSeleccionada).getPrecio() * Double.valueOf(input_monto_litros.getText().toString());
                                    calculo_monto_litro.setText(String.valueOf(nuevoprecio));
                                    monto_c = nuevoprecio;
                                    litro_c = Double.parseDouble(input_monto_litros.getText().toString());
                                }
                                else{
                                    calculo_monto_litro.setText(String.valueOf(0));
                                }
                            } else if (rgMontoLitro.getCheckedRadioButtonId() == R.id.monto) {
                                if (Double.valueOf(input_monto_litros.getText().toString()) > 0) {
                                    calculaLitros = (Double.valueOf(input_monto_litros.getText().toString()) / client.getAutotanquesCercanosArrayList().get(pipaSeleccionada).getPrecio());
                                    calculo_monto_litro.setText(String.valueOf(calculaLitros));
                                    monto_c = Double.parseDouble(input_monto_litros.getText().toString());
                                    litro_c = calculaLitros;
                                }
                                else{
                                    calculo_monto_litro.setText(String.valueOf(0));
                                }
                            }
                        }
                        else {
                            input_monto_litros.setText("0");
                        }
                    }
                });
                /*input_monto_litros.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {

                    }
                });*/

                /*precio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                });*/

                Button btnConformaPedido = (Button) view.findViewById(R.id.btnConfirma);
                final String finalFormapagoseleccionada = formapagoseleccionada;
                btnConformaPedido.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean cancel = false;
                        View focusView = null;
                        String error="";

                        // Check for a valid l/p, if the user entered one.
                        if (TextUtils.isEmpty(input_monto_litros.getText()) || String.valueOf(input_monto_litros.getText()).equals("0")) {
                           // litros.setError(getString(R.string.error_invalid_value));
                           // focusView = litros;
                            error=getString(R.string.error_invalid_value);
                            cancel = true;
                        }

                        // Check for a valid ammount.
                        if (monto_c < 200.00) {
                            Snackbar.make(view, R.string.error_invalid_ammount, Snackbar.LENGTH_SHORT).show();
                            cancel = true;
                        }



                        // Check for a valid password, if the user entered one.
                        if (TextUtils.isEmpty(calculo_monto_litro.getText()) || String.valueOf(calculo_monto_litro.getText()).equals("0")) {
                            Utilities.SetLog("ERROR PRECIO", String.valueOf(calculo_monto_litro.getText()), WSkeys.log);
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
                            // cantidad  = nf.parse(litros.getText().toString()).doubleValue();
                            // monto = nf.parse(precio.getText().toString()).doubleValue();
                            cantidad = litro_c;
                            monto = monto_c;


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
                            bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }

                    }
                });

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheets", "Offset: " + slideOffset);
            }


        });



        //on backpressed to control bsb
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (bsb.getState()==BottomSheetBehavior.STATE_EXPANDED){
                            bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        else if(bsb.getState()==BottomSheetBehavior.STATE_COLLAPSED) {
                            return false;
                        }

                        return true;
                    }
                }
                return false;
            }
        });



        btn_pedidoActivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pedidoActivo.getStatus().equals("1")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Estatus de tu pedido: " + pedidoActivo.getNombreStatus() );
                    builder.setMessage("En breve recibiras información de tu pedido");
                    builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            bsb_cancelar.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    Intent intent = new Intent(getActivity(), PedidoAceptadoActivity.class);
                    String json_pedido = gson.toJson(pedidoActivo);
                    intent.putExtra("pedido_data",json_pedido);
                    intent.putExtra("idPedido",pedidoActivo.getId());
                    intent.putExtra("pedido_data",json_pedido);
                    intent.putExtra("status",pedidoActivo.getStatus());
                    startActivity(intent);
                }



                /*seguimientoPedido.setIdPedido(String.valueOf(pedidoActivo.getId()));
                seguimientoPedido.setStatus(String.valueOf(pedidoActivo.getStatus()));
                seguimientoPedido.setColor("");
                seguimientoPedido.setNombreConcesionario(pedidoActivo.getNombreConcesionario());
                seguimientoPedido.setNombreConductor(pedidoActivo.getNombreConductor());
                seguimientoPedido.setLatitud(String.valueOf(pedidoActivo.getLatitud()));
                seguimientoPedido.setLongitud(String.valueOf(pedidoActivo.getLongitud()));
                seguimientoPedido.setNombreStatus(pedidoActivo.getNombreStatus());
                seguimientoPedido.setTiempo("");
                seguimientoPedido.setTipo("3");*/

            }
        });

        //CANCELAR

        Spinner motivos = view.findViewById(R.id.motivos);
        LlenaMotivos(motivos);

        motivos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("onItemSelected",String.valueOf(i));

                motivo_seleccionado = String.valueOf(motivoAux.get(i).getId());
                motivo_texto = String.valueOf(motivoAux.get(i).getText());


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        bottom_sheet_cancelar = view.findViewById(R.id.bottomSheetCancela);
        bsb_cancelar = BottomSheetBehavior.from(bottom_sheet_cancelar);
        bsb_cancelar.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                String nuevoEstado = "";



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

                Button btnCancelaPedido = (Button) view.findViewById(R.id.cancela_pedido);
                btnCancelaPedido.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean cancel = false;
                        View focusView = null;
                        String error="";

                        // Check for a valid l/p, if the user entered one.
                        if (motivo_seleccionado.equals("")) {
                            // litros.setError(getString(R.string.error_invalid_value));
                            // focusView = litros;
                            error=getString(R.string.error_invalid_motivo);
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

                            //ejecuta  cancela pedido
                            try {
                                //CancelOrderTask(String.valueOf(motivo_seleccionado),String.valueOf(order));
                                Utilities.SetLog("in cancel motivo", motivo_seleccionado, WSkeys.log);
                                Utilities.SetLog("in cancel pedido", seguimientoPedido.getId(), WSkeys.log);
                                if(!seguimientoPedido.getId().equals("")) {
                                    CancelOrderTask(motivo_seleccionado, seguimientoPedido.getId());
                                }
                                else{
                                    Toast toast = Toast.makeText(getContext(),  "Espera a que se asigne tu pedido", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Utilities.SetLog("ejecuta cancela pedido", "", WSkeys.log);

                        }

                    }
                });

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheets", "Offset: " + slideOffset);
            }




            public void CancelOrderTask(final String motivo, final String order) throws JSONException {

                Map<String, String> params = new HashMap<String, String>();
                params.put(WSkeys.pedido, order);
                params.put(WSkeys.motivo, motivo);
                Log.e("PARAMETROSCANCEL_B", params.toString());


                String url = WSkeys.URL_BASE + WSkeys.URL_CANCELA+ "?"+WSkeys.pedido+"="+order+"&"+WSkeys.motivo+"="+motivo+"";
                Utilities.SetLog("CANCELA",url,WSkeys.log);

                RequestQueue queue = Volley.newRequestQueue(getContext());
                //JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
                StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            ParserCancela(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Utilities.SetLog("ERROR RESPONSE",error.toString(),WSkeys.log);
                        Snackbar.make(view, R.string.errorlistener, Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put(WSkeys.pedido, order);
                        params.put(WSkeys.motivo, motivo);
                        return new JSONObject(params).toString().getBytes();
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(WSkeys.pedido, order);
                        params.put(WSkeys.motivo, motivo);
                        Log.e("PARAMETROSCANCEL", params.toString());
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

            public void ParserCancela(String response) throws JSONException {

                Utilities.SetLog("PARSER-CANCELA",response.toString(),WSkeys.log);
                JSONObject response_object = new JSONObject(response);

                // si el response regresa ok, entonces si inicia la sesión
                if (response_object.getInt("codeError") == (WSkeys.okresponse)) {
                    String pedido_data = gson.toJson(seguimientoPedido);
                    Intent intent = new Intent(getActivity(), CancelaActivity.class);
                    intent.putExtra("cancel_result",response_object.getString("data"));
                    intent.putExtra("cause",motivo_texto);
                    intent.putExtra("order",pedido_data);
                    intent.putExtra("from","aceptado");
                    startActivity(intent);
                }
                // si ocurre un error al registrar la solicitud se muestra mensaje de error
                else{
                    Snackbar.make(view, response_object.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
        //FIN CANCELAR

        return view;
    }


    public void LlenaMotivos(final Spinner motivos){


        String url = WSkeys.URL_BASE + WSkeys.URL_MOTIVO_CANCELA;
        Utilities.SetLog("LLENA motivo CANCELA",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserMotivos(response, motivos);
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

    public void ParserMotivos(String response, Spinner motivos) throws JSONException {

        Utilities.SetLog("RESPONSE_MOTIVOS",response,WSkeys.log);
        //Log.e("CodeResponse", response);


        JSONObject respuesta = new JSONObject(response);
        Integer posselected =0;

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)) {
            //ontener nivel de data
            //Utilities.SetLog("RESPONSEASENTAMIENTOS",data,WSkeys.log);
            JSONArray ja_usocfdi = respuesta.getJSONArray(WSkeys.data);
            Utilities.SetLog("MOTIVOSARRAY",ja_usocfdi.toString(),WSkeys.log);
            for(int i=0; i<ja_usocfdi.length(); i++){
                MotivoCancela motivoCancela = new MotivoCancela();
                try {

                    JSONObject jsonObject = (JSONObject) ja_usocfdi.get(i);
                    motivoCancela.setId(jsonObject.getInt("id"));
                    motivoCancela.setText(jsonObject.getString("text"));
                    motivoAux.add(motivoCancela);
                    motivoArray.add(jsonObject.getString("text"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            motivos.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,motivoArray));
            motivos.setSelection(posselected);
        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(view, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }
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
                                iLat=iLat+0.0001399;
                                iLon=iLon+0.0001223;
                                Location getCurrentLocation = (Location) task.getResult();
                                Utilities.SetLog("Ubicacion: ", Double.toString(getCurrentLocation.getLatitude()) + " , " + Double.toString(getCurrentLocation.getLongitude()), WSkeys.log);

                                    ActualizarUbicacionTask(getCurrentLocation.getLatitude()+iLat,getCurrentLocation.getLongitude()-iLon);

                            } else {
                                Utilities.SetLog("MAP-LOcATION", task.toString(), WSkeys.log);
                                Snackbar.make(view, R.string.failedgetlocation, Snackbar.LENGTH_SHORT)
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

        AddMarkerConductor(latitud,longitud,"AGS","Pipas el pipo", 100.00,"2", 1);
    }

    //MAP
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceCurrentLocation();
        }
        //ejecutarTarea();
    }

    private void getDeviceCurrentLocation() {
        //Snackbar.make(direcciones, R.string.gettingDeviceLocation, Snackbar.LENGTH_SHORT)
        //        .show();

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
                               latCurrent = getCurrentLocation.getLatitude();
                               lonCurrent = getCurrentLocation.getLongitude();
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
                //ejecutarTarea();
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

                            //bsb.setState(BottomSheetBehavior.STATE_EXPANDED);

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

                            //bsb.setState(BottomSheetBehavior.STATE_EXPANDED);

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

            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION_PERMISSION);
            }
            initMyMap();
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initMyMap();
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
            direccionArray.add("Enviar a mi ubicación");
            for (int i = 0; i < client.getAddressDataArrayList().size(); i++) {
                direccionArray.add(client.getAddressDataArrayList().get(i).getAlias());
            }

        }else{
            direccionArray.add("");
            direccionArray.add("Agrega una dirección");
            Utilities.SetLog("size dir",String.valueOf(direccionArray.size()),WSkeys.log);
        }
        direcciones.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, direccionArray));
    }


    public void LlenaPipas(final Spinner pipas){

        pipasArray = new ArrayList<String>();

        if (autotanquesCercanosAux != null) {

            pipasArray.add("Selecciona una pipa");
            for (int i = 0; i < autotanquesCercanosAux.size(); i++) {
                pipasArray.add(autotanquesCercanosAux.get(i).getConcesionario() + " $" + autotanquesCercanosAux.get(i).getPrecio() );
            }

        }else{
            pipasArray.add("No hay pipas disponibles");
            Utilities.SetLog("size dir",String.valueOf(pipasArray.size()),WSkeys.log);
        }
        pipas.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, pipasArray));
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

        Utilities.SetLog("PARSER-MAIN_PIPASCERC",response.toString(),WSkeys.log);
        autotanquesCercanosAux.clear();
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
                    LlenaPipas(pipas);
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
        Log.e("onItemSelected if",String.valueOf(i) + view + l + adapterView.toString());

        if (adapterView.getId()==R.id.spinner1) {

            if (i != 0) {
                if (client.getAddressDataArrayList() == null) {
                    Intent intent = new Intent(getContext(), PerfilData.class);
                    intent.putExtra("active", WSkeys.datos_direccion);
                    startActivity(intent);
                    direcciones.setSelection(0);
                } else {
                    //client.getAddressDataArrayList().get(i).getLatitud();
                    //client.getAddressDataArrayList().get(i).getLongitud();
                    Log.e("onItemSelected", String.valueOf(i));
                    if (i ==1){
                        latitudPedido=latCurrent;
                        longitudPedido=lonCurrent;
                        try {
                            ConsultaPrincipal(new LatLng(latitudPedido, longitudPedido));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        direccionSeleccionada = 0;

                    } else if (direcciones.getSelectedItemId()>1){
                        try {
                            //mMap.clear();
                            Log.e("Selected--idaddress", String.valueOf(client.getAddressDataArrayList().get(i - 2).getId()));
                            Log.e("Selected--alias", client.getAddressDataArrayList().get(i - 2).getAlias());
                            ConsultaPrincipal(new LatLng(client.getAddressDataArrayList().get(i - 2).getLatitud(), client.getAddressDataArrayList().get(i - 2).getLongitud()));
                            MoveCameraSelectedDirection(client.getAddressDataArrayList().get(i - 2).getLatitud(), client.getAddressDataArrayList().get(i - 2).getLongitud(), client.getAddressDataArrayList().get(i - 2).getAlias(), client.getAddressDataArrayList().get(i - 2).getId());
                            direccionSeleccionada = client.getAddressDataArrayList().get(i - 2).getId();
                            latitudPedido = client.getAddressDataArrayList().get(i - 2).getLatitud();
                            longitudPedido = client.getAddressDataArrayList().get(i - 2).getLongitud();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            } else {
                /*Intent intent = new Intent(getContext(), PerfilData.class);
                intent.putExtra("active", WSkeys.datos_direccion);
                startActivity(intent);*/
                direcciones.setSelection(0);
                direccionSeleccionada=0;
            }
        }else if (adapterView.getId()==R.id.spinner2){
            if (i != 0) {
                Log.e("onItemSelected PIPA", String.valueOf(i));
                if(direcciones.getSelectedItemId()>0) {
                    pipaSeleccionada = i - 1;
                    bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    Snackbar.make(view, R.string.error_noaddressselected, Snackbar.LENGTH_SHORT).show();
                    pipas.setSelection(0,true);
                    pipaSeleccionada=0;
                }

            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.e("nothing",adapterView.toString());
    }

    public void AddMarker(Double lat, Double lon, String conductor, String concesionario,Double precio, String tiempo, Integer id){
        Marker mMarker = mMap.addMarker(new MarkerOptions()
                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_pipa_2_01))
                .position(new LatLng(lat,lon))
                .title(concesionario)
                .snippet("$" + String.valueOf(precio) + " por litro \n" + "Llega en: "+ tiempo));

        mMarker.showInfoWindow();
        mMarker.setTag(id);
    }

    public void AddMarkerConductor(Double lat, Double lon, String conductor, String concesionario,Double precio, String tiempo, Integer id){
        if (mMarkerConductor != null) {
            mMarkerConductor.remove();
        }
        mMarkerConductor = mMap.addMarker(new MarkerOptions()
                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_pipa_1))
                .position(new LatLng(lat,lon))
                .title("Concesionario: "+concesionario)
                .snippet("Conductor: " + conductor + "\n" + "Precio: $"+ String.valueOf(precio) + "\n" + "Tiempo de Llegada: "+ tiempo));

        mMarkerConductor.showInfoWindow();
        mMarkerConductor.setTag(id);
    }

    public void MoveCameraSelectedDirection(Double lat, Double lon, String alias, Integer id){
        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(lat,lon))
                .zoom(15)
                .bearing(0)
                .tilt(45)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

        Marker mMarkerd = mMap.addMarker(new MarkerOptions()
                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_home_blue_24dp))
                .position(new LatLng(lat,lon))
                .title("Dirección")
                .snippet(alias));

        mMarkerd.showInfoWindow();
        mMarkerd.setTag(id);


    }



    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public void ValidaPedidoActivo() throws JSONException {

        String url = WSkeys.URL_BASE + WSkeys.URL_PEDIDO_ACTIVO;
        Utilities.SetLog("MAINSEARCH-PEDIDOACTIVO?",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ParserPedidoActivo(response);
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

    public void ParserPedidoActivo(JSONObject response) throws JSONException {
        Gson gson = new Gson();
        Utilities.SetLog("PARSER-MAIN_ACTIVO",response.toString(),WSkeys.log);

        // si el response regresa ok, entonces si inicia la sesión
        if (response.getInt("codeError") == (WSkeys.okresponse)) {
            JSONObject jo_data = response.getJSONObject(WSkeys.data);
            pedidoActivo = gson.fromJson(jo_data.toString(), PedidoActivo.class);
            seguimientoPedido = gson.fromJson(jo_data.toString(), SeguimientoPedido.class);
            seguimientoPedido.setTipo("3");
            client.setSeguimientoPedido(seguimientoPedido);
            Utilities.SetLog("PARSER-STATUS_ACTIVO",seguimientoPedido.getStatus(),WSkeys.log);

            if (seguimientoPedido.getStatus().equals("1")){
                label_pedido.setText("Tienes un pedido Solicitado");
            }
            else {
                label_pedido.setText(R.string.pedido_en_curso);
            }
            layoutDirecciones.setVisibility(View.GONE);
            layoutPedidoActivo.setVisibility(View.VISIBLE);
        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else if (response.getInt("codeError") == (WSkeys.no_error_ok)) {

            Utilities.SetLog("PARSER-MAIN_ACTIVO",response.getString(WSkeys.messageError),WSkeys.log);
        }else{
            Snackbar.make(direcciones, response.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();
        }
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