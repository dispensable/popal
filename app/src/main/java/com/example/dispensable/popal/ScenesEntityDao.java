package com.example.dispensable.popal;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ScenesEntityDao {
    @Query("select * FROM scenes")
    List<ScenesEntity> getScenesList();

    @Query("select * FROM scenes WHERE scenesName = :name")
    ScenesEntity getScenesByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addScenes(ScenesEntity scenesEntity);

    @Delete()
    void deleteScenes(ScenesEntity scenesEntity);
}
