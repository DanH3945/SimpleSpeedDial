package com.danh3945.simplespeeddial.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {QuickContact.class}, version = 3)
public abstract class QuickContactDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "speed_dial_quick_contact_database";

    private static QuickContactDatabase quickContactDatabase;

    public abstract QuickContactDao quickContactDao();

    public static QuickContactDatabase getQuickContactDatabase(Context context) {
        if (quickContactDatabase == null) {
            quickContactDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    QuickContactDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return quickContactDatabase;
    }

    public static void destroyInstance() {
        quickContactDatabase = null;
    }
}
