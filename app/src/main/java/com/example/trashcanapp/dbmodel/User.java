package com.example.trashcanapp.dbmodel;


import java.util.ArrayList;

public class User {
    private String nameSurname;
    private String UserID;
    private ArrayList<Object> recycleBinList;

    public User(String nameSurname) {
        this.nameSurname = nameSurname;
    }
}
