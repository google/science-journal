package com.google.android.apps.forscience.whistlepunk.devicemanager;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.apps.forscience.javalib.Success;
import com.google.android.apps.forscience.whistlepunk.AppSingleton;
import com.google.android.apps.forscience.whistlepunk.DataController;
import com.google.android.apps.forscience.whistlepunk.LoggingConsumer;
import com.google.android.apps.forscience.whistlepunk.R;
import com.google.android.apps.forscience.whistlepunk.metadata.ExternalSensorSpec;
import com.google.android.apps.forscience.whistlepunk.metadata.MkrSciBleSensorSpec;
import com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor;

import java.util.Objects;

public class MkrSciSensorOptionsDialog extends DialogFragment {

    private static final String TAG = "MkrSciSensorOptionsDialog";

    private static final String KEY_EXPERIMENT_ID = "experiment_id";
    private static final String KEY_SENSOR_ID = "sensor_id";
    private static final String KEY_AVAILABLE_OPTIONS = "available_options";
    private static final String KEY_DEFAULT_OPTION = "def_option";

    private RadioButton[] mRadioButtons;

    private DataController mDataController;

    private MkrSciBleSensorSpec mSensorSpec;

    public static MkrSciSensorOptionsDialog newInstance(
            String experimentId, String sensorId,
            String[] options, int defChecked) {
        Bundle args = new Bundle();
        args.putString(KEY_EXPERIMENT_ID, experimentId);
        args.putString(KEY_SENSOR_ID, sensorId);
        args.putStringArray(KEY_AVAILABLE_OPTIONS, options);
        args.putInt(KEY_DEFAULT_OPTION, defChecked);
        MkrSciSensorOptionsDialog dialog = new MkrSciSensorOptionsDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDataController = AppSingleton.getInstance(getActivity()).getDataController();

        final Bundle args = getArguments();
        final String experimentId = args.getString(KEY_EXPERIMENT_ID);
        final String sensorId = args.getString(KEY_SENSOR_ID);
        final String[] optionIds = args.getStringArray(KEY_AVAILABLE_OPTIONS);
        final int defChecked = args.getInt(KEY_DEFAULT_OPTION);
        assert optionIds != null;

        final int size = optionIds.length;

        final View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.mkrsci_sensor_options_dialog, null);
        final RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        mRadioButtons = new RadioButton[size];

        for (int i = 0; i < size; i++) {
            final String optionId = optionIds[i];
            int labelId;
            switch (optionIds[i]) {
                case MkrSciBleSensor.HANDLER_TEMPERATURE_CELSIUS:
                    labelId = R.string.mkrsci_input_sensor_temperature_c;
                    break;
                case MkrSciBleSensor.HANDLER_TEMPERATURE_FAHRENHEIT:
                    labelId = R.string.mkrsci_input_sensor_temperature_f;
                    break;
                case MkrSciBleSensor.HANDLER_LIGHT:
                    labelId = R.string.mkrsci_input_sensor_light;
                    break;
                default:
                    labelId = R.string.mkrsci_input_sensor_raw;
                    break;
            }
            mRadioButtons[i] = new RadioButton(getActivity());
            mRadioButtons[i].setTag(optionId);
            mRadioButtons[i].setText(labelId);
            mRadioButtons[i].setOnClickListener(view1 -> {
                mSensorSpec.setHandler(optionId);
                saveSensorSpec(experimentId, sensorId);
            });
            radioGroup.addView(mRadioButtons[i], new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
        }

        loadSensorSpec(sensorId, defChecked);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.title_activity_sensor_settings)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, (dialog, i) -> dialog.dismiss());
        return builder.create();
    }

    private void loadSensorSpec(String sensorId, int defChecked) {
        mDataController.getExternalSensorById(sensorId, new LoggingConsumer<ExternalSensorSpec>(TAG,
                "Load external sensor with ID = " + sensorId) {
            @Override
            public void success(ExternalSensorSpec sensor) {
                mSensorSpec = (MkrSciBleSensorSpec) sensor;
                final String handler = mSensorSpec.getHandler();
                int index = defChecked;
                for (int i = 0; i < mRadioButtons.length; i++) {
                    if (Objects.equals(handler, mRadioButtons[i].getTag())) {
                        index = i;
                        break;
                    }
                }
                mRadioButtons[index].setChecked(true);
            }
        });
    }

    private void saveSensorSpec(String experimentId, String sensorId) {
        mDataController.addOrGetExternalSensor(mSensorSpec,
                new LoggingConsumer<String>(TAG, "update external sensor") {
                    @Override
                    public void success(final String newSensorId) {
                        if (!newSensorId.equals(sensorId)) {
                            mDataController.replaceSensorInExperiment(experimentId, sensorId,
                                    newSensorId, new LoggingConsumer<Success>(
                                            TAG, "update experiment") {
                                        @Override
                                        public void success(Success value) {
                                            // ok
                                        }
                                    });
                        }
                    }
                });
    }

}
