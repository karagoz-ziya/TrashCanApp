package com.example.trashcanapp.dbmodel;

import android.media.Image;
import android.util.Log;

import com.example.trashcanapp.constants.BINTYPE;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;

public class RecycleBin {
    private GeoPoint geopoint;
    private HashMap<BINTYPE, Integer> binTypeTrust;
    private String description;
    private Image photograph;
    private HashMap<String, Integer> validity;

    public GeoPoint getGeopoint() {
        return geopoint;
    }

    public void setGeopoint(GeoPoint geopoint) {
        this.geopoint = geopoint;
    }

    public HashMap<BINTYPE, Integer> getBinTypeTrust() {
        return binTypeTrust;
    }

    public void setBinTypeTrust(HashMap<BINTYPE, Integer> binTypeTrust) {
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


    }
}
