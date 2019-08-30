package com.cicili.mx.cicili;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.cicili.mx.cicili.domain.AddressData;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.Pedido;
import com.cicili.mx.cicili.domain.WSkeys;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements MapMainFragment.OnFragmentInteractionListener,
        AddressMainFragment.OnListFragmentInteractionListener,
        MenuListDialogFragment.Listener{


    Application application = (Application) Client.getContext();
    Client client = (Client) application;

    final Fragment fragmentMain = new MapMainFragment();
    final Fragment fragmentAddress = new AddressMainFragment();
    final Fragment fragmentOrder = new OrderMainFragment();
    final FragmentManager fm = getSupportFragmentManager();

    Fragment active = fragmentMain;
    BottomNavigationView nv;


    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).show(fragmentMain).commit();
                    active = fragmentMain;
                    return true;
                case R.id.navigation_perfil:
                    //MenuListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    //intent.putExtra("token", token);
                    startActivity(intent);
                    //active = fragmenPerfil;
                    return true;
                case R.id.navigation_address:
                    fm.beginTransaction().hide(active).show(fragmentAddress).commit();
                    active = fragmentAddress;
                    return true;
                case R.id.navigation_orders:
                    fm.beginTransaction().hide(active).show(fragmentOrder).commit();
                    active = fragmentOrder;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        if (client.getStatus().equals(WSkeys.datos_personales)){
            Intent intent = new Intent(MainActivity.this, PerfilData.class);
            intent.putExtra("active","PE");
            startActivity(intent);
        }

        /*if (client.getStatus().equals(WSkeys.datos_pago)){
            Intent intent = new Intent(MainActivity.this, PerfilData.class);
            intent.putExtra("active","PA");
            startActivity(intent);
        }*/

        fm.beginTransaction().add(R.id.container, fragmentMain, "3").hide(fragmentAddress).commit();
        fm.beginTransaction().add(R.id.container, fragmentAddress, "2").hide(fragmentAddress).commit();
        fm.beginTransaction().add(R.id.container, fragmentOrder, "1").hide(fragmentOrder).commit();
        fm.beginTransaction().hide(active).show(fragmentMain).commit();

    }





    @Override
    public void onItemClicked(int position) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onListFragmentInteraction(AddressData item) {

    }

    public interface Listener {
        void onItemClicked(int position);
    }

}
