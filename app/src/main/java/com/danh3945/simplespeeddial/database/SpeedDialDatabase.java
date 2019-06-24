package com.danh3945.simplespeeddial.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {SpeedDialBtn.class}, version = 1)
public abstract class SpeedDialDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "speed_dial_database";

    private static SpeedDialDatabase speedDialDatabase;

    public abstract SpeedDialDao speedDialDao();

    public static SpeedDialDatabase getSpeedDialDatabase(Context context) {
        if (speedDialDatabase == null) {
            speedDialDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    SpeedDialDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return speedDialDatabase;
    }

    public static void destroyInstance() {
        speedDialDatabase = null;
    }
}
