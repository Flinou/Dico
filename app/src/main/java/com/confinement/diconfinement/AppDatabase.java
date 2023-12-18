package com.confinement.diconfinement;

import android.os.Parcelable;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Definitions.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DefinitionsDao definitionsDao();
}

