package com.cicili.mx.cicili;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cicili.mx.cicili.ScheduleMainFragment.OnListFragmentInteractionListener;
import com.cicili.mx.cicili.domain.PedidoData;
import com.cicili.mx.cicili.dummy.DummyContent.DummyItem;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemScheduleRecyclerViewAdapter extends RecyclerView.Adapter<MyItemScheduleRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<PedidoData> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemScheduleRecyclerViewAdapter(ArrayList<PedidoData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_itemschedule, parent, false);
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
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
