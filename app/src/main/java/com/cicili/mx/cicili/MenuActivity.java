package com.cicili.mx.cicili;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

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
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.PaymentData;
import com.cicili.mx.cicili.domain.PedidoActivo;
import com.cicili.mx.cicili.domain.PedidoData;
import com.cicili.mx.cicili.domain.RfcData;
import com.cicili.mx.cicili.domain.SeguimientoPedido;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.dummy.DummyContent;
import com.cicili.mx.cicili.io.SessionManager;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MapMainFragment.OnFragmentInteractionListener,
        AddressMainFragment.OnListFragmentInteractionListener,
        MenuListDialogFragment.Listener,
        UserProfileFragment.OnFragmentInteractionListener,
        PaymentMainFragment.OnListFragmentInteractionListener,
        AddressDetailFragment.OnFragmentInteractionListener,
        PaymentDetailFragment.OnFragmentInteractionListener,
        RfcMainFragment.OnListFragmentInteractionListener,
        RfcDetailFragment.OnFragmentInteractionListener,
        ScheduleMainFragment.OnListFragmentInteractionListener,
        OrderMainFragment.OnListFragmentInteractionListener,
        NotificationReceiver.OnNotificationReceiverListener,MessageReceiverCallback {


    Application application = (Application) Client.getContext();
    Client client = (Client) application;

    final Fragment fragmentMain = new MapMainFragment();
    final Fragment fragmentAddress = new AddressMainFragment();
    final Fragment fragmentOrder = new OrderMainFragment();
    final Fragment fragmenUserProfile = new UserProfileFragment();
    final Fragment fragmentPayment = new PaymentMainFragment();
    final Fragment fragmentRfc = new RfcMainFragment();
    final Fragment fragmentSchedule = new ScheduleMainFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragmentMain;
    Fragment current = fragmentMain;
    Fragment old = fragmentMain;
    BottomNavigationView nv;
    DrawerLayout drawer;
    FloatingActionButton fab_menu;
    FloatingActionButton fab_help;
    TextView tvname, tvarea;
    ImageView imageView;
    LinearLayout encabezado;
    ImageView img_back;
    PedidoActivo pedidoActivo = new PedidoActivo();
    SeguimientoPedido seguimientoPedido = new SeguimientoPedido();

    protected static final String BUTTON_ACTION = "buttonaction";
    protected static final int REQUEST_BUTTON = 300;

    private NotificationReceiver broadcast;

    SessionManager session;
    private String token_firebase="";
    /*private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).show(fragmentMain).commit();
                    active = fragmentMain;
                    return true;
                *//*case R.id.navigation_perfil:
                    //MenuListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
                    Intent intent = new Intent(MenuActivity.this, PerfilData.class);
                    //intent.putExtra("token", token);
                    startActivity(intent);
                    //active = fragmenPerfil;
                    return true;*//*
                case R.id.navigation_address:
                    fm.beginTransaction().hide(active).show(fragmentAddress).commit();
                    active = fragmentAddress;
                    return true;
                case R.id.navigation_orders:

                    if (!fragmentOrder.isAdded()) {
                        Utilities.SetLog("FRAGMENT_ORDER",  active.getTag(),WSkeys.log);
                        fm.beginTransaction().add(R.id.main_container,fragmentOrder,"fragmentOrder").commit();
                    }
                    fm.beginTransaction().addToBackStack("fragmentMain");
                    fm.beginTransaction().hide(active).show(fragmentOrder).commit();
                    active = fragmentOrder;

                    //este es el intent para alguna aclaración
                    //Intent intent = new Intent(MenuActivity.this, Aclaracion.class);
                    //startActivity(intent);
                    return true;
                case R.id.navigation_payment:
                    fm.beginTransaction().hide(active).show(fragmentPayment).commit();
                    active = fragmentPayment;
                    return true;
            }
            return false;
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(getApplicationContext());
        if (session.checkLogin()) {

            finish();
        }

        final HashMap<String, String> user = session.getSession();

        //FloatingActionButton fab = findViewById(R.id.fab_menu);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.id.fab_menu);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        fab_menu = (FloatingActionButton) findViewById(R.id.fab_menu);
        fab_help = (FloatingActionButton) findViewById(R.id.fab_help);
        /*encabezado = findViewById(R.id.encabezado);
        img_back = findViewById(R.id.img_back);*/


        client.setContextMap(MenuActivity.this);

        if (savedInstanceState != null) {
            return;
        }


        /*try {
            ValidaPedidoActivo();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        fab_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        fab_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, AyudaActivity.class);
                startActivity(intent);
            }
        });

        //Perfil update data
        View headerView = navigationView.getHeaderView(0);
        tvname = headerView.findViewById(R.id.tv_name);
        tvarea =  headerView.findViewById(R.id.tv_email);
        imageView = headerView.findViewById(R.id.imageView);


        //


        //BOTTOMNAVIGATION OPTIONS
        // BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        // navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        /*if (client.getStatus().equals(WSkeys.datos_personales)){
            Intent intent = new Intent(MenuActivity.this, PerfilData.class);
            intent.putExtra("active",WSkeys.datos_personales);
            startActivity(intent);
        }else if (client.getStatus().equals(WSkeys.datos_pago)){
                    Intent intent = new Intent(MenuActivity.this, PerfilData.class);
                    intent.putExtra("active",WSkeys.datos_pago);
                    startActivity(intent);
        }else if (client.getStatus().equals(WSkeys.datos_direccion)){
            Intent intent = new Intent(MenuActivity.this, PerfilData.class);
            intent.putExtra("active",WSkeys.datos_direccion);
            startActivity(intent);
        } else {*/
           /* if (findViewById(R.id.main_container) != null) {

                if (savedInstanceState != null) {
                return;
                }

                fm.beginTransaction().add(R.id.main_container, fragmentMain, "fragmentMain").commit();
                active = fragmentMain;
            }*/

        //fm.beginTransaction().add(R.id.main_container, fragmentAddress, "fragmentAddress").hide(fragmentAddress).commit();
        //fm.beginTransaction().add(R.id.main_container, fragmentOrder, "fragmentOrder").hide(fragmentOrder).commit();
        //fm.beginTransaction().add(R.id.main_container, fragmenUserProfile, "fragmenUserProfile").hide(fragmenUserProfile).commit();
        //fm.beginTransaction().add(R.id.main_container, fragmentPayment, "fragmentPayment").hide(fragmentPayment).commit();
        //fm.beginTransaction().add(R.id.main_container, fragmentRfc, "fragmentRfc").hide(fragmentRfc).commit();
        //fm.beginTransaction().hide(active).show(fragmentMain).commit();

        if (client.getAccess_token()=="" || client.getAccess_token()== null) {

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Utilities.SetLog("getInstanceId failed: ",task.getException().toString(),true);
                                return;
                            }

                            // Get new token instance
                            token_firebase = task.getResult().getToken();
                            client.setAccess_token(token_firebase);
                            Utilities.SetLog("TOKEN FIREBASE MENUACT: ",token_firebase,true);
                            UserLoginTask(user.get(SessionManager.KEY_EMAIL), user.get(SessionManager.KEY_PSW));
                        }
                    });
            Utilities.SetLog("LOGINFROMMENU", "MENUACTIVITY", WSkeys.log);
        }else{
            IniciaPerfil();
            //ActivaMap("0");
        }

        broadcast = new NotificationReceiver(this);
        IntentFilter intentFilter = new IntentFilter(BUTTON_ACTION);
        registerReceiver(broadcast, intentFilter);

    }

    @Override
    public void onBackPressed() {
        client.setContextMap(null);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showMenuBackToHelp();
            //super.onBackPressed();
            if (active == fragmentMain) {
                super.onBackPressed();
            } else {
                Utilities.SetLog("ONBACKPRESED", active.getTag(), WSkeys.log);
                fm.beginTransaction().hide(active).show(fragmentMain).commit();
                active = fragmentMain;
            }

        }
        Utilities.SetLog("ONBACKPRESED", "MENUACTIVITY", WSkeys.log);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;

        if (id == R.id.navigation_perfil) {

            Intent intent = new Intent(MenuActivity.this, PerfilDetailActivity.class);
            startActivity(intent);


        } /*else if (id == R.id.navigation_datos) {

            Intent intent = new Intent(MenuActivity.this, PerfilDetailActivity.class);
            startActivity(intent);


            //UserProfileFragment userProfileFragment = new UserProfileFragment();
            //userProfileFragment.show(getSupportFragmentManager(),"fragmenUserProfile");

        } else if (id == R.id.navigation_cuenta) {

            Intent intent = new Intent(MenuActivity.this, DeleteAccountActivity.class);
            startActivity(intent);


            //UserProfileFragment userProfileFragment = new UserProfileFragment();
            //userProfileFragment.show(getSupportFragmentManager(),"fragmenUserProfile");

        }*/ else if (id == R.id.navigation_address) {
            Utilities.SetLog("FRAGMENT_ADDRESS", active.getTag(), WSkeys.log);
            //fm.beginTransaction().add(R.id.main_container, fragmentAddress, "fragmentAddress").hide(active).commit();

            //fm.beginTransaction().add(R.id.main_container, fragmentAddress, "fragmentAddress").hide(active).commit();
            //fm.beginTransaction().addToBackStack("fragmentMain");
            //fm.beginTransaction().show(fragmentAddress).commit();
            //active = fragmentAddress;
            if (!fragmentAddress.isAdded()) {
                Utilities.SetLog("FRAGMENT_ADDRESS", active.getTag(), WSkeys.log);
                fm.beginTransaction().add(R.id.main_container, fragmentAddress, "fragmentAddress").commit();
            }
            fm.beginTransaction().addToBackStack("fragmentMain");
            fm.beginTransaction().hide(active).show(fragmentAddress).commit();
            active = fragmentAddress;
            hideMenuHelpToBack();

        } else if (id == R.id.navigation_payment) {

            if (!fragmentPayment.isAdded()) {
                Utilities.SetLog("FRAGMENT_PAYMENT", active.getTag(), WSkeys.log);
                fm.beginTransaction().add(R.id.main_container, fragmentPayment, "fragmentPayment").commit();
            }
            fm.beginTransaction().addToBackStack("fragmentMain").commit();
            fm.beginTransaction().hide(active).show(fragmentPayment).commit();
            active = fragmentPayment;
            hideMenuHelpToBack();


            //fm.beginTransaction().add(R.id.main_container, fragmentPayment, "fragmentPayment").hide(active).commit();
            //fm.beginTransaction().show(fragmentPayment).commit();
            //active = fragmentPayment;


        } else if (id == R.id.navigation_rfc) {
            //fm.beginTransaction().add(R.id.main_container, fragmentRfc, "fragmentRfc").hide(active).commit();
            //fm.beginTransaction().show(fragmentRfc).commit();
            //active = fragmentRfc;
            if (!fragmentRfc.isAdded()) {
                Utilities.SetLog("FRAGMENT_RFC", active.getTag(), WSkeys.log);
                fm.beginTransaction().add(R.id.main_container, fragmentRfc, "fragmentRfc").commit();
            }
            fm.beginTransaction().addToBackStack("fragmentMain").commit();
            fm.beginTransaction().hide(active).show(fragmentRfc).commit();
            active = fragmentRfc;
            hideMenuHelpToBack();

        } else if (id == R.id.navigation_schedule) {
            if (!fragmentSchedule.isAdded()) {
                Utilities.SetLog("FRAGMENT_Schedule", active.getTag(), WSkeys.log);
                fm.beginTransaction().add(R.id.main_container, fragmentSchedule, "fragmentSchedule").commit();
            }
            fm.beginTransaction().addToBackStack("fragmentMain").commit();
            fm.beginTransaction().hide(active).show(fragmentSchedule).commit();
            active = fragmentSchedule;
            hideMenuHelpToBack();

        } else if (id == R.id.navigation_history) {
            if (!fragmentOrder.isAdded()) {
                Utilities.SetLog("FRAGMENT_ORDER", active.getTag(), WSkeys.log);
                fm.beginTransaction().add(R.id.main_container, fragmentOrder, "fragmentOrder").commit();
            }
            fm.beginTransaction().addToBackStack("fragmentMain");
            fm.beginTransaction().hide(active).show(fragmentOrder).commit();
            active = fragmentOrder;
            hideMenuHelpToBack();

        } else if (id == R.id.nav_legal) {
            Intent intent = new Intent(MenuActivity.this, LegalListActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(MenuActivity.this, HelpListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_exit) {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            session.logoutSession();
            android.os.Process.killProcess(android.os.Process.myPid());
            finish();
            System.exit(0);
        }

//


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    @Override
    public void onItemClicked(int position) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onListFragmentInteraction(AddressData item) {
        //al dar clic en el elemento de la lista de direccioes

        Utilities.SetLog("MENUACTIVITYADRS", String.valueOf(item.getId()), WSkeys.log);
        String index = String.valueOf(client.getAddressDataArrayList().indexOf(item));
        Intent intent = new Intent(MenuActivity.this, AddressDetailActivity.class);
        intent.putExtra("ARG_PARAM1", index);
        startActivity(intent);

        /*AddressDetailFragment addressDetailFragment = new AddressDetailFragment();
        addressDetailFragment = AddressDetailFragment.newInstance(index,"");
        addressDetailFragment.setCancelable(false);
        addressDetailFragment.show(getSupportFragmentManager(),"fragmenAddressDetail");*/
    }

    @Override
    public void onListFragmentInteraction(PaymentData item) {
        Utilities.SetLog("MENUACTIVITYPAYMNT", String.valueOf(item.getId()), WSkeys.log);
        String index = String.valueOf(client.getPaymentDataArrayList().indexOf(item));
        Intent intent = new Intent(MenuActivity.this, PaymentDetailActivity.class);
        intent.putExtra("ARG_PARAM1", index);
        startActivity(intent);
       /* PaymentDetailFragment paymentDetailFragment = new PaymentDetailFragment();
        paymentDetailFragment = PaymentDetailFragment.newInstance(index,"");
        paymentDetailFragment.setCancelable(false);
        paymentDetailFragment.show(getSupportFragmentManager(),"fragmentPaymentDetail");*/
    }

    @Override
    public void onListFragmentInteraction(RfcData item) {

        Utilities.SetLog("MENUACTIVITYRFC", String.valueOf(item.getId()), WSkeys.log);
        String index = String.valueOf(client.getRfcDataArrayList().indexOf(item));
        Intent intent = new Intent(MenuActivity.this, RfcDetailActivity.class);
        intent.putExtra("ARG_PARAM1", index);
        startActivity(intent);

        /*RfcDetailFragment rfcDetailFragment = new RfcDetailFragment();
        rfcDetailFragment = RfcDetailFragment.newInstance(index,"");
        rfcDetailFragment.setCancelable(false);
        rfcDetailFragment.show(getSupportFragmentManager(),"fragmentRfcDetail");*/
    }

    @Override
    public void onListFragmentInteraction(PedidoData item) {

        Utilities.SetLog("MENUACTIVITYPEDIDO_ _", String.valueOf(item.getId()), WSkeys.log);
        String index = String.valueOf(client.getPedidosDataArrayList().indexOf(item));
        Intent intent = new Intent(MenuActivity.this, OrderDetailActivity.class);
        String order = String.valueOf(item.getId());
        intent.putExtra("ARG_PARAM1", order);
        startActivity(intent);
        /*
        RfcDetailFragment rfcDetailFragment = new RfcDetailFragment();
        rfcDetailFragment = RfcDetailFragment.newInstance(index,"");
        rfcDetailFragment.setCancelable(false);
        rfcDetailFragment.show(getSupportFragmentManager(),"fragmentRfcDetail");*/
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcast != null) unregisterReceiver(broadcast);
    }

    @Override
    public void onButtonClicked(Context context, Intent intentNotification) {
        String idPedido = intentNotification.getStringExtra("idPedido");
        String data = intentNotification.getStringExtra("data");

        Toast.makeText(this, "Aceptaron tu pedido", Toast.LENGTH_SHORT).show();

        //Gson gson = new Gson();
        //SeguimientoPedido seguimientoPedido= gson.fromJson(intentNotification.getData().toString() , SeguimientoPedido.class);


        Intent intent = new Intent(MenuActivity.this, PedidoAceptadoActivity.class);

        Utilities.SetLog("intent idpedido: ", idPedido, WSkeys.log);
        Utilities.SetLog("intent DATA", data, WSkeys.log);
        intent.putExtra("idPedido", idPedido);
        intent.putExtra("pedido_data", data);
        intent.putExtra("status", "2");
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    public void onStatusPedido(Context context, Intent intentNotification) {


    }

    public void hideMenuHelpToBack() {
        /*fab_menu.hide();
        fab_help.setImageResource(R.drawable.ic_close);
        fab_help.setImageResource(android.R.drawable.ic_menu_revert);
        fab_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/
        fab_menu.hide();
        fab_help.hide();
    }

    public void showMenuBackToHelp() {
        /*fab_menu.show();
        fab_help.setImageResource(R.drawable.ic_help_outline_white_24dp);
        fab_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, AyudaActivity.class);
                startActivity(intent);
            }
        });*/
        fab_menu.show();
        fab_help.show();
    }

    @Override
    public void getReceiverEstatusPedido(String status, String mensaje) {


        //Fragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.map);

        Fragment fragment = fragmentMain.getChildFragmentManager().findFragmentById(R.id.map);

        Utilities.SetLog("instanceof : ", String.valueOf(fragment), WSkeys.log);

        if (fragment instanceof SupportMapFragment) {

            /** Se valida que esta bien instanciado y se hace la comunicación*/

            MapMainFragment receptor = (MapMainFragment) fragment.getParentFragment();

            /** Se envia el mensaje al metodo del receptor*/
            receptor.setRecibeEstatusPedido(status);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Utilities.SetLog("MenuActivity onRequestPermissionsResult: ", String.valueOf(requestCode), WSkeys.log);
        Fragment fragment = fragmentMain.getChildFragmentManager().findFragmentById(R.id.map);

        Utilities.SetLog("instanceof : ", String.valueOf(fragment), WSkeys.log);

        if (fragment instanceof SupportMapFragment) {

            /** Se valida que esta bien instanciado y se hace la comunicación*/

            MapMainFragment receptor = (MapMainFragment) fragment.getParentFragment();

            /** Se envia el mensaje al metodo del receptor*/

            receptor.RequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    /*public void ValidaPedidoActivo() throws JSONException {

        String url = WSkeys.URL_BASE + WSkeys.URL_PEDIDO_ACTIVO;
        Utilities.SetLog("MAINSEARCH-PEDIDOACTIVO?", url, WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(MenuActivity.this);
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

                Utilities.SetLog("ERROR RESPONSE", error.toString(), WSkeys.log);
                *//*Snackbar.make(direcciones, R.string.errorlistener, Snackbar.LENGTH_SHORT)
                        .show();*//*
                Toast.makeText(MenuActivity.this, R.string.errorlistener, Toast.LENGTH_LONG).show();
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
        Utilities.SetLog("PARSER-MAIN_ACTIVO", response.toString(), WSkeys.log);

        // si el response regresa ok, entonces si inicia la sesión
        if (response.getInt("codeError") == (WSkeys.okresponse)) {
            JSONObject jo_data = response.getJSONObject(WSkeys.data);
            pedidoActivo = gson.fromJson(jo_data.toString(), PedidoActivo.class);
            seguimientoPedido = gson.fromJson(jo_data.toString(), SeguimientoPedido.class);
            seguimientoPedido.setTipo("3");
            client.setSeguimientoPedido(seguimientoPedido);
            Utilities.SetLog("PARSER-STATUS_ACTIVO", seguimientoPedido.getStatus(), WSkeys.log);

            if (seguimientoPedido.getStatus().equals("1")) {
                ActivaMap("1");
            } else {
                Intent intent = new Intent(MenuActivity.this, PedidoAceptadoActivity.class);
                String json_pedido = gson.toJson(pedidoActivo);
                intent.putExtra("pedido_data", json_pedido);
                intent.putExtra("idPedido", pedidoActivo.getId());
                intent.putExtra("pedido_data", json_pedido);
                intent.putExtra("status", pedidoActivo.getStatus());
                startActivity(intent);
            }

        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else if (response.getInt("codeError") == (WSkeys.no_error_ok)) {

            ActivaMap("0");
            Utilities.SetLog("PARSER-MAIN_ACTIVO", response.getString(WSkeys.messageError), WSkeys.log);
        } else {
            *//*Snackbar.make(direcciones, response.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();*//*
            Toast.makeText(this, WSkeys.messageError, Toast.LENGTH_LONG).show();
        }
    }*/


    /*@Override
    protected void onRestart() {
        fm.beginTransaction().add(R.id.main_container, fragmentMain, "fragmentMain").commit();
        active = fragmentMain;
        super.onRestart();

    }*/
    public void ActivaMap(String activo) {
        Utilities.SetLog("MENU - activamap", activo, WSkeys.log);
        if (findViewById(R.id.main_container) != null) {
            Bundle bundle = new Bundle();
            bundle.putString("ARG_PARAM1", activo);
            bundle.putString("ARG_PARAM2", " ");
            Utilities.SetLog("MENU - ACTIVO", activo, WSkeys.log);
            fragmentMain.setArguments(bundle);
            fm.beginTransaction().add(R.id.main_container, fragmentMain, "fragmentMain").commitAllowingStateLoss();
            active = fragmentMain;
        }
    }


    public void UserLoginTask(final String mEmail, final String mPassword) {
        Utilities.SetLog("TOKEN FIREBASE MNLOGTSK: ",token_firebase,true);


        String url = WSkeys.URL_BASE + WSkeys.URL_LOGIN;
        RequestQueue queue = Volley.newRequestQueue(MenuActivity.this);
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserData(response, mEmail, mPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                    session.logoutSession();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                session.logoutSession();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=utf-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(WSkeys.PUSERNAME, mEmail);
                params.put(WSkeys.PPASSWORD, mPassword);
                params.put(WSkeys.TOKENFIREBASE, token_firebase);
                Log.e("PARAMETROS", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);

    }


    public void ParserData(String response, String userName, String userPassword) throws JSONException {

        //Log.e("LoginResponse", response);
        JSONObject respuesta = new JSONObject(response);
        Utilities.SetLog("MENULOGIN response", response, true);

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)) {
            JSONObject jousuario = respuesta.getJSONObject(WSkeys.data);
            //JSONArray jousuario = respuesta.getJSONArray(WSkeys.data);

            //Utilities.SetLog("LOGIN idcte",Integer.toString(jousuario.getInt(WSkeys.idcte)).toString(),true);
            client.setIdcte(jousuario.getInt(WSkeys.idcte));
            client.setStatus(jousuario.getString(WSkeys.status));
            client.setToken(jousuario.getString(WSkeys.token));
            client.setEmail(jousuario.getString(WSkeys.email));
            client.setCellphone(jousuario.getString(WSkeys.cel));
            client.setUsername(userName);
            client.setSexo(jousuario.getString(WSkeys.sexo));
            client.setPhoto(jousuario.getString(WSkeys.img));
            Utilities.SetLog("MENULOGIN TOKEN", client.getToken(), WSkeys.log);

            //Utilities.SetLog("MENULOGIN IMAGEN", jousuario.getString(WSkeys.img), WSkeys.log);
            //client.setSexo("");
            Utilities.SetClientData(jousuario, client);

            session.createSession(client.getToken(), userName, userPassword);

            if (client.getStatus().equals(WSkeys.datos_personales)) {
                Intent intent = new Intent(MenuActivity.this, PerfilData.class);
                intent.putExtra("active", WSkeys.datos_personales);
                startActivity(intent);
            }
            if (client.getStatus().equals(WSkeys.datos_pago)) {
                Intent intent = new Intent(MenuActivity.this, PerfilData.class);
                intent.putExtra("active", WSkeys.datos_pago);
                startActivity(intent);
            }

            if (client.getStatus().equals(WSkeys.datos_direccion)) {
                Intent intent = new Intent(MenuActivity.this, PerfilData.class);
                intent.putExtra("active", WSkeys.datos_direccion);
                startActivity(intent);
            }

            IniciaPerfil();
        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else {
            session.logoutSession();
        }
    }

    public void IniciaPerfil(){
        tvname.setText(client.getName());
        tvarea.setText(client.getEmail());
        if (!client.getPhoto().isEmpty() || client.getPhoto() != null) {

            byte[] decodedString = Base64.decode(client.getPhoto().substring(client.getPhoto().indexOf(",") + 1).getBytes(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
           // Utilities.SetLog("IMAGEN CLIENTE", client.getPhoto().substring(client.getPhoto().indexOf(",") + 1), WSkeys.log);
            imageView.setImageBitmap(decodedByte);
        }
        ActivaMap("0");

    }
}


