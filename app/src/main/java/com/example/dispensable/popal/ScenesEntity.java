package com.example.dispensable.popal;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "scenes")
public class ScenesEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String scenesName;
    public String eventActionTableName;
}
