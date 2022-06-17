package com.mhandharbeni.saklaruniversalconfig.database;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.mhandharbeni.saklaruniversalconfig.database.helpers.DataConverter;
import com.mhandharbeni.saklaruniversalconfig.database.helpers.DateConverter;
import com.mhandharbeni.saklaruniversalconfig.database.interfaces.InterfaceButtons;
import com.mhandharbeni.saklaruniversalconfig.database.migrations.Migrations;
import com.mhandharbeni.saklaruniversalconfig.database.models.Buttons;
import com.mhandharbeni.saklaruniversalconfig.utils.Constant;

@TypeConverters({
        DateConverter.class,
        DataConverter.class
})
@Database(
        entities = {
                Buttons.class
        },
        version = Constant.DB_VERSION,
        exportSchema = Constant.DB_EXPORT
)
public abstract class AppDb extends RoomDatabase {
    public abstract InterfaceButtons buttons();

    private static volatile AppDb INSTANCE;

    public static AppDb getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDb.class,
                                    Constant.DB_NAME
                            )
                            .addMigrations(
                                    Migrations.MIGRATION_0_1
                            )
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}