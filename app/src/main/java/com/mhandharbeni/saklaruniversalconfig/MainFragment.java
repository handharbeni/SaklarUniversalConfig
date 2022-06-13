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
import com.mhandharbeni.saklaruniversalconfig.utils.UtilNav;

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
        new UtilNav<Boolean>()
                .observeValue(
                        NavHostFragment.findNavController(MainFragment.this),
                        getViewLifecycleOwner(),
                        Constant.BLUETOOTH_CONNECTED,
                        o -> {
                            try {
                                if (o) {
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
            new UtilNav<List<String>>()
                    .setStateHandle(
                            NavHostFragment.findNavController(MainFragment.this),
                            Constant.BLUETOOTH_SEND_COMMAND,
                            lCommand
                    );
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
            new UtilNav<List<String>>()
                    .setStateHandle(
                            NavHostFragment.findNavController(MainFragment.this),
                            Constant.BLUETOOTH_SEND_COMMAND,
                            lCommand);
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