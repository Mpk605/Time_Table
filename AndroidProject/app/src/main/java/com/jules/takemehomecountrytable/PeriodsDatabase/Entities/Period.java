package com.jules.takemehomecountrytable.PeriodsDatabase.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "period_table")
public class Period implements Comparable<Period> {
    @PrimaryKey
    public int periodId;

    @ColumnInfo(name = "Title")
    public String title;

    @ColumnInfo(name = "Room")
    public String room;

    @ColumnInfo(name = "Type")
    public String type;

    @ColumnInfo(name = "StartHour")
    public String startHour;

    @ColumnInfo(name = "EndHour")
    public String endHour;

    @ColumnInfo(name = "Color")
    public int color;

    public float position;

    public Period(String title, String room, String type, String startHour, String endHour, int periodId, int color) {
        this.periodId = periodId;
        this.title = title;
        this.room = room;
        this.type = type;
        this.startHour = startHour;
        this.endHour = endHour;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public String getRoom() {
        return room;
    }

    public String getType() {
        return type;
    }

    public String getStartHour() {
        return startHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public int getColor() {
        return color;
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Period{" +
                "id=" + periodId +
                ", title='" + title + '\'' +
                ", room='" + room + '\'' +
                ", type='" + type + '\'' +
                ", startHour='" + startHour + '\'' +
                ", endHour='" + endHour + '\'' +
                "}\n";
    }

    @Override
    public int compareTo(Period o) {
        String start1 = this.startHour.replace("T", "").replace("Z", "");
        String start2 = o.startHour.replace("T", "").replace("Z", "");

        return Long.compare(Long.parseLong(start1), Long.parseLong(start2));
    }
}