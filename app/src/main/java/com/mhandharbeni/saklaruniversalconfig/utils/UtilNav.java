package com.mhandharbeni.saklaruniversalconfig.utils;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;

import java.util.Objects;

public class UtilNav<T> {
    public void setStateHandle(NavController navController, String key, T value) {
        Objects.requireNonNull(
                navController
                        .getCurrentBackStackEntry()
        ).getSavedStateHandle().set(key, value);
    }

    @SuppressWarnings("unchecked")
    public void observeValue(NavController navController, LifecycleOwner lifecycleOwner, String key, Observer<T> observer) {
        Objects.requireNonNull(
                navController
                        .getCurrentBackStackEntry()
        ).getSavedStateHandle().getLiveData(key).observe(lifecycleOwner, (Observer<? super Object>) t -> observer.onChanged((T) t));
    }

    @SuppressWarnings("unchecked")
    public void observeValue(NavController navController, String key, Observer<T> observer) {
        Objects.requireNonNull(
                navController
                        .getCurrentBackStackEntry()
        ).getSavedStateHandle().getLiveData(key).observeForever((Observer<? super Object>) t -> observer.onChanged((T) t));
    }

}
