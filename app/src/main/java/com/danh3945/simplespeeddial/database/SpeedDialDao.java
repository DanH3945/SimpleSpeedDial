package com.danh3945.simplespeeddial.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SpeedDialDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSpeedDialButton(SpeedDialBtn speedDialBtn);

    @Query("SELECT * FROM SpeedDialBtn")
    List<SpeedDialBtn> getSpeedDialButtonsList();

    @Query("SELECT * FROM SpeedDialBtn")
    LiveData<List<SpeedDialBtn>> getSpeedDialButtonsListLiveData();

    @Delete
    void removeSpeedDialEntry(SpeedDialBtn speedDialBtn);
}
