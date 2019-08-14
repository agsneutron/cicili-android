package com.cicili.mx.cicili;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cicili.mx.cicili.PaymentMainFragment.OnListFragmentInteractionListener;
import com.cicili.mx.cicili.domain.PaymentData;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemPaymentRecyclerViewAdapter extends RecyclerView.Adapter<MyItemPaymentRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<PaymentData> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemPaymentRecyclerViewAdapter(ArrayList<PaymentData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_itempayment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Integer itipopago = mValues.get(position).getTipoPago();
        String stipopago ="";

        if(itipopago == WSkeys.efectivo) {
            stipopago = WSkeys.defectivo;
        }else if (itipopago == WSkeys.TDD) {
            stipopago = WSkeys.dTDD;
        }else if (itipopago ==  WSkeys.TDC) {
                stipopago = WSkeys.dTDC;
        }

        holder.mIdView.setText(stipopago);
        holder.mContentView.setText(String.valueOf(mValues.get(position).getNumero()));
        holder.mDescriptionView.setText(String.valueOf(mValues.get(position).getVencimiento()));

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
        public final TextView mDescriptionView;
        public PaymentData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mDescriptionView = (TextView) view.findViewById(R.id.description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
