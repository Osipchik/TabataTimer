package com.example.lab2.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.lab2.Models.TimerModel;

@Database(entities = {TimerModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TimerDao timerDao();
}
