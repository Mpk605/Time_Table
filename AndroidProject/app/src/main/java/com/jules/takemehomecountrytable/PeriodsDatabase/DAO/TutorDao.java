package com.jules.takemehomecountrytable.PeriodsDatabase.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.jules.takemehomecountrytable.PeriodsDatabase.Entities.Teacher;
import com.jules.takemehomecountrytable.PeriodsDatabase.Entities.Tutor;

import java.util.List;

@Dao
public interface TutorDao {
    @Insert
    void insert(Tutor periodTeacher);

//    @Query("SELECT * FROM period_table INNER JOIN tutor_table ON period_table.periodId = tutor_table.pid WHERE tutor_table.=:firstName AND tutor_table.teacher_last_name=:lastName")
//    List<Period> getPeriodFromTeacher(String firstName, String lastName);

    @Query("SELECT * FROM teacher_table INNER JOIN tutor_table ON teacher_table.last_name = tutor_table.teacher_last_name WHERE tutor_table.pid=:pid")
    List<Teacher> getTeachersForPeriod(int pid);
}
