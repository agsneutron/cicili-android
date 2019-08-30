package com.cicili.mx.cicili.domain;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by ariaocho on 25/06/19.
 */

public class Client extends Application {

    private String username;
    private String lastname;
    private String lastsname;
    private Integer idcte;
    private String status;
    private String token;
    private String iddevice;
    private String date;
    private String device;
    private String name;
    private String access_token;
    private String usertype;
    private String address;
    private String email;
    private String cellphone;
    private String photo;
    private String sexo;
    private Integer rfcdatasize;
    private ArrayList<PaymentData> paymentDataArrayList;
    private ArrayList<AddressData> addressDataArrayList;
    private ArrayList<RfcData> rfcDataArrayList;
    private ArrayList<AutotanquesCercanos> autotanquesCercanosArrayList;
    private ArrayList<Pedido> pedidoDataArrayList;


    private static Context mContext;

    public void onCreate(){
        super.onCreate();
        this.mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }


    public Client() {
    }

    public Client(String username, String lastname, String lastsname, Integer idcte, String status, String token, String iddevice, String date, String device, String name, String access_token, String usertype, String address, String email, String cellphone, String photo, String sexo, Integer rfcdatasize, ArrayList<PaymentData> paymentDataArrayList, ArrayList<AddressData> addressDataArrayList, ArrayList<RfcData> rfcDataArrayList, ArrayList<AutotanquesCercanos> autotanquesCercanosArrayList, ArrayList<Pedido> pedidoDataArrayList) {
        this.username = username;
        this.lastname = lastname;
        this.lastsname = lastsname;
        this.idcte = idcte;
        this.status = status;
        this.token = token;
        this.iddevice = iddevice;
        this.date = date;
        this.device = device;
        this.name = name;
        this.access_token = access_token;
        this.usertype = usertype;
        this.address = address;
        this.email = email;
        this.cellphone = cellphone;
        this.photo = photo;
        this.sexo = sexo;
        this.rfcdatasize = rfcdatasize;
        this.paymentDataArrayList = paymentDataArrayList;
        this.addressDataArrayList = addressDataArrayList;
        this.rfcDataArrayList = rfcDataArrayList;
        this.autotanquesCercanosArrayList = autotanquesCercanosArrayList;
        this.pedidoDataArrayList = pedidoDataArrayList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLastsname() {
        return lastsname;
    }

    public void setLastsname(String lastsname) {
        this.lastsname = lastsname;
    }

    public Integer getIdcte() {
        return idcte;
    }

    public void setIdcte(Integer idcte) {
        this.idcte = idcte;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIddevice() {
        return iddevice;
    }

    public void setIddevice(String iddevice) {
        this.iddevice = iddevice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ArrayList<PaymentData> getPaymentDataArrayList() {
        return paymentDataArrayList;
    }

    public void setPaymentDataArrayList(ArrayList<PaymentData> paymentDataArrayList) {
        this.paymentDataArrayList = paymentDataArrayList;
    }

    public ArrayList<AddressData> getAddressDataArrayList() {
        return addressDataArrayList;
    }

    public void setAddressDataArrayList(ArrayList<AddressData> addressDataArrayList) {
        this.addressDataArrayList = addressDataArrayList;
    }

    public ArrayList<RfcData> getRfcDataArrayList() {
        return rfcDataArrayList;
    }

    public void setRfcDataArrayList(ArrayList<RfcData> rfcDataArrayList) {
        this.rfcDataArrayList = rfcDataArrayList;
    }


    public ArrayList<AutotanquesCercanos> getAutotanquesCercanosArrayList() {
        return autotanquesCercanosArrayList;
    }

    public void setAutotanquesCercanosArrayList(ArrayList<AutotanquesCercanos> autotanquesCercanosArrayList) {
        this.autotanquesCercanosArrayList = autotanquesCercanosArrayList;
    }

    public static Context getmContext() {
        return mContext;
    }

    public static void setmContext(Context mContext) {
        Client.mContext = mContext;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Integer getRfcdatasize() {
        return rfcdatasize;
    }

    public void setRfcdatasize(Integer rfcdatasize) {
        this.rfcdatasize = rfcdatasize;
    }

    public ArrayList<Pedido> getPedidoDataArrayList() {
        return pedidoDataArrayList;
    }

    public void setPedidoDataArrayList(ArrayList<Pedido> pedidoDataArrayList) {
        this.pedidoDataArrayList = pedidoDataArrayList;
    }
}
