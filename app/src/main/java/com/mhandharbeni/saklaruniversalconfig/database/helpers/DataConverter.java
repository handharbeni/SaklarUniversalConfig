package com.mhandharbeni.saklaruniversalconfig.database.helpers;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class DataConverter {
    private static Gson gson = new Gson();

    @TypeConverter
    public static List<Object> toList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Object>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String toString(List<Object> someObjects) {
        return gson.toJson(someObjects);
    }
}
