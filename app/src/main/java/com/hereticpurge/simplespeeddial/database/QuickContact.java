package com.hereticpurge.simplespeeddial.database;

import android.arch.persistence.room.Entity;

@Entity
public class QuickContact {

    private String name;
    private String number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
