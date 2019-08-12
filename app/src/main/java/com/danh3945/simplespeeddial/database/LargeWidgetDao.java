package com.danh3945.simplespeeddial.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LargeWidgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSpeedDialButton(LargeWidgetObject largeWidgetObject);

    @Query("SELECT * FROM LargeWidgetObject")
    List<LargeWidgetObject> getSpeedDialButtonsList();

    @Query("SELECT * FROM LargeWidgetObject")
    LiveData<List<LargeWidgetObject>> getSpeedDialButtonsListLiveData();

    @Delete
    void removeSpeedDialEntry(LargeWidgetObject largeWidgetObject);
}
