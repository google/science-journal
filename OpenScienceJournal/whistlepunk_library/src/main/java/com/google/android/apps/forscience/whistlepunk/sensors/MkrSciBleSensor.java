package com.google.android.apps.forscience.whistlepunk.sensors;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

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

public class MkrSciBleSensor extends ScalarSensor {

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    public static final String SENSOR_INPUT_1 = "input_1";
    public static final String SENSOR_INPUT_2 = "input_2";
    public static final String SENSOR_INPUT_3 = "input_3";
    public static final String SENSOR_VOLTAGE = "voltage";
    public static final String SENSOR_CURRENT = "current";
    public static final String SENSOR_RESISTANCE = "resistance";
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

    private String mCharacteristic;

    private ValueHandler mValueHandler;

    public MkrSciBleSensor(String sensorId, MkrSciBleSensorSpec spec) {
        super(sensorId, AppSingleton.getUiThreadExecutor());
        mAddress = spec.getAddress();
        String sensorKind = spec.getSensor();
        switch (sensorKind) {
            case SENSOR_INPUT_1:
                mCharacteristic = MkrSciBleManager.INPUT_1_UUID;
                mValueHandler = new SimpleValueHandler(0);
                break;
            case SENSOR_INPUT_2:
                mCharacteristic = MkrSciBleManager.INPUT_2_UUID;
                mValueHandler = new SimpleValueHandler(0);
                break;
            case SENSOR_INPUT_3:
                mCharacteristic = MkrSciBleManager.INPUT_3_UUID;
                mValueHandler = new SimpleValueHandler(0);
                break;
            case SENSOR_VOLTAGE:
                mCharacteristic = MkrSciBleManager.VOLTAGE_UUID;
                mValueHandler = new SimpleValueHandler(0);
                break;
            case SENSOR_CURRENT:
                mCharacteristic = MkrSciBleManager.CURRENT_UUID;
                mValueHandler = new SimpleValueHandler(0);
                break;
            case SENSOR_RESISTANCE:
                mCharacteristic = MkrSciBleManager.RESISTANCE_UUID;
                mValueHandler = new SimpleValueHandler(0);
                break;
            case SENSOR_ACCELEROMETER_X:
                mCharacteristic = MkrSciBleManager.ACCELEROMETER_UUID;
                mValueHandler = new SimpleValueHandler(0);
                break;
            case SENSOR_ACCELEROMETER_Y:
                mCharacteristic = MkrSciBleManager.ACCELEROMETER_UUID;
                mValueHandler = new SimpleValueHandler(1);
                break;
            case SENSOR_ACCELEROMETER_Z:
                mCharacteristic = MkrSciBleManager.ACCELEROMETER_UUID;
                mValueHandler = new SimpleValueHandler(2);
                break;
            case SENSOR_GYROSCOPE_X:
                mCharacteristic = MkrSciBleManager.GYROSCOPE_UUID;
                mValueHandler = new SimpleValueHandler(0);
                break;
            case SENSOR_GYROSCOPE_Y:
                mCharacteristic = MkrSciBleManager.GYROSCOPE_UUID;
                mValueHandler = new SimpleValueHandler(1);
                break;
            case SENSOR_GYROSCOPE_Z:
                mCharacteristic = MkrSciBleManager.GYROSCOPE_UUID;
                mValueHandler = new SimpleValueHandler(2);
                break;
            case SENSOR_MAGNETOMETER_X:
                mCharacteristic = MkrSciBleManager.MAGNETOMETER_UUID;
                mValueHandler = new SimpleValueHandler(0);
                break;
            case SENSOR_MAGNETOMETER_Y:
                mCharacteristic = MkrSciBleManager.MAGNETOMETER_UUID;
                mValueHandler = new SimpleValueHandler(1);
                break;
            case SENSOR_MAGNETOMETER_Z:
                mCharacteristic = MkrSciBleManager.MAGNETOMETER_UUID;
                mValueHandler = new SimpleValueHandler(2);
                break;
            default:
                throw new RuntimeException("Unmanaged mkr sci ble sensor: " + sensorKind);
        }
    }

    @Override
    protected SensorRecorder makeScalarControl(
            StreamConsumer c, SensorEnvironment environment,
            Context context, SensorStatusListener listener) {
        final Clock clock = environment.getDefaultClock();
        final MkrSciBleManager.Listener mkrSciBleListener = new MkrSciBleManager.Listener() {

            private boolean connected = false;

            @Override
            public void onValuesUpdated(double[] values) {
                if (!connected) {
                    connected = true;
                    sHandler.post(() -> listener.onSourceStatus(getId(),
                            SensorStatusListener.STATUS_CONNECTED));
                }
                mValueHandler.handle(c, clock.getNow(), values);
            }
        };
        return new AbstractSensorRecorder() {
            @Override
            public void startObserving() {
                sHandler.post(() -> listener.onSourceStatus(getId(),
                        SensorStatusListener.STATUS_CONNECTING));
                MkrSciBleManager.subscribe(context, mAddress, mCharacteristic, mkrSciBleListener);
            }

            @Override
            public void stopObserving() {
                MkrSciBleManager.unsubscribe(mAddress, mCharacteristic, mkrSciBleListener);
                listener.onSourceStatus(getId(), SensorStatusListener.STATUS_DISCONNECTED);
            }
        };
    }

    private interface ValueHandler {

        void handle(StreamConsumer c, long ts, double[] values);

    }

    private static class SimpleValueHandler implements ValueHandler {
        private int index;

        private SimpleValueHandler(int index) {
            this.index = index;
        }

        @Override
        public void handle(StreamConsumer c, long ts, double[] values) {
            if (values.length > index) {
                c.addData(ts, values[index]);
            }
        }
    }

}
