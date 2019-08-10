package com.cicili.mx.cicili;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.widget.TextView;

public class PerfilData extends AppCompatActivity implements PersonalDataFragment.OnFragmentInteractionListener,
        PaymentDataFragment.OnFragmentInteractionListener, AddressDataFragment.OnFragmentInteractionListener,
        RfcDataFragment.OnFragmentInteractionListener{
    private TextView mTextMessage;

    Application application = (Application) Client.getContext();
    Client client = (Client) application;

    final Fragment fragmentPersonal = new PersonalDataFragment();
    final Fragment fragmentPayment = new PaymentDataFragment();
    final Fragment fragmentAddress = new AddressDataFragment();
    final Fragment fragmentRfc = new RfcDataFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragmentPersonal;
    BottomNavigationView nv;


    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    /*private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_perfil:
                    fm.beginTransaction().hide(active).show(fragmentPersonal).commit();
                    active = fragmentPersonal;
                    return true;
                case R.id.navigation_payment:
                    fm.beginTransaction().hide(active).show(fragmentPayment).commit();
                    active = fragmentPayment;
                    return true;
                case R.id.navigation_address:
                    fm.beginTransaction().hide(active).show(fragmentAddress).commit();
                    active = fragmentAddress;
                    return true;
            }
            return false;
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_data);
        //BottomNavigationView navView = findViewById(R.id.nav_view);
        //navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        String dataactive="";
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String id="";



        if (bundle != null) {
            //si trae fragment por activar
            dataactive = bundle.getString("active");
            id =  bundle.getString("id");

            Utilities.SetLog("LOG ID",id,WSkeys.log);
        }

        //fm.beginTransaction().add(R.id.container, fragmentAddress, "3").hide(fragmentAddress).commit();
        //fm.beginTransaction().add(R.id.container, fragmentPayment, "2").hide(fragmentPayment).commit();
        //fm.beginTransaction().add(R.id.container, fragmentPersonal, "1").hide(fragmentPersonal).commit();
        //fm.beginTransaction().hide(active).show(fragmentPersonal).commit();


        if (dataactive.equals(WSkeys.datos_personales) || active.equals("")) {
            //navView.setSelectedItemId(R.id.navigation_perfil);
            fm.beginTransaction().hide(active);
            fm.beginTransaction().add(R.id.container, fragmentPersonal, "1").show(fragmentPersonal).commit();
            active = fragmentPersonal;
        }
        else if (dataactive.equals(WSkeys.datos_pago)){
            //navView.setSelectedItemId(R.id.navigation_payment);
            if(id != null) {
                bundle = new Bundle();
                bundle.putString("ARG_PARAM1", id);
                bundle.putString("ARG_PARAM2", " ");
                Utilities.SetLog("LOG ID in not null",id,WSkeys.log);
                fragmentPayment.setArguments(bundle);
            }
            fm.beginTransaction().hide(active);
            fm.beginTransaction().add(R.id.container, fragmentPayment, "2").show(fragmentPayment).commit();
            active = fragmentPayment;
        }
        else if (dataactive.equals(WSkeys.datos_direccion)){
            //navView.setSelectedItemId(R.id.navigation_address);
            if(id != null) {
                bundle = new Bundle();
                bundle.putString("ARG_PARAM1", id);
                bundle.putString("ARG_PARAM2", " ");
                Utilities.SetLog("LOG ID in not null",id,WSkeys.log);
                fragmentAddress.setArguments(bundle);
            }
            fm.beginTransaction().hide(active);
            fm.beginTransaction().add(R.id.container, fragmentAddress, "3").show(fragmentAddress).commit();
            active = fragmentAddress;
        }
        else if (dataactive.equals(WSkeys.datos_rfc)) {
            //navView.setSelectedItemId(R.id.navigation_address);
            if (id != null) {
                bundle = new Bundle();
                bundle.putString("ARG_PARAM1", id);
                bundle.putString("ARG_PARAM2", " ");
                Utilities.SetLog("LOG ID in not null", id, WSkeys.log);
                fragmentRfc.setArguments(bundle);
            }
            fm.beginTransaction().hide(active);
            fm.beginTransaction().add(R.id.container, fragmentRfc, "4").show(fragmentRfc).commit();
            active = fragmentRfc;
        }

    }
}
