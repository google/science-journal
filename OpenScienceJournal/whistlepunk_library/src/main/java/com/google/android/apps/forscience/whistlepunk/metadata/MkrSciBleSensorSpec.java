package com.google.android.apps.forscience.whistlepunk.metadata;

import android.util.Log;

import com.google.android.apps.forscience.whistlepunk.BuiltInSensorAppearance;
import com.google.android.apps.forscience.whistlepunk.R;
import com.google.android.apps.forscience.whistlepunk.SensorAnimationBehavior;
import com.google.android.apps.forscience.whistlepunk.SensorAppearance;
import com.google.android.apps.forscience.whistlepunk.data.GoosciMkrSciSensorConfig;
import com.google.android.apps.forscience.whistlepunk.devicemanager.SensorTypeProvider;
import com.google.common.annotations.VisibleForTesting;

import java.util.Objects;

import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.HANDLER_LIGHT;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.HANDLER_TEMPERATURE_CELSIUS;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.HANDLER_TEMPERATURE_FAHRENHEIT;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.SENSOR_ACCELEROMETER_X;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.SENSOR_ACCELEROMETER_Y;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.SENSOR_ACCELEROMETER_Z;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.SENSOR_CURRENT;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.SENSOR_GYROSCOPE_X;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.SENSOR_GYROSCOPE_Y;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.SENSOR_GYROSCOPE_Z;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.SENSOR_INPUT_1;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.SENSOR_INPUT_2;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.SENSOR_INPUT_3;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.SENSOR_MAGNETOMETER;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.SENSOR_RESISTANCE;
import static com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor.SENSOR_VOLTAGE;

public class MkrSciBleSensorSpec extends ExternalSensorSpec {
    private static final String TAG = "MkrSciBleSensorSpec";
    public static final String TYPE = MkrSciBleDeviceSpec.TYPE;

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
        if (Objects.equals(mConfig.sensor, SENSOR_INPUT_1)) {
            if (Objects.equals(mConfig.handler, HANDLER_TEMPERATURE_CELSIUS)) {
                return BuiltInSensorAppearance.create(
                        R.string.input_1, // name
                        R.drawable.ic_sensor_temperature_white_24dp, // icon
                        R.string.temperature_c_units, // units
                        0, // desc short
                        0, // desc extended 1st part
                        0, // desc extended 2nd part
                        0,// desc extended image
                        new SensorAnimationBehavior(R.drawable.mkrsci_temperature_level_drawable,
                                SensorAnimationBehavior.TYPE_RELATIVE_SCALE), // animation
                        BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                        null // sensor id
                );
            }
            if (Objects.equals(mConfig.handler, HANDLER_TEMPERATURE_FAHRENHEIT)) {
                return BuiltInSensorAppearance.create(
                        R.string.input_1, // name
                        R.drawable.ic_sensor_temperature_white_24dp, // icon
                        R.string.temperature_f_units, // units
                        0, // desc short
                        0, // desc extended 1st part
                        0, // desc extended 2nd part
                        0,// desc extended image
                        new SensorAnimationBehavior(R.drawable.mkrsci_temperature_level_drawable,
                                SensorAnimationBehavior.TYPE_RELATIVE_SCALE), // animation
                        BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                        null // sensor id
                );
            }
            if (Objects.equals(mConfig.handler, HANDLER_LIGHT)) {
                return BuiltInSensorAppearance.create(
                        R.string.input_1, // name
                        R.drawable.ic_sensor_light_white_24dp, // icon
                        R.string.ambient_light_units, // units
                        0, // desc short
                        0, // desc extended 1st part
                        0, // desc extended 2nd part
                        0,// desc extended image
                        new SensorAnimationBehavior(
                                R.drawable.ambient_level_drawable,
                                SensorAnimationBehavior.TYPE_RELATIVE_SCALE),
                        BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                        null // sensor id
                );
            }
            return BuiltInSensorAppearance.create(
                    R.string.input_1, // name
                    R.drawable.ic_sensor_input_1_white_24dp, // icon
                    0, // units
                    0, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    new SensorAnimationBehavior(R.drawable.mkrsci_level_drawable,
                            SensorAnimationBehavior.TYPE_RELATIVE_SCALE), // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, SENSOR_INPUT_2)) {
            if (Objects.equals(mConfig.handler, HANDLER_LIGHT)) {
                return BuiltInSensorAppearance.create(
                        R.string.input_2, // name
                        R.drawable.ic_sensor_light_white_24dp, // icon
                        R.string.ambient_light_units, // units
                        0, // desc short
                        0, // desc extended 1st part
                        0, // desc extended 2nd part
                        0,// desc extended image
                        new SensorAnimationBehavior(
                                R.drawable.ambient_level_drawable,
                                SensorAnimationBehavior.TYPE_RELATIVE_SCALE),
                        BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                        null // sensor id
                );
            }
            return BuiltInSensorAppearance.create(
                    R.string.input_2, // name
                    R.drawable.ic_sensor_input_2_white_24dp, // icon
                    0, // units
                    0, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    new SensorAnimationBehavior(R.drawable.mkrsci_level_drawable,
                            SensorAnimationBehavior.TYPE_RELATIVE_SCALE), // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, SENSOR_INPUT_3)) {
            return BuiltInSensorAppearance.create(
                    R.string.input_3, // name
                    R.drawable.ic_sensor_input_3_white_24dp, // icon
                    0, // units
                    0, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    new SensorAnimationBehavior(R.drawable.mkrsci_level_drawable,
                            SensorAnimationBehavior.TYPE_RELATIVE_SCALE), // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, SENSOR_VOLTAGE)) {
            return BuiltInSensorAppearance.create(
                    R.string.voltage, // name
                    R.drawable.ic_sensor_voltage_white_24dp, // icon
                    R.string.voltage_units, // units
                    R.string.sensor_desc_short_mkrsci_voltage, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    new SensorAnimationBehavior(R.drawable.mkrsci_level_drawable,
                            SensorAnimationBehavior.TYPE_RELATIVE_SCALE), // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, SENSOR_CURRENT)) {
            return BuiltInSensorAppearance.create(
                    R.string.current, // name
                    R.drawable.ic_sensor_current_white_24dp, // icon
                    R.string.current_units, // units
                    R.string.sensor_desc_short_mkrsci_current, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    new SensorAnimationBehavior(R.drawable.mkrsci_level_drawable,
                            SensorAnimationBehavior.TYPE_RELATIVE_SCALE), // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, SENSOR_RESISTANCE)) {
            return BuiltInSensorAppearance.create(
                    R.string.resistance, // name
                    R.drawable.ic_sensor_resistance_white_24dp, // icon
                    R.string.resistance_units, // units
                    R.string.sensor_desc_short_mkrsci_resistance, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    new SensorAnimationBehavior(R.drawable.mkrsci_level_drawable,
                            SensorAnimationBehavior.TYPE_RELATIVE_SCALE), // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, SENSOR_ACCELEROMETER_X)) {
            return BuiltInSensorAppearance.create(
                    R.string.acc_x,
                    R.drawable.ic_sensor_acc_x_white_24dp,
                    R.string.acc_units,
                    R.string.sensor_desc_short_mkrsci_acc,
                    0,
                    0,
                    0,
                    new SensorAnimationBehavior(R.drawable.mkrsci_accx_level_drawable,
                            SensorAnimationBehavior.TYPE_ACCELEROMETER_SCALE_ROTATES),
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL,
                    null
            );
        }
        if (Objects.equals(mConfig.sensor, SENSOR_ACCELEROMETER_Y)) {
            return BuiltInSensorAppearance.create(
                    R.string.acc_y,
                    R.drawable.ic_sensor_acc_y_white_24dp,
                    R.string.acc_units,
                    R.string.sensor_desc_short_mkrsci_acc,
                    0,
                    0,
                    0,
                    new SensorAnimationBehavior(R.drawable.mkrsci_accy_level_drawable,
                            SensorAnimationBehavior.TYPE_ACCELEROMETER_SCALE_ROTATES),
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL,
                    null
            );
        }
        if (Objects.equals(mConfig.sensor, SENSOR_ACCELEROMETER_Z)) {
            return BuiltInSensorAppearance.create(
                    R.string.acc_z,
                    R.drawable.ic_sensor_acc_z_white_24dp,
                    R.string.acc_units,
                    R.string.sensor_desc_short_mkrsci_acc,
                    0,
                    0,
                    0,
                    new SensorAnimationBehavior(R.drawable.mkrsci_accy_level_drawable,
                            SensorAnimationBehavior.TYPE_ACCELEROMETER_SCALE_ROTATES),
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL,
                    null
            );
        }
        if (Objects.equals(mConfig.sensor, SENSOR_GYROSCOPE_X)) {
            return BuiltInSensorAppearance.create(
                    R.string.gyr_x, // name
                    R.drawable.ic_sensor_gyr_x_white_24dp, // icon
                    R.string.gyr_units, // units
                    R.string.sensor_desc_short_mkrsci_gyr, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    new SensorAnimationBehavior(R.drawable.mkrsci_level_drawable,
                            SensorAnimationBehavior.TYPE_RELATIVE_SCALE), // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, SENSOR_GYROSCOPE_Y)) {
            return BuiltInSensorAppearance.create(
                    R.string.gyr_y, // name
                    R.drawable.ic_sensor_gyr_y_white_24dp, // icon
                    R.string.gyr_units, // units
                    R.string.sensor_desc_short_mkrsci_gyr, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    new SensorAnimationBehavior(R.drawable.mkrsci_level_drawable,
                            SensorAnimationBehavior.TYPE_RELATIVE_SCALE), // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, SENSOR_GYROSCOPE_Z)) {
            return BuiltInSensorAppearance.create(
                    R.string.gyr_z, // name
                    R.drawable.ic_sensor_gyr_z_white_24dp, // icon
                    R.string.gyr_units, // units
                    R.string.sensor_desc_short_mkrsci_gyr, // desc short
                    0, // desc extended 1st part
                    0, // desc extended 2nd part
                    0,// desc extended image
                    new SensorAnimationBehavior(R.drawable.mkrsci_level_drawable,
                            SensorAnimationBehavior.TYPE_RELATIVE_SCALE), // animation
                    BuiltInSensorAppearance.DEFAULT_POINTS_AFTER_DECIMAL, // points after decimal
                    null // sensor id
            );
        }
        if (Objects.equals(mConfig.sensor, SENSOR_MAGNETOMETER)) {
            return BuiltInSensorAppearance.create(
                    R.string.magnetic_field_strength,
                    R.drawable.ic_sensor_magnet_white_24dp,
                    R.string.magnetic_strength_units,
                    R.string.sensor_desc_short_mkrsci_magnetometer, // desc short
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
