package com.jules.takemehomecountrytable.PeriodsDatabase;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jules.takemehomecountrytable.PeriodsDatabase.DAO.PeriodDao;
import com.jules.takemehomecountrytable.PeriodsDatabase.DAO.TeacherDao;
import com.jules.takemehomecountrytable.PeriodsDatabase.DAO.TutorDao;
import com.jules.takemehomecountrytable.PeriodsDatabase.Entities.Period;
import com.jules.takemehomecountrytable.PeriodsDatabase.Entities.Teacher;
import com.jules.takemehomecountrytable.PeriodsDatabase.Entities.Tutor;

@androidx.room.Database(entities = {Period.class, Teacher.class, Tutor.class}, version = 1)
public abstract class PeriodsDatabase extends RoomDatabase {
    private static PeriodsDatabase instance;
    private static final String DB_NAME = "periods_db";

    public abstract PeriodDao getPeriodDao();

    public abstract TeacherDao getTeacherDao();

    public abstract TutorDao getTutorDao();

    public static synchronized PeriodsDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), PeriodsDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }
}
