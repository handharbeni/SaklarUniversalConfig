package com.mhandharbeni.saklaruniversalconfig;

import android.annotation.SuppressLint;
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

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
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

@SuppressLint("DefaultLocale")
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
                        .navigate(R.id.action_SaklarConfig_to_BluetoothDevice)
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
            binding.txtDisplay.setText(String.format("MODE %d", iMode));
            changeMode();
        });
        binding.previousMode.setOnClickListener(v -> {
            if (iMode == 1) {
                return;
            }
            iMode--;
            binding.txtDisplay.setText(String.format("MODE %d", iMode));
            changeMode();
        });
        binding.nextMode.setOnClickListener(v -> {
            if (iMode == 4) {
                return;
            }
            iMode++;
            binding.txtDisplay.setText(String.format("MODE %d", iMode));
            changeMode();
        });

        setupData();
    }

    void onButtonClick(View view) {
        Buttons buttons = null;
        if (view.getId() == binding.btnPos1.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.btnPos1.getText().toString(), String.valueOf(iMode));
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        } else if (view.getId() == binding.btnPos2.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.btnPos2.getText().toString(), String.valueOf(iMode));
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        } else if (view.getId() == binding.btnPos3.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.btnPos3.getText().toString(), String.valueOf(iMode));
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        } else if (view.getId() == binding.btnPos4.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.btnPos4.getText().toString(), String.valueOf(iMode));
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        } else if (view.getId() == binding.btnPos5.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.btnPos5.getText().toString(), String.valueOf(iMode));
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        } else if (view.getId() == binding.btnPos6.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.btnPos6.getText().toString(), String.valueOf(iMode));
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        } else if (view.getId() == binding.btnPos7.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.btnPos7.getText().toString(), String.valueOf(iMode));
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        } else if (view.getId() == binding.btnPos8.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.btnPos8.getText().toString(), String.valueOf(iMode));
            Log.d(TAG, "onButtonClick: "+buttons.toString());
        }
        if (buttons != null) {
            showData(buttons);
        }
    }

    void showData(Buttons buttons) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(requireContext()).inflate(R.layout.relay_form, null, false);
        TextInputLayout edtRelayName = view.findViewById(R.id.edtRelayName);
        SwitchMaterial switchToggle = view.findViewById(R.id.switchToggle);

        Objects.requireNonNull(edtRelayName.getEditText()).setText(buttons.getLabel());
        switchToggle.setChecked(buttons.getType()==0);

        UtilDialogs.showDialog(requireContext(), view, "Save", new UtilDialogs.DialogCallbacks() {
            @Override
            public void onPositiveClick() {

            }

            @Override
            public void onNegativeClick() {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    void setupData() {
        try {
            List<Buttons> buttons = appDb.buttons().getButtonByMode(String.valueOf(iMode));
            for (int i = 0; i < buttons.size(); i++) {
                Buttons btns = buttons.get(i);
                switch (i) {
                    case 0 :
                        binding.btnPos1.setText(btns.getLabel());
                        if (btns.getStatus()==0) {
                            binding.btnPos1.setTextColor(requireContext().getResources().getColor(R.color.teal_700));
                        } else {
                            binding.btnPos1.setTextColor(requireContext().getResources().getColor(R.color.white));
                        }
                        break;
                    case 1 :
                        binding.btnPos2.setText(btns.getLabel());
                        if (btns.getStatus()==1) {
                            binding.btnPos2.setTextColor(requireContext().getResources().getColor(R.color.teal_700));
                        } else {
                            binding.btnPos2.setTextColor(requireContext().getResources().getColor(R.color.white));
                        }
                        break;
                    case 2 :
                        binding.btnPos3.setText(btns.getLabel());
                        if (btns.getStatus()==1) {
                            binding.btnPos3.setTextColor(requireContext().getResources().getColor(R.color.teal_700));
                        } else {
                            binding.btnPos3.setTextColor(requireContext().getResources().getColor(R.color.white));
                        }
                        break;
                    case 3 :
                        binding.btnPos4.setText(btns.getLabel());
                        if (btns.getStatus()==1) {
                            binding.btnPos4.setTextColor(requireContext().getResources().getColor(R.color.teal_700));
                        } else {
                            binding.btnPos4.setTextColor(requireContext().getResources().getColor(R.color.white));
                        }
                        break;
                    case 4 :
                        binding.btnPos5.setText(btns.getLabel());
                        if (btns.getStatus()==1) {
                            binding.btnPos5.setTextColor(requireContext().getResources().getColor(R.color.teal_700));
                        } else {
                            binding.btnPos5.setTextColor(requireContext().getResources().getColor(R.color.white));
                        }
                        break;
                    case 5 :
                        binding.btnPos6.setText(btns.getLabel());
                        if (btns.getStatus()==1) {
                            binding.btnPos6.setTextColor(requireContext().getResources().getColor(R.color.teal_700));
                        } else {
                            binding.btnPos6.setTextColor(requireContext().getResources().getColor(R.color.white));
                        }
                        break;
                    case 6 :
                        binding.btnPos7.setText(btns.getLabel());
                        if (btns.getStatus()==1) {
                            binding.btnPos7.setTextColor(requireContext().getResources().getColor(R.color.teal_700));
                        } else {
                            binding.btnPos7.setTextColor(requireContext().getResources().getColor(R.color.white));
                        }
                        break;
                    case 7 :
                        binding.btnPos8.setText(btns.getLabel());
                        if (btns.getStatus()==1) {
                            binding.btnPos8.setTextColor(requireContext().getResources().getColor(R.color.teal_700));
                        } else {
                            binding.btnPos8.setTextColor(requireContext().getResources().getColor(R.color.white));
                        }
                        break;
                }
            }
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