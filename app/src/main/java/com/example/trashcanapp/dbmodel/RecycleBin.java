package com.example.trashcanapp.dbmodel;

import android.media.Image;

import com.example.trashcanapp.constants.BINTYPE;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;

public class RecycleBin {
    private GeoPoint geopoint;
    private BINTYPE binType;
    private String description;
    private Image photograph;
    HashMap<String, Integer> reviews = new HashMap<String, Integer>();

    public GeoPoint getGeopoint() {
        return geopoint;
    }

    public void setGeopoint(GeoPoint geopoint) {
        this.geopoint = geopoint;
    }

    public BINTYPE getBinType() {
        return binType;
    }

    public void setBinType(BINTYPE binType) {
        this.binType = binType;
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

    public HashMap<String, Integer> getReviews() {
        return reviews;
    }

    public void setReviews(HashMap<String, Integer> reviews) {
        this.reviews = reviews;
    }

    public RecycleBin(){
        this.reviews.put("star1Count", 0);
        this.reviews.put("star2Count", 0);
        this.reviews.put("star3Count", 0);
        this.reviews.put("star4Count", 0);
        this.reviews.put("star5Count", 0);
    }
}
