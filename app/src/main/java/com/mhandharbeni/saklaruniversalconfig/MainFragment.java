package com.mhandharbeni.saklaruniversalconfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.mhandharbeni.saklaruniversalconfig.databinding.FragmentMainBinding;
import com.mhandharbeni.saklaruniversalconfig.utils.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Objects.requireNonNull(
                        NavHostFragment
                                .findNavController(MainFragment.this)
                                .getCurrentBackStackEntry()
                )
                .getSavedStateHandle()
                .getLiveData(Constant.BLUETOOTH_CONNECTED)
                .observe(getViewLifecycleOwner(), o -> {
                    try {
                        if ((boolean) o) {
                            binding.fab.setImageResource(R.drawable.ic_bluetooth_connected_black);
                        } else {
                            binding.fab.setImageResource(R.drawable.ic_bluetooth_black);
                        }
                    } catch (Exception ignored) {
                    }
                });

        if (MainActivity.bluetoothConnected) {
            binding.fab.setImageResource(R.drawable.ic_bluetooth_connected_black);
        } else {
            binding.fab.setImageResource(R.drawable.ic_bluetooth_black);
        }
        binding.btnTestOn.setOnClickListener(v -> {
            List<String> lCommand = new ArrayList<>(
                    Arrays.asList(
                            "01",
                            "01",
                            "01",
                            "DD"
                    )
            );
            Objects.requireNonNull(
                    NavHostFragment
                            .findNavController(MainFragment.this)
                            .getCurrentBackStackEntry()
            ).getSavedStateHandle().set(Constant.BLUETOOTH_SEND_COMMAND, lCommand);
        });
        binding.btnTestOff.setOnClickListener(v -> {
            List<String> lCommand = new ArrayList<>(
                    Arrays.asList(
                            "03",
                            "01",
                            "01",
                            "DB"
                    )
            );
            Objects.requireNonNull(
                    NavHostFragment
                            .findNavController(MainFragment.this)
                            .getCurrentBackStackEntry()
            ).getSavedStateHandle().set(Constant.BLUETOOTH_SEND_COMMAND, lCommand);
        });
        binding.fab.setOnClickListener(v ->
                NavHostFragment
                        .findNavController(MainFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}