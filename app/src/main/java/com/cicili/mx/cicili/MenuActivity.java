package com.cicili.mx.cicili;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.cicili.mx.cicili.domain.AddressData;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.PaymentData;
import com.cicili.mx.cicili.domain.PedidoData;
import com.cicili.mx.cicili.domain.RfcData;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.dummy.DummyContent;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Base64;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

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
        OrderMainFragment.OnListFragmentInteractionListener{


    Application application = (Application) Client.getContext();
    Client client = (Client) application;

    final Fragment fragmentMain = new MapMainFragment();
    final Fragment fragmentAddress = new AddressMainFragment();
    final Fragment fragmentOrder = new OrderMainFragment();
    final Fragment fragmenUserProfile = new UserProfileFragment();
    final Fragment fragmentPayment= new PaymentMainFragment();
    final Fragment fragmentRfc = new RfcMainFragment();
    final Fragment fragmentSchedule = new ScheduleMainFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragmentMain;
    Fragment current = fragmentMain;
    Fragment old = fragmentMain;
    BottomNavigationView nv;


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
        FloatingActionButton fab = findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        //PErfil update data
        View headerView = navigationView.getHeaderView(0);
        TextView tvname = (TextView)headerView.findViewById(R.id.tv_name);
        tvname.setText(client.getName());
        TextView tvarea = (TextView)headerView.findViewById(R.id.tv_email);
        tvarea.setText(client.getEmail());
        ImageView imageView = (ImageView)headerView.findViewById(R.id.imageView);

        byte[] decodedString = Base64.decode(client.getPhoto().substring(client.getPhoto().indexOf(",") + 1).getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        Utilities.SetLog("IMAGEN CLIENTE",client.getPhoto().substring(client.getPhoto().indexOf(",") + 1),WSkeys.log);

        imageView.setImageBitmap(decodedByte);
        //


        //BOTTOMNAVIGATION OPTIONS
        // BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
       // navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        if (client.getStatus().equals(WSkeys.datos_personales)){
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
        } else {

            if (findViewById(R.id.main_container) != null) {

                if (savedInstanceState != null) {
                return;
                }

                fm.beginTransaction().add(R.id.main_container, fragmentMain, "fragmentMain").commit();
                active = fragmentMain;
            }

            //fm.beginTransaction().add(R.id.main_container, fragmentAddress, "fragmentAddress").hide(fragmentAddress).commit();
            //fm.beginTransaction().add(R.id.main_container, fragmentOrder, "fragmentOrder").hide(fragmentOrder).commit();
            //fm.beginTransaction().add(R.id.main_container, fragmenUserProfile, "fragmenUserProfile").hide(fragmenUserProfile).commit();
            //fm.beginTransaction().add(R.id.main_container, fragmentPayment, "fragmentPayment").hide(fragmentPayment).commit();
            //fm.beginTransaction().add(R.id.main_container, fragmentRfc, "fragmentRfc").hide(fragmentRfc).commit();
            //fm.beginTransaction().hide(active).show(fragmentMain).commit();

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if (active==fragmentMain){
                super.onBackPressed();
            }
            else{

                Utilities.SetLog("ONBACKPRESED",active.getTag(),WSkeys.log);
                fm.beginTransaction().hide(active).show(fragmentMain).commit();
                active=fragmentMain;
            }

        }



        Utilities.SetLog("ONBACKPRESED","MENUACTIVITY",WSkeys.log);
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


            //UserProfileFragment userProfileFragment = new UserProfileFragment();
            //userProfileFragment.show(getSupportFragmentManager(),"fragmenUserProfile");

        } else if (id == R.id.navigation_address) {
            Utilities.SetLog("FRAGMENT_ADDRESS", active.getTag(),WSkeys.log);
            //fm.beginTransaction().add(R.id.main_container, fragmentAddress, "fragmentAddress").hide(active).commit();

            //fm.beginTransaction().add(R.id.main_container, fragmentAddress, "fragmentAddress").hide(active).commit();
            //fm.beginTransaction().addToBackStack("fragmentMain");
            //fm.beginTransaction().show(fragmentAddress).commit();
            //active = fragmentAddress;
            if (!fragmentAddress.isAdded()) {
                Utilities.SetLog("FRAGMENT_ADDRESS",  active.getTag(),WSkeys.log);
                fm.beginTransaction().add(R.id.main_container,fragmentAddress,"fragmentAddress").commit();
            }
            fm.beginTransaction().addToBackStack("fragmentMain");
            fm.beginTransaction().hide(active).show(fragmentAddress).commit();
            active = fragmentAddress;


        } else if (id == R.id.navigation_payment) {

            if (!fragmentPayment.isAdded()) {
                Utilities.SetLog("FRAGMENT_PAYMENT",  active.getTag(),WSkeys.log);
                fm.beginTransaction().add(R.id.main_container,fragmentPayment,"fragmentPayment").commit();
            }
            fm.beginTransaction().addToBackStack("fragmentMain").commit();
            fm.beginTransaction().hide(active).show(fragmentPayment).commit();
            active = fragmentPayment;

            //fm.beginTransaction().add(R.id.main_container, fragmentPayment, "fragmentPayment").hide(active).commit();
            //fm.beginTransaction().show(fragmentPayment).commit();
            //active = fragmentPayment;


        } else if (id == R.id.navigation_rfc) {
            //fm.beginTransaction().add(R.id.main_container, fragmentRfc, "fragmentRfc").hide(active).commit();
            //fm.beginTransaction().show(fragmentRfc).commit();
            //active = fragmentRfc;
            if (!fragmentRfc.isAdded()) {
                Utilities.SetLog("FRAGMENT_RFC", active.getTag(),WSkeys.log);
                fm.beginTransaction().add(R.id.main_container,fragmentRfc,"fragmentRfc").commit();
            }
            fm.beginTransaction().addToBackStack("fragmentMain").commit();
            fm.beginTransaction().hide(active).show(fragmentRfc).commit();
            active = fragmentRfc;

        } else if (id == R.id.navigation_schedule) {
            if (!fragmentSchedule.isAdded()) {
                Utilities.SetLog("FRAGMENT_Schedule", active.getTag(), WSkeys.log);
                fm.beginTransaction().add(R.id.main_container, fragmentSchedule, "fragmentSchedule").commit();
            }
            fm.beginTransaction().addToBackStack("fragmentMain").commit();
            fm.beginTransaction().hide(active).show(fragmentSchedule).commit();
            active = fragmentSchedule;
        }else if(id == R.id.navigation_history){
            if (!fragmentOrder.isAdded()) {
                Utilities.SetLog("FRAGMENT_ORDER",  active.getTag(),WSkeys.log);
                fm.beginTransaction().add(R.id.main_container,fragmentOrder,"fragmentOrder").commit();
            }
            fm.beginTransaction().addToBackStack("fragmentMain");
            fm.beginTransaction().hide(active).show(fragmentOrder).commit();
            active = fragmentOrder;
        }else if (id == R.id.nav_legal) {
            Intent intent = new Intent(MenuActivity.this, LegalActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_help) {
            Intent intent = new Intent(MenuActivity.this, AyudaActivity.class);
            startActivity(intent);

        }
//


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri){
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
        PaymentDetailFragment paymentDetailFragment = new PaymentDetailFragment();
        paymentDetailFragment = PaymentDetailFragment.newInstance(index,"");
        paymentDetailFragment.setCancelable(false);
        paymentDetailFragment.show(getSupportFragmentManager(),"fragmentPaymentDetail");
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

        Utilities.SetLog("MENUACTIVITYPEDIDO", String.valueOf(item.getIdAutotanque()), WSkeys.log);
        /*String index = String.valueOf(client.getPedidoDataArrayList().indexOf(item));
        RfcDetailFragment rfcDetailFragment = new RfcDetailFragment();
        rfcDetailFragment = RfcDetailFragment.newInstance(index,"");
        rfcDetailFragment.setCancelable(false);
        rfcDetailFragment.show(getSupportFragmentManager(),"fragmentRfcDetail");*/
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
