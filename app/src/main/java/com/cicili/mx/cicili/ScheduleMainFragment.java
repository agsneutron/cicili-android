package com.cicili.mx.cicili;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.domain.PedidoData;
import com.cicili.mx.cicili.domain.WSkeys;
import com.cicili.mx.cicili.dummy.DummyContent;
import com.cicili.mx.cicili.dummy.DummyContent.DummyItem;
import com.cicili.mx.cicili.io.Utilities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ScheduleMainFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    Application application = (Application) Client.getContext();
    Client client = (Client) application;
    public ArrayList<PedidoData> PEDIDO_ITEMS = new ArrayList<PedidoData>();
    View view;
    ImageView img_back;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScheduleMainFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ScheduleMainFragment newInstance(int columnCount) {
        ScheduleMainFragment fragment = new ScheduleMainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_itemschedule_list, container, false);

        LlenaPedido();

        img_back = view.findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PerfilData.class);
                intent.putExtra("active",WSkeys.datos_programar);
                startActivity(intent);

            }
        });

        return view;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }





    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(PedidoData item);
    }

    public void LlenaPedido(){


        String url = WSkeys.URL_BASE + WSkeys.URL_CONSULTA_PROGRAMADOS;
        Utilities.SetLog("LLENAPEDIDOs",url,WSkeys.log);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ParserPedido(response);
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

    public void ParserPedido(String response) throws JSONException {

        Utilities.SetLog("RESPONSE_PEDIDOLIST",response,WSkeys.log);
        //Log.e("CodeResponse", response);

        JSONObject respuesta = new JSONObject(response);

        // si el response regresa ok, entonces si inicia la sesión
        if (respuesta.getInt("codeError") == (WSkeys.okresponse)) {
            //ontener nivel de data
            //Utilities.SetLog("RESPONSEASENTAMIENTOS",data,WSkeys.log);
            //JSONArray ja_usocfdi = respuesta.getJSONArray(WSkeys.data);
            Utilities.SetPedidoData(respuesta,client);
            Utilities.SetLog("PEDIDO_ARRAY",respuesta.toString(),WSkeys.log);
            if (client.getPedidosDataArrayList() != null) {
                PEDIDO_ITEMS = client.getPedidosDataArrayList();

                Utilities.SetLog("MAINPEDIDO", PEDIDO_ITEMS.get(0).getFormaPago(), WSkeys.log);
            }
            // Set the adapter
            //if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
            //    if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            //    } else {
            //        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            //    }
            recyclerView.setAdapter(new MyItemScheduleRecyclerViewAdapter(PEDIDO_ITEMS, mListener));

        }
        // si ocurre un error al registrar la solicitud se muestra mensaje de error
        else{
            Snackbar.make(view, respuesta.getString(WSkeys.messageError), Snackbar.LENGTH_SHORT)
                    .show();

            Utilities.SetLog("ERRORPARSER",response,WSkeys.log);
        }
    }
}
