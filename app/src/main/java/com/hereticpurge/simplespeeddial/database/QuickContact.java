package com.hereticpurge.simplespeeddial.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.net.Uri;
import android.support.annotation.NonNull;

@Entity
@TypeConverters(LocalTypeConverters.class)
public class QuickContact {

    @PrimaryKey @NonNull
    private String name;
    private String number;
    private String numberType;
    private Uri lookup_uri;

    public QuickContact(String name) {
        this.name = name;
    }

    public @NonNull String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
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
