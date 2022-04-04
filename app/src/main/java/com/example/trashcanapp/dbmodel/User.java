package com.example.trashcanapp.dbmodel;


import java.util.ArrayList;

public class User {
    private String nameSurname;
    private String UserID;
    private ArrayList<Object> recycleBinList;

    public User(String userID, String nameSurname) {
        this.nameSurname = nameSurname;
        this.UserID = userID;
    }




    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public ArrayList<Object> getRecycleBinList() {
        return recycleBinList;
    }

    public void setRecycleBinList(ArrayList<Object> recycleBinList) {
        this.recycleBinList = recycleBinList;
    }
}
