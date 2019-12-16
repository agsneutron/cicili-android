package com.cicili.mx.cicili;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.cicili.mx.cicili.domain.Client;
import com.cicili.mx.cicili.io.InputMessage;

import java.util.ArrayList;
import java.util.List;

public class AdapterMessage extends RecyclerView.Adapter<HolderMessage> {

    private List<InputMessage> listMensaje = new ArrayList<>();
    private Context c;

    public AdapterMessage(Context c) {
        this.c = c;
    }

    public void addMensaje(InputMessage m){
        listMensaje.add(m);
        notifyItemInserted(listMensaje.size());
    }

    public  void clearMensajes(){
        listMensaje.clear();
        notifyDataSetChanged();

    }

    @Override
    public HolderMessage onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.cardview_message,parent,false);
        return new HolderMessage(v);
    }

    @Override
    public void onBindViewHolder(HolderMessage holder, int position) {
        Application application = (Application) Client.getContext();
        Client client = (Client) application;
        holder.getNombre().setText(listMensaje.get(position).getUsuario());
        holder.getMensaje().setText(listMensaje.get(position).getTexto());
        if (listMensaje.get(position).getIdUsuario().equals(client.getIdcte())) {

            byte[] decodedString = Base64.decode(client.getPhoto().substring(client.getPhoto().indexOf(",") + 1).getBytes(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            holder.getFotoMensajePerfil().setImageBitmap(decodedByte);
        }
        /*if(listMensaje.get(position).getType_mensaje().equals("2")){
            holder.getFotoMensaje().setVisibility(View.VISIBLE);
            holder.getMensaje().setVisibility(View.VISIBLE);
           // Glide.with(c).load(listMensaje.get(position).getUrlFoto()).into(holder.getFotoMensaje());
        }else if(listMensaje.get(position).getType_mensaje().equals("1")){*/
            holder.getFotoMensaje().setVisibility(View.GONE);
            holder.getMensaje().setVisibility(View.VISIBLE);
        //}
        /*if(listMensaje.get(position).getFotoPerfil().isEmpty()){
            holder.getFotoMensajePerfil().setImageResource(R.mipmap.ic_launcher);
        }else{
           // Glide.with(c).load(listMensaje.get(position).getFotoPerfil()).into(holder.getFotoMensajePerfil());
        }*/
        //Long codigoHora = listMensaje.get(position).getHora();
        String codigoHora = listMensaje.get(position).getFecha();
        //Date d = new Date(codigoHora);
        //SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");//a pm o am
        //holder.getHora().setText(sdf.format(d));
        holder.getHora().setText(codigoHora);
    }

    @Override
    public int getItemCount() {
        return listMensaje.size();
    }
}
