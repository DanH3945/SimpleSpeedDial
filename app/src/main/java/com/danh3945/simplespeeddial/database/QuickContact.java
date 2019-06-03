package com.danh3945.simplespeeddial.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.net.Uri;
import android.support.annotation.NonNull;

@Entity
@TypeConverters(LocalTypeConverters.class)
public class QuickContact {

    @PrimaryKey @NonNull
    private String id;
    private String name;
    private String number;
    private String numberType;
    private Uri lookup_uri;

    public QuickContact(String id) {
        this.id = id;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

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

    public String getNumberType() {
        return numberType;
    }

    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }

    public Uri getLookup_uri() {
        return lookup_uri;
    }

    public void setLookup_uri(Uri lookup_uri) {
        this.lookup_uri = lookup_uri;
    }
}
