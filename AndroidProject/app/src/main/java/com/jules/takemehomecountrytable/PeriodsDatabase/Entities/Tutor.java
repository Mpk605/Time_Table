package com.jules.takemehomecountrytable.PeriodsDatabase.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "tutor_table",
        primaryKeys = {"pid", "teacher_last_name"},
        foreignKeys = {
                @ForeignKey(entity = Period.class,
                        parentColumns = "periodId",
                        childColumns = "pid"),
                @ForeignKey(entity = Teacher.class,
                        parentColumns = "last_name",
                        childColumns = "teacher_last_name")
        }, indices = {@Index("pid"), @Index("teacher_last_name")})
public class Tutor {
    @ColumnInfo(name = "pid")
    public int pid;

    @NonNull
    @ColumnInfo(name = "teacher_last_name")
    public String lastName;

    public Tutor(int pid, @NonNull String lastName) {
        this.pid = pid;
        this.lastName = lastName;
    }
}
