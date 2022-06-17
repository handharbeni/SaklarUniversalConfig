package com.mhandharbeni.saklaruniversalconfig;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
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
    private int iMode = 1;

    LiveData<List<Buttons>> liveData = null;

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

        liveData = appDb.buttons().getLiveByMode("1");

        liveData.observe(getViewLifecycleOwner(), this);

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
        binding.btnPos1.setOnClickListener(this::onButtonClick);
        binding.btnPos2.setOnClickListener(this::onButtonClick);
        binding.btnPos3.setOnClickListener(this::onButtonClick);
        binding.btnPos4.setOnClickListener(this::onButtonClick);
        binding.btnPos5.setOnClickListener(this::onButtonClick);
        binding.btnPos6.setOnClickListener(this::onButtonClick);
        binding.btnPos7.setOnClickListener(this::onButtonClick);
        binding.btnPos8.setOnClickListener(this::onButtonClick);

        binding.mode1.setOnClickListener(v -> {
            iMode = 1;
            changeMode();
        });
        binding.previousMode.setOnClickListener(v -> {
            if (iMode == 1) {
                return;
            }
            iMode--;
            changeMode();
        });
        binding.nextMode.setOnClickListener(v -> {
            if (iMode == 4) {
                return;
            }
            iMode++;
            changeMode();
        });

        setupData();
    }

    void onButtonClick(View view) {
        if (view.getId() == binding.btnPos1.getId()) {
            Buttons buttons = appDb.buttons().getButtonByLabel(binding.btnPos1.getText().toString());
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        } else if (view.getId() == binding.btnPos2.getId()) {
            Buttons buttons = appDb.buttons().getButtonByLabel(binding.btnPos2.getText().toString());
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        } else if (view.getId() == binding.btnPos3.getId()) {
            Buttons buttons = appDb.buttons().getButtonByLabel(binding.btnPos3.getText().toString());
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        } else if (view.getId() == binding.btnPos4.getId()) {
            Buttons buttons = appDb.buttons().getButtonByLabel(binding.btnPos4.getText().toString());
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        } else if (view.getId() == binding.btnPos5.getId()) {
            Buttons buttons = appDb.buttons().getButtonByLabel(binding.btnPos5.getText().toString());
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        } else if (view.getId() == binding.btnPos6.getId()) {
            Buttons buttons = appDb.buttons().getButtonByLabel(binding.btnPos6.getText().toString());
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        } else if (view.getId() == binding.btnPos7.getId()) {
            Buttons buttons = appDb.buttons().getButtonByLabel(binding.btnPos7.getText().toString());
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        } else if (view.getId() == binding.btnPos8.getId()) {
            Buttons buttons = appDb.buttons().getButtonByLabel(binding.btnPos8.getText().toString());
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        }
    }

    void setupData() {
        try {
            List<Buttons> buttons = appDb.buttons().getButtonByMode(String.valueOf(iMode));
            binding.btnPos1.setText(buttons.get(0).getLabel());
            binding.btnPos2.setText(buttons.get(1).getLabel());
            binding.btnPos3.setText(buttons.get(2).getLabel());
            binding.btnPos4.setText(buttons.get(3).getLabel());
            binding.btnPos5.setText(buttons.get(4).getLabel());
            binding.btnPos6.setText(buttons.get(5).getLabel());
            binding.btnPos7.setText(buttons.get(6).getLabel());
            binding.btnPos8.setText(buttons.get(7).getLabel());
        } catch (Exception ignored) {}
    }

    void changeMode() {
        liveData.removeObservers(getViewLifecycleOwner());
        liveData = appDb.buttons().getLiveByMode(String.valueOf(iMode));
        liveData.observe(getViewLifecycleOwner(), this);

        setupData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onChanged(List<Buttons> buttons) {
        setupData();
    }
}