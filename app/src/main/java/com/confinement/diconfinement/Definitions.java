package com.confinement.diconfinement;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Definitions {
    @PrimaryKey
    @ColumnInfo(name = "ID")
    public int id;

    @ColumnInfo(name = "NATURE")
    public String nature;

    @ColumnInfo(name = "DEFINITION")
    public String definition;

    @ColumnInfo(name = "WORD")
    public String word;

    @ColumnInfo(name = "SYNONYM")
    public String synonym;
}
