package com.danh3945.simplespeeddial.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SingleTileWidgetDao {

    @Insert
    void insertSingleTileData(SingleTileWidgetObject singleTileWidgetObject);

    @Delete()
    void deleteSingleTileData(SingleTileWidgetObject singleTileWidgetObject);

    @Query("SELECT * FROM SingleTileWidgetObject")
    List<SingleTileWidgetObject> getSingleTileDataList();

}
