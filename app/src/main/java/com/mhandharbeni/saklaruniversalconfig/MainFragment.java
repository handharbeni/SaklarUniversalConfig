package com.mhandharbeni.saklaruniversalconfig;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.mhandharbeni.saklaruniversalconfig.database.AppDb;
import com.mhandharbeni.saklaruniversalconfig.database.models.Buttons;
import com.mhandharbeni.saklaruniversalconfig.databinding.FragmentMainBinding;
import com.mhandharbeni.saklaruniversalconfig.utils.Constant;
import com.mhandharbeni.saklaruniversalconfig.utils.UtilDialogs;
import com.mhandharbeni.saklaruniversalconfig.utils.UtilNav;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainFragment extends Fragment implements Observer<List<Buttons>> {
    private final String TAG = MainFragment.class.getSimpleName();
    private FragmentMainBinding binding;
    private AppDb appDb;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMainBinding.inflate(inflater, container, false);
        appDb = AppDb.getInstance(requireContext());
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
//        binding.btnTestOn.setOnClickListener(v -> {
//            List<String> lCommand = new ArrayList<>(
//                    Arrays.asList(
//                            "01",
//                            "01",
//                            "01",
//                            "DD"
//                    )
//            );
//            new UtilNav<List<String>>()
//                    .setStateHandle(
//                            NavHostFragment.findNavController(MainFragment.this),
//                            Constant.BLUETOOTH_SEND_COMMAND,
//                            lCommand
//                    );
//        });
//        binding.btnTestOff.setOnClickListener(v -> {
//            List<String> lCommand = new ArrayList<>(
//                    Arrays.asList(
//                            "03",
//                            "01",
//                            "01",
//                            "DB"
//                    )
//            );
//            new UtilNav<List<String>>()
//                    .setStateHandle(
//                            NavHostFragment.findNavController(MainFragment.this),
//                            Constant.BLUETOOTH_SEND_COMMAND,
//                            lCommand);
//        });
        binding.fab.setOnClickListener(v ->
                NavHostFragment
                        .findNavController(MainFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );
        binding.btnPos1.setOnClickListener(v -> binding.txtDisplay.setText(binding.btnPos1.getText().toString()));
        binding.btnPos2.setOnClickListener(v -> binding.txtDisplay.setText(binding.btnPos2.getText().toString()));
        binding.btnPos3.setOnClickListener(v -> binding.txtDisplay.setText(binding.btnPos3.getText().toString()));
        binding.btnPos4.setOnClickListener(v -> binding.txtDisplay.setText(binding.btnPos4.getText().toString()));
        binding.btnPos5.setOnClickListener(v -> binding.txtDisplay.setText(binding.btnPos5.getText().toString()));
        binding.btnPos6.setOnClickListener(v -> binding.txtDisplay.setText(binding.btnPos6.getText().toString()));
        binding.btnPos7.setOnClickListener(v -> binding.txtDisplay.setText(binding.btnPos7.getText().toString()));
        binding.btnPos8.setOnClickListener(v -> binding.txtDisplay.setText(binding.btnPos8.getText().toString()));

        binding.mode1.setOnClickListener(v -> UtilDialogs.showDialog(requireContext(), "Test", "test Message"));

        appDb.buttons().getLiveByMode("1").observe(getViewLifecycleOwner(), this);
        initAdapter();
    }

    void initAdapter() {
        for (Buttons buttons : appDb.buttons().getButtonByMode("1")) {
            Log.d(TAG, "initAdapter: "+buttons.toString());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onChanged(List<Buttons> buttons) {
        for (Buttons button : buttons) {
            Log.d(TAG, "onChanged: "+button.toString());
        }
    }
}