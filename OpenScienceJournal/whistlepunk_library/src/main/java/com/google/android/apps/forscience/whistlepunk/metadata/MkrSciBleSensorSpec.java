package com.google.android.apps.forscience.whistlepunk.metadata;

import android.util.Log;

import com.google.android.apps.forscience.whistlepunk.BuiltInSensorAppearance;
import com.google.android.apps.forscience.whistlepunk.R;
import com.google.android.apps.forscience.whistlepunk.SensorAnimationBehavior;
import com.google.android.apps.forscience.whistlepunk.SensorAppearance;
import com.google.android.apps.forscience.whistlepunk.data.GoosciMkrSciSensorConfig;
import com.google.android.apps.forscience.whistlepunk.devicemanager.SensorTypeProvider;
import com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor;
import com.google.common.annotations.VisibleForTesting;

import java.util.Objects;

public class MkrSciBleSensorSpec extends ExternalSensorSpec {
    private static final String TAG = "MkrSciBleSensorSpec";
    public static final String TYPE = "mkrsci_bluetooth_le";

    private String mName;

    private GoosciMkrSciSensorConfig.MkrSciBleSensorConfig mConfig = new GoosciMkrSciSensorConfig.MkrSciBleSensorConfig();

    public MkrSciBleSensorSpec(String address, String sensor, String name) {
        mName = name;
        mConfig.address = address;
        mConfig.sensor = sensor;
        mConfig.handler = "";
    }

    public MkrSciBleSensorSpec(String name, byte[] config) {
        mName = name;
        loadFromConfig(config);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getAddress() {
        return mConfig.address;
    }

    @Override
    public String getName() {
        return mName;
    }

    public String getSensor() {
        return mConfig.sensor;
    }

    public void setHandler(String handler) {
        mConfig.handler = handler;
    }

    public String getHandler() {
        return mConfig.handler;
    }

    @Override
    public SensorAppearance getSensorAppearance() {
        // TODO h42 getSensorAppearance
        if (Objects.equals(mConfig.sensor, MkrSciBleSensor.SENSOR_INPUT_1)) {
            return BuiltInSensorAppearance.create(
                    R.string.input1, // name
                    R.drawable.ic_sensor_input_1_white_24dp, // icon
                    0, // units
                    0, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    null, // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, MkrSciBleSensor.SENSOR_INPUT_2)) {
            return BuiltInSensorAppearance.create(
                    R.string.input2, // name
                    R.drawable.ic_sensor_input_2_white_24dp, // icon
                    0, // units
                    0, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    null, // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, MkrSciBleSensor.SENSOR_INPUT_3)) {
            return BuiltInSensorAppearance.create(
                    R.string.input3, // name
                    R.drawable.ic_sensor_input_3_white_24dp, // icon
                    0, // units
                    0, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    null, // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, MkrSciBleSensor.SENSOR_VOLTAGE)) {
            return BuiltInSensorAppearance.create(
                    R.string.voltage, // name
                    R.drawable.ic_sensor_voltage_white_24dp, // icon
                    0, // units
                    0, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    null, // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, MkrSciBleSensor.SENSOR_CURRENT)) {
            return BuiltInSensorAppearance.create(
                    R.string.current, // name
                    R.drawable.ic_sensor_current_white_24dp, // icon
                    0, // units
                    0, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    null, // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, MkrSciBleSensor.SENSOR_RESISTANCE)) {
            return BuiltInSensorAppearance.create(
                    R.string.resistance, // name
                    R.drawable.ic_sensor_resistance_white_24dp, // icon
                    0, // units
                    0, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    null, // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, MkrSciBleSensor.SENSOR_ACCELEROMETER_X)) {
            return BuiltInSensorAppearance.create(
                    R.string.acc_x, R.drawable.ic_sensor_acc_x_white_24dp, R.string.acc_units,
                    R.string.sensor_desc_short_acc_x, R.string.sensor_desc_first_paragraph_acc,
                    R.string.sensor_desc_second_paragraph_acc, R.drawable.learnmore_acc,
                    new SensorAnimationBehavior(R.drawable.accx_level_drawable,
                            SensorAnimationBehavior.TYPE_ACCELEROMETER_SCALE_ROTATES),
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, null);
        }
        if (Objects.equals(mConfig.sensor, MkrSciBleSensor.SENSOR_ACCELEROMETER_Y)) {
            return BuiltInSensorAppearance.create(
                    R.string.acc_y, R.drawable.ic_sensor_acc_y_white_24dp, R.string.acc_units,
                    R.string.sensor_desc_short_acc_y, R.string.sensor_desc_first_paragraph_acc,
                    R.string.sensor_desc_second_paragraph_acc, R.drawable.learnmore_acc,
                    new SensorAnimationBehavior(R.drawable.accy_level_drawable,
                            SensorAnimationBehavior.TYPE_ACCELEROMETER_SCALE_ROTATES),
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, null);
        }
        if (Objects.equals(mConfig.sensor, MkrSciBleSensor.SENSOR_ACCELEROMETER_Z)) {
            return BuiltInSensorAppearance.create(
                    R.string.acc_z, R.drawable.ic_sensor_acc_z_white_24dp, R.string.acc_units,
                    R.string.sensor_desc_short_acc_z, R.string.sensor_desc_first_paragraph_acc,
                    R.string.sensor_desc_second_paragraph_acc, R.drawable.learnmore_acc,
                    new SensorAnimationBehavior(R.drawable.accz_level_drawable,
                            SensorAnimationBehavior.TYPE_ACCELEROMETER_SCALE_ROTATES),
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, null);
        }
        if (Objects.equals(mConfig.sensor, MkrSciBleSensor.SENSOR_GYROSCOPE_X)) {
            return BuiltInSensorAppearance.create(
                    R.string.gyr_x, // name
                    R.drawable.ic_sensor_gyr_x_white_24dp, // icon
                    0, // units
                    0, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    null, // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, MkrSciBleSensor.SENSOR_GYROSCOPE_Y)) {
            return BuiltInSensorAppearance.create(
                    R.string.gyr_y, // name
                    R.drawable.ic_sensor_gyr_y_white_24dp, // icon
                    0, // units
                    0, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    null, // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, MkrSciBleSensor.SENSOR_GYROSCOPE_Z)) {
            return BuiltInSensorAppearance.create(
                    R.string.gyr_z, // name
                    R.drawable.ic_sensor_gyr_z_white_24dp, // icon
                    0, // units
                    0, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    null, // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, MkrSciBleSensor.SENSOR_MAGNETOMETER)) {
            return BuiltInSensorAppearance.create(
                    R.string.magnetic_field_strength,
                    R.drawable.ic_sensor_magnet_white_24dp,
                    R.string.magnetic_strength_units,
                    0,
                    0,
                    0,
                    0,
                    new SensorAnimationBehavior(
                            R.drawable.magnetometer_level_drawable,
                            SensorAnimationBehavior.TYPE_POSITIVE_RELATIVE_SCALE),
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, null);
        }
        return SensorTypeProvider.getSensorAppearance(SensorTypeProvider.TYPE_CUSTOM, mName);
    }

    @Override
    public byte[] getConfig() {
        return getBytes(mConfig);
    }

    @Override
    public boolean shouldShowOptionsOnConnect() {
        return true;
    }

    @VisibleForTesting
    public void loadFromConfig(byte[] data) {
        try {
            mConfig = GoosciMkrSciSensorConfig.MkrSciBleSensorConfig.parseFrom(data);
        } catch (Throwable e) {
            Log.e(TAG, "Could not deserialize config", e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean isSameSensor(ExternalSensorSpec spec) {
        if (spec instanceof MkrSciBleSensorSpec) {
            MkrSciBleSensorSpec aux = (MkrSciBleSensorSpec) spec;
            return Objects.equals(aux.mConfig.address, mConfig.address)
                    && Objects.equals(aux.mConfig.sensor, mConfig.sensor);
        }
        return false;
    }

}
