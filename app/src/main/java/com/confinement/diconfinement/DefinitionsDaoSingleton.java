package com.confinement.diconfinement;

import android.content.Context;

import androidx.room.Room;

public class DefinitionsDaoSingleton {
    // private static instance variable to hold the singleton instance
    private static volatile DefinitionsDao INSTANCE = null;

    // private constructor to prevent instantiation of the class
    private DefinitionsDaoSingleton() {}

    // public static method to retrieve the singleton instance
    public static DefinitionsDao getInstance(Context context) {
        // Check if the instance is already created
        if(INSTANCE == null) {
            // synchronize the block to ensure only one thread can execute at a time
            synchronized (AppDatabase.class) {
                // check again if the instance is already created
                if (INSTANCE == null) {
                    // create the singleton instance
                    INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "definitions.db")
                            .createFromAsset("databases/definitions.db")
                            .allowMainThreadQueries().build().definitionsDao();
                    //Witchery : First SQL request si very slow. Making this request during app startup.
                    INSTANCE.findByWord(Globals.FIRST_SQL);
                }
            }
        }
        // return the singleton instance
        return INSTANCE;
    }
}

