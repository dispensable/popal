package com.example.dispensable.popal;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface EventActionEntityDao {
    @Query("select * FROM event_action_table")
    List<EventActionEntity> getEventActionList();

    @Query("select * FROM event_action_table WHERE scenesId = :scenesId")
    List<EventActionEntity> getCallBackListByScenesId(String scenesId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addEventActionEntity(EventActionEntity eventActionEntity);
}
