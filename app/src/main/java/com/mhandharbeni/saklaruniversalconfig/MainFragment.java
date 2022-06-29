package com.mhandharbeni.saklaruniversalconfig;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
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

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        liveData = appDb.buttons().getLiveByMode(String.valueOf(iMode));

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

        binding.lampMode.setOnClickListener(v -> {
            iMode = 1;
            binding.txtDisplay.setText("Lamp Mode");
            changeMode();
        });
//        binding.onOff.setOnClickListener(v -> {
//            if (iMode == 1) {
//                return;
//            }
//            iMode--;
//            binding.txtDisplay.setText(String.format("MODE %d", iMode));
//            changeMode();
//        });
        binding.sirineMode.setOnClickListener(v -> {
            if (iMode == 4) {
                iMode = 2;
                binding.txtDisplay.setText(String.format("SIREN %d", iMode-1));
                changeMode();
                return;
            }
            iMode++;
            binding.txtDisplay.setText(String.format("SIREN %d", iMode-1));
            changeMode();
        });

        setupData();
    }

    void onButtonClick(View view) {
        Buttons buttons = null;
        MaterialTextView mtView = null;
        if (view.getId() == binding.btnPos1.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.txtBtn1.getText().toString(), String.valueOf(iMode));
            mtView = binding.txtBtn1;
        } else if (view.getId() == binding.btnPos2.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.txtBtn2.getText().toString(), String.valueOf(iMode));
            mtView = binding.txtBtn2;
        } else if (view.getId() == binding.btnPos3.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.txtBtn3.getText().toString(), String.valueOf(iMode));
            mtView = binding.txtBtn3;
        } else if (view.getId() == binding.btnPos4.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.txtBtn4.getText().toString(), String.valueOf(iMode));
            mtView = binding.txtBtn4;
        } else if (view.getId() == binding.btnPos5.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.txtBtn5.getText().toString(), String.valueOf(iMode));
            mtView = binding.txtBtn5;
        } else if (view.getId() == binding.btnPos6.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.txtBtn6.getText().toString(), String.valueOf(iMode));
            mtView = binding.txtBtn6;
        } else if (view.getId() == binding.btnPos7.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.txtBtn7.getText().toString(), String.valueOf(iMode));
            mtView = binding.txtBtn7;
        } else if (view.getId() == binding.btnPos8.getId()) {
            buttons = appDb.buttons().getButtonByLabel(binding.txtBtn8.getText().toString(), String.valueOf(iMode));
            mtView = binding.txtBtn8;
        }
        if (mtView != null && buttons != null) {
            if (iMode == 1) {
                showDataLamp(mtView, buttons);
            } else {
                showDataRelay(mtView, buttons);
            }
        }
    }

    void showDataRelay(MaterialTextView materialTextView, Buttons buttons) {

        materialTextView.setBackgroundColor(requireContext().getResources().getColor(R.color.color_2));
    }

    void showDataLamp(MaterialTextView materialTextView, Buttons buttons) {
        materialTextView.setBackgroundColor(requireContext().getResources().getColor(R.color.color_2));

        @SuppressLint("InflateParams") View view = LayoutInflater.from(requireContext()).inflate(R.layout.relay_form, null, false);
        TextInputLayout edtRelayName = view.findViewById(R.id.edtRelayName);
        RadioGroup radioGroup = view.findViewById(R.id.rgType);
        MaterialRadioButton btnToggle = view.findViewById(R.id.btnToggle);
        MaterialRadioButton btnPushOn = view.findViewById(R.id.btnPushOn);
        MaterialRadioButton btnGroup = view.findViewById(R.id.btnGroup);

        switch (buttons.getType()) {
            case 1 :
                btnToggle.setChecked(true);
                break;
            case 2 :
                btnPushOn.setChecked(true);
                break;
            case 3 :
                btnGroup.setChecked(true);
                break;
        }

        Objects.requireNonNull(edtRelayName.getEditText()).setText(buttons.getLabel());

        UtilDialogs.showDialog(requireContext(), view, "Save", new UtilDialogs.DialogCallbacks() {
            @Override
            public void onPositiveClick() {
                int type = 1;
                if (radioGroup.getCheckedRadioButtonId() == R.id.btnPushOn) {
                    type = 2;
                } else if (radioGroup.getCheckedRadioButtonId() == R.id.btnGroup){
                    type = 3;
                }
                buttons.setLabel(edtRelayName.getEditText().getText().toString());
                buttons.setType(type);
                appDb.buttons().insert(buttons);

                materialTextView.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
            }

            @Override
            public void onNegativeClick() {}

            @Override
            public void onCancel() {
                materialTextView.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
            }
        });
    }

    @SuppressLint("SetTextI18n")
    void setupData() {
        try {
            List<Buttons> buttons = appDb.buttons().getButtonByMode(String.valueOf(iMode));
            for (int i = 0; i < buttons.size(); i++) {
                Buttons btns = buttons.get(i);
                switch (i) {
                    case 0 :
                        binding.txtBtn1.setText(btns.getLabel());
                        if (btns.getStatus()==1) {
                            binding.txtBtn1.setBackgroundColor(requireContext().getResources().getColor(R.color.color_2));
                        } else {
                            binding.txtBtn1.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
                        }
                        break;
                    case 1 :
                        binding.txtBtn2.setText(btns.getLabel());
                        if (btns.getStatus()==1) {
                            binding.txtBtn2.setBackgroundColor(requireContext().getResources().getColor(R.color.color_2));
                        } else {
                            binding.txtBtn2.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
                        }
                        break;
                    case 2 :
                        binding.txtBtn3.setText(btns.getLabel());
                        if (btns.getStatus()==1) {
                            binding.txtBtn3.setBackgroundColor(requireContext().getResources().getColor(R.color.color_2));
                        } else {
                            binding.txtBtn3.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
                        }
                        break;
                    case 3 :
                        if (iMode > 1) {
                            binding.txtBtn4.setText("HF1");
                        } else {
                            binding.txtBtn4.setText(btns.getLabel());
                        }
                        if (btns.getStatus()==1) {
                            binding.txtBtn4.setBackgroundColor(requireContext().getResources().getColor(R.color.color_2));
                        } else {
                            binding.txtBtn4.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
                        }
                        break;
                    case 4 :
                        binding.txtBtn5.setText(btns.getLabel());
                        if (btns.getStatus()==1) {
                            binding.txtBtn5.setBackgroundColor(requireContext().getResources().getColor(R.color.color_2));
                        } else {
                            binding.txtBtn5.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
                        }
                        break;
                    case 5 :
                        binding.txtBtn6.setText(btns.getLabel());
                        if (btns.getStatus()==1) {
                            binding.txtBtn6.setBackgroundColor(requireContext().getResources().getColor(R.color.color_2));
                        } else {
                            binding.txtBtn6.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
                        }
                        break;
                    case 6 :
                        binding.txtBtn7.setText(btns.getLabel());
                        if (btns.getStatus()==1) {
                            binding.txtBtn7.setBackgroundColor(requireContext().getResources().getColor(R.color.color_2));
                        } else {
                            binding.txtBtn7.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
                        }
                        break;
                    case 7 :
                        if (iMode > 1) {
                            binding.txtBtn8.setText("HF2");
                        } else {
                            binding.txtBtn8.setText(btns.getLabel());
                        }
                        if (btns.getStatus()==1) {
                            binding.txtBtn8.setBackgroundColor(requireContext().getResources().getColor(R.color.color_2));
                        } else {
                            binding.txtBtn8.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
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