package com.hereticpurge.simplespeeddial.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface QuickContactDao {

    @Insert
    void insertContact(QuickContact quickContact);

    @Query("SELECT * FROM QuickContact")
    List<QuickContact> getQuickContactList(String name);
}
