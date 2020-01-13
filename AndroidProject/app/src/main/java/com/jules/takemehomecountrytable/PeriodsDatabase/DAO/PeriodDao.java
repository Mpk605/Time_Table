package com.jules.takemehomecountrytable.PeriodsDatabase.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.jules.takemehomecountrytable.PeriodsDatabase.Entities.Period;

import java.util.List;

@Dao
public interface PeriodDao {
    @Insert
    void insert(Period... periods);

    @Update
    void update(Period... periods);

    @Delete
    void delete(Period... periods);

    @Transaction
    @Query("SELECT * FROM period_table")
    List<Period> getAllPeriods();

    @Query("SELECT * FROM period_table WHERE room=:room")
    List<Period> getPeriodsIn(final String room);

    @Query("SELECT * FROM period_table WHERE period_table.StartHour > :start AND period_table.StartHour < :end ORDER BY period_table.StartHour")
    List<Period> getDaysPeriods(final String start, final String end);

    @Query("SELECT periodId FROM period_table WHERE StartHour = :start AND EndHour = :end AND Room = :room")
    int getId(final String start, final String end, final String room);
}
