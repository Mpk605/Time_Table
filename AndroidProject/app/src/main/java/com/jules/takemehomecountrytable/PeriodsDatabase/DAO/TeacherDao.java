package com.jules.takemehomecountrytable.PeriodsDatabase.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jules.takemehomecountrytable.PeriodsDatabase.Entities.Teacher;

import java.util.List;

@Dao
public interface TeacherDao {
    @Insert
    void insert(Teacher... teachers);

    @Update
    void update(Teacher... teachers);

    @Delete
    void delete(Teacher... teachers);
}
