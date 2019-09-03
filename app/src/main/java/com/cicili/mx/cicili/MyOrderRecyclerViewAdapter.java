package com.cicili.mx.cicili;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cicili.mx.cicili.OrderMainFragment.OnListFragmentInteractionListener;
import com.cicili.mx.cicili.domain.Pedido;
import com.cicili.mx.cicili.domain.PedidoData;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.dummy.DummyContent.DummyItem;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyOrderRecyclerViewAdapter extends RecyclerView.Adapter<MyOrderRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<PedidoData> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyOrderRecyclerViewAdapter(ArrayList<PedidoData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.valueOf(mValues.get(position).getNombreStatus()));
        holder.mDate.setText(String.valueOf(mValues.get(position).getFechaSolicitada()));
        holder.mTime.setText(String.valueOf(mValues.get(position).getHoraSolicitada()));
        holder.mCantidad.setText(String.valueOf(mValues.get(position).getCantidad()));
        holder.mFormaPago.setText(String.valueOf(mValues.get(position).getFormaPago()));
        holder.mContentView.setText(String.valueOf(mValues.get(position).getDireccion()));


       /* MapsInitializer.initialize(getActivity());

        holder.mMapView.onCreate(dialog.onSaveInstanceState());
        holder.mMapView.onResume();


        holder.mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                mMap = googleMap;

                if (mLocationPermissionGranted) {
                    Utilities.SetLog(LOG, "mLocationPermissionGranted", WSkeys.log);
                    //getDeviceCurrentLocation();
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }



                    if (pos != null) {
                        setPosition(LatFTA,LonFTA,addressData.getAlias());
                    }
                    setPinForLocation(addressData.getAlias());
                }
            }
        });*/

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mDate;
        public final TextView mTime;
        public final TextView mCantidad;
        public final TextView mFormaPago;
        //public final MapView mMapView;
        private GoogleMap mMap;
        public PedidoData mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mDate = (TextView) view.findViewById(R.id.date);
            mTime = (TextView) view.findViewById(R.id.time);
            mCantidad = (TextView) view.findViewById(R.id.cantidad);
            mFormaPago = (TextView) view.findViewById(R.id.formaPago);
            //mMapView = (MapView) view.findViewById(R.id.map_pedidos);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
