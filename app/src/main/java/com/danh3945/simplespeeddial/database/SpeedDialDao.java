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
    void insertSpeedDialButton(SpeedDialObject speedDialObject);

    @Query("SELECT * FROM SpeedDialObject")
    List<SpeedDialObject> getSpeedDialButtonsList();

    @Query("SELECT * FROM SpeedDialObject")
    LiveData<List<SpeedDialObject>> getSpeedDialButtonsListLiveData();

    @Delete
    void removeSpeedDialEntry(SpeedDialObject speedDialObject);
}
