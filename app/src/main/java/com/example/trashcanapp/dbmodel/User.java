package com.example.trashcanapp.dbmodel;


import java.util.ArrayList;

public class User {
    private String nameSurname;
    private ArrayList<String> recycleBinList;

    public User(){}

    public User( String nameSurname) {
        this.nameSurname = nameSurname;
    }


    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }


    public ArrayList<String> getRecycleBinList() {
        return recycleBinList;
    }

    public void setRecycleBinList(ArrayList<String> recycleBinList) {
        this.recycleBinList = recycleBinList;
    }
}
