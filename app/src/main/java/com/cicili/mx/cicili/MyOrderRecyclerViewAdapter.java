package com.cicili.mx.cicili;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cicili.mx.cicili.OrderMainFragment.OnListFragmentInteractionListener;
import com.cicili.mx.cicili.domain.PedidoData;
import com.cicili.mx.cicili.dummy.DummyContent.DummyItem;

import java.util.ArrayList;

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
        holder.mIdView.setText(String.valueOf(mValues.get(position).getCantidad()));
        holder.mDate.setText(String.valueOf(mValues.get(position).getFechaSolicitada()));
        holder.mTime.setText(String.valueOf(mValues.get(position).getHoraSolicitada()));
        holder.mCantidad.setText(String.valueOf(mValues.get(position).getMonto()));
        holder.mFormaPago.setText(String.valueOf(mValues.get(position).getAlias()));
        holder.mContentView.setText(String.valueOf(mValues.get(position).getDireccion()));
        holder.mStatus.setText(String.valueOf(mValues.get(position).getNombreStatus()));
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
        public final TextView mStatus;
        public PedidoData mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
            mDate = view.findViewById(R.id.date);
            mTime = view.findViewById(R.id.time);
            mCantidad = view.findViewById(R.id.cantidad);
            mFormaPago = view.findViewById(R.id.formaPago);
            mStatus = view.findViewById(R.id.status_order);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
