package com.mhandharbeni.saklaruniversalconfig.database.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mhandharbeni.saklaruniversalconfig.database.models.Buttons;

import java.util.List;

@Dao
public interface InterfaceButtons {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Buttons buttons);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Buttons> listButtons);

    @Update
    void update(Buttons buttons);

    @Update
    void update(List<Buttons> listButtons);

    @Delete
    void delete(Buttons buttons);

    @Delete
    void delete(List<Buttons> listButtons);

    @Query("SELECT * FROM buttons")
    List<Buttons> getList();

    @Query("SELECT * FROM buttons")
    LiveData<List<Buttons>> getLive();

    @Query("SELECT * FROM buttons WHERE mode = :value")
    LiveData<List<Buttons>> getLiveByMode(String value);

    @Query("SELECT * FROM buttons WHERE uniqueId = :value")
    Buttons getButtonById(int value);

    @Query("SELECT * FROM buttons WHERE label = :value AND mode = :mode")
    Buttons getButtonByLabel(String value, String mode);

    @Query("SELECT * FROM buttons WHERE position = :value")
    Buttons getButtonByPosition(String value);

    @Query("SELECT * FROM buttons WHERE mode = :value")
    List<Buttons> getButtonByMode(String value);
}
