package com.danh3945.simplespeeddial.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface QuickContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertContact(QuickContact quickContact);

    @Query("SELECT * FROM QuickContact")
    List<QuickContact> getQuickContactList();

    @Query("SELECT * FROM QuickContact")
    LiveData<List<QuickContact>> getQuickContactObservableList();

    @Query("SELECT * FROM QuickContact WHERE name = :name")
    QuickContact getSingleContact(String name);

    @Delete
    void removeSpeedDialEntry(QuickContact quickContact);
}
