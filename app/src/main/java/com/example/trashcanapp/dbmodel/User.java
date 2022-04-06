package com.example.trashcanapp.dbmodel;


import java.util.ArrayList;

public class User {
    private String nameSurname;
    private ArrayList<Object> recycleBinList;

    public User( String nameSurname) {
        this.nameSurname = nameSurname;
    }


    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }


    public ArrayList<Object> getRecycleBinList() {
        return recycleBinList;
    }

    public void setRecycleBinList(ArrayList<Object> recycleBinList) {
        this.recycleBinList = recycleBinList;
    }
}
