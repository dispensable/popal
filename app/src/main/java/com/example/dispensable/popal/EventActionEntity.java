package com.example.dispensable.popal;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "event_action_table")
public class EventActionEntity {
        @PrimaryKey(autoGenerate = true)
        public int id;
        public int scenesId;
        public String event;
        public String sensor;
        public String sensorItem;
        public String order;
        public String value;
        public String objToAct;
        public String ActSource;
}
