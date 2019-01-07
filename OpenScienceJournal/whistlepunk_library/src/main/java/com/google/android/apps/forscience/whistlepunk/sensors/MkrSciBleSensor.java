package com.google.android.apps.forscience.whistlepunk.sensors;

import android.content.Context;

import com.google.android.apps.forscience.ble.MkrSciBleManager;
import com.google.android.apps.forscience.whistlepunk.AppSingleton;
import com.google.android.apps.forscience.whistlepunk.Clock;
import com.google.android.apps.forscience.whistlepunk.metadata.MkrSciBleSensorSpec;
import com.google.android.apps.forscience.whistlepunk.sensorapi.AbstractSensorRecorder;
import com.google.android.apps.forscience.whistlepunk.sensorapi.ScalarSensor;
import com.google.android.apps.forscience.whistlepunk.sensorapi.SensorEnvironment;
import com.google.android.apps.forscience.whistlepunk.sensorapi.SensorRecorder;
import com.google.android.apps.forscience.whistlepunk.sensorapi.SensorStatusListener;
import com.google.android.apps.forscience.whistlepunk.sensorapi.StreamConsumer;

import java.util.Objects;

public class MkrSciBleSensor extends ScalarSensor {

    public static final String SENSOR_INPUT_1 = "input_1";
    public static final String SENSOR_INPUT_2 = "input_2";
    public static final String SENSOR_INPUT_3 = "input_3";
    public static final String SENSOR_VOLTAGE = "voltage";
    public static final String SENSOR_CURRENT = "current";
    public static final String SENSOR_RESISTANCE = "resistance";
    public static final String SENSOR_TEMPERATURE = "temperature";
    public static final String SENSOR_ACCELEROMETER_X = "accelerometer_x";
    public static final String SENSOR_ACCELEROMETER_Y = "accelerometer_y";
    public static final String SENSOR_ACCELEROMETER_Z = "accelerometer_z";
    public static final String SENSOR_GYROSCOPE_X = "gyroscope_x";
    public static final String SENSOR_GYROSCOPE_Y = "gyroscope_y";
    public static final String SENSOR_GYROSCOPE_Z = "gyroscope_z";
    public static final String SENSOR_MAGNETOMETER_X = "magnetometer_x";
    public static final String SENSOR_MAGNETOMETER_Y = "magnetometer_y";
    public static final String SENSOR_MAGNETOMETER_Z = "magnetometer_z";

    private String mAddress;

    private String mSensor;

    public MkrSciBleSensor(String sensorId, MkrSciBleSensorSpec spec) {
        super(sensorId, AppSingleton.getUiThreadExecutor());
        mAddress = spec.getAddress();
        mSensor = spec.getSensor();
    }

    @Override
    protected SensorRecorder makeScalarControl(
            StreamConsumer c, SensorEnvironment environment,
            Context context, SensorStatusListener listener) {
        final Clock clock = environment.getDefaultClock();
        final MkrSciBleManager.Listener mkrSciBleListener = new MkrSciBleManager.Listener() {

            private boolean dataAvailable = false;

            @Override
            public void onInput1Updated(double value) {
                if (Objects.equals(mSensor, SENSOR_INPUT_1)) {
                    addData(value);
                }
            }

            @Override
            public void onInput2Updated(double value) {
                if (Objects.equals(mSensor, SENSOR_INPUT_2)) {
                    addData(value);
                }
            }

            @Override
            public void onInput3Updated(double value) {
                if (Objects.equals(mSensor, SENSOR_INPUT_3)) {
                    addData(value);
                }
            }

            @Override
            public void onVoltageUpdated(double value) {
                if (Objects.equals(mSensor, SENSOR_VOLTAGE)) {
                    addData(value);
                }
            }

            @Override
            public void onCurrentUpdated(double value) {
                if (Objects.equals(mSensor, SENSOR_CURRENT)) {
                    addData(value);
                }
            }

            @Override
            public void onResistanceUpdated(double value) {
                if (Objects.equals(mSensor, SENSOR_RESISTANCE)) {
                    addData(value);
                }
            }

            @Override
            public void onTemperatureUpdated(double value) {
                if (Objects.equals(mSensor, SENSOR_TEMPERATURE)) {
                    addData(value);
                }
            }

            @Override
            public void onAccelerometerUpdated(double x, double y, double z) {
                if (Objects.equals(mSensor, SENSOR_ACCELEROMETER_X)) {
                    addData(x);
                } else if (Objects.equals(mSensor, SENSOR_ACCELEROMETER_Y)) {
                    addData(y);
                } else if (Objects.equals(mSensor, SENSOR_ACCELEROMETER_Z)) {
                    addData(z);
                }
            }

            @Override
            public void onGyroscopeUpdated(double x, double y, double z) {
                if (Objects.equals(mSensor, SENSOR_GYROSCOPE_X)) {
                    addData(x);
                } else if (Objects.equals(mSensor, SENSOR_GYROSCOPE_Y)) {
                    addData(y);
                } else if (Objects.equals(mSensor, SENSOR_GYROSCOPE_Z)) {
                    addData(z);
                }
            }

            @Override
            public void onMagnetometerUpdated(double x, double y, double z) {
                if (Objects.equals(mSensor, SENSOR_MAGNETOMETER_X)) {
                    addData(x);
                } else if (Objects.equals(mSensor, SENSOR_MAGNETOMETER_Y)) {
                    addData(y);
                } else if (Objects.equals(mSensor, SENSOR_MAGNETOMETER_Z)) {
                    addData(z);
                }
            }

            private void addData(double v) {
                if (!dataAvailable) {
                    dataAvailable = true;
                }
                c.addData(clock.getNow(), v);
            }
        };
        return new AbstractSensorRecorder() {

            private Thread t;

            private int v = 1;

            @Override
            public void startObserving() {
                MkrSciBleManager.subscribe(context, mAddress, mkrSciBleListener);
                listener.onSourceStatus(getId(), SensorStatusListener.STATUS_CONNECTED);
            }

            @Override
            public void stopObserving() {
                MkrSciBleManager.unsubscribe(mAddress, mkrSciBleListener);
                listener.onSourceStatus(getId(), SensorStatusListener.STATUS_DISCONNECTED);
            }
        };
    }

}
