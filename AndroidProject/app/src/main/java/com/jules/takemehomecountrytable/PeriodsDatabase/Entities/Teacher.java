package com.jules.takemehomecountrytable.PeriodsDatabase.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "teacher_table", primaryKeys = "last_name")
public class Teacher {
    @ColumnInfo(name = "first_name")
    public String first_name;

    @NonNull
    @ColumnInfo(name = "last_name")
    public String last_name;

    public Teacher(String first_name, @NonNull String last_name) {
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public String getFullName() {
        return last_name + ' ' + first_name;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                '}';
    }
}
