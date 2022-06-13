package com.mhandharbeni.saklaruniversalconfig.utils;

import java.util.ArrayList;
import java.util.List;

public class UtilList<T> {

    @SuppressWarnings("unchecked")
    public List<T> convertObjectToList(Object obj) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List) {
            for (int i = 0; i < ((List<T>) obj).size(); i++) {
                T item = ((List<T>) obj).get(i);
                result.add((T) item);
            }
        }
        return result;
    }
}
