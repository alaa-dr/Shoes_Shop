package com.example.shoes_shop.database.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.shoes_shop.database.DbContract;

public class OrderHome {
    private String Name;
    private int Sync_status;

    public OrderHome(String name, int sync_status) {
        Name = name;
        Sync_status = sync_status;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getSync_status() {
        return Sync_status;
    }

    public void setSync_status(int sync_status) {
        Sync_status = sync_status;
    }



}
