package com.example.dispensable.popal;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {ScenesEntity.class, EventActionEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{
    private static AppDatabase sInstance;

    public static AppDatabase getDatabase(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                    "scenes.db").build();
        }
        return sInstance;
    }

    public static void onDestroy() {
        sInstance = null;
    }

    public abstract ScenesEntityDao getScenesEntityDao();

    public abstract EventActionEntityDao getEventActionEntityDao();
}
