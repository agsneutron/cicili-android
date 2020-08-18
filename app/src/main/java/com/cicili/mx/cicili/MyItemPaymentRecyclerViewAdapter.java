package com.cicili.mx.cicili;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cicili.mx.cicili.PaymentMainFragment.OnListFragmentInteractionListener;
import com.cicili.mx.cicili.domain.PaymentData;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.dummy.DummyContent.DummyItem;
import com.cicili.mx.cicili.io.Utilities;

import java.util.ArrayList;

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
        Utilities.SetLog("P-TIPOPAGO ",String.valueOf(mValues.get(position).getTipoPago()),WSkeys.log );
        holder.mIdView.setText(stipopago);
        holder.mContentView.setText(String.valueOf(mValues.get(position).getNumero()));
        holder.mDescriptionView.setText(String.valueOf(mValues.get(position).getVencimiento()));
        Utilities.SetLog("P-TIPOTARJETA ",mValues.get(position).getTipoTarjeta(),WSkeys.log );
       /* switch (mValues.get(position).getTipoTarjeta()) {
            case WSkeys.dvisa:
                holder.mImage.setImageResource(R.drawable.ic_visa);
                Utilities.SetLog("P-RVH-V ",mValues.get(position).getTipoTarjeta(),WSkeys.log );
                break;
            case WSkeys.dmc:
                holder.mImage.setImageResource(R.drawable.ic_mastercard);
                Utilities.SetLog("P-RVH-MC ",mValues.get(position).getTipoTarjeta(),WSkeys.log );
                break;
            case WSkeys.damex:
                holder.mImage.setImageResource(R.drawable.ic_american_express);
                Utilities.SetLog("P-RVH-AMX ",mValues.get(position).getTipoTarjeta(),WSkeys.log );
                break;
        }
*/
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
        public final ImageView mImage;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mDescriptionView;
        public PaymentData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImage = (ImageView) view.findViewById(R.id.inbox);
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
