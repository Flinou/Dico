package com.confinement.diconfinement;

import android.os.Parcelable;

import androidx.room.Dao;
import androidx.room.Query;

import com.confinement.diconfinement.Definitions;

import java.util.List;

@Dao
public interface DefinitionsDao {
     @Query("SELECT * FROM definitions")
        List<Definitions> getAll();

     @Query("SELECT * FROM definitions WHERE word LIKE :word LIMIT 1")
        List<Definitions> findByWord(String word);
}
