package com.example.trashcanapp.dbmodel;

import android.media.Image;
import android.util.Log;

import com.example.trashcanapp.constants.*;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;

public class RecycleBin {
    private GeoPoint geopoint;
    private HashMap<String, Integer> binTypeTrust;
    private String description;
    private Image photograph;
    private HashMap<String, Integer> validity;

    public GeoPoint getGeopoint() {
        return geopoint;
    }

    public void setGeopoint(GeoPoint geopoint) {
        this.geopoint = geopoint;
    }

    public HashMap<String, Integer> getBinTypeTrust() {
        return binTypeTrust;
    }

    public void setBinTypeTrust(HashMap<String, Integer> binTypeTrust) {
        this.binTypeTrust = binTypeTrust;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Image getPhotograph() {
        return photograph;
    }

    public void setPhotograph(Image photograph) {
        this.photograph = photograph;
    }

    public HashMap<String, Integer> getValidity() {
        return validity;
    }

    public void setValidity(HashMap<String, Integer> validity) {
        this.validity = validity;
    }

    public RecycleBin(){
        if (this.validity == null){
            HashMap<String, Integer> vali = new HashMap<>();
            vali.put("exist", 1);
            vali.put("notexist", 0);
            this.setValidity(vali);
        }
        if(this.binTypeTrust == null){
            HashMap<String, Integer> trust = new HashMap<>();
            trust.put(CONSTANTS.PLASTIC,0);
            trust.put(CONSTANTS.GLASS,0);
            trust.put(CONSTANTS.CAN,0);
            trust.put(CONSTANTS.PAPER,0);
            trust.put(CONSTANTS.BATTERY,0);
            this.setBinTypeTrust(trust);
        }

    }
}
