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

    public static final String SENSOR_INPUT_1 = "1001_input_1";
    public static final String SENSOR_INPUT_2 = "1002_input_2";
    public static final String SENSOR_INPUT_3 = "1003_input_3";
    public static final String SENSOR_VOLTAGE = "2001_voltage";
    public static final String SENSOR_CURRENT = "3001_current";
    public static final String SENSOR_RESISTANCE = "4001_resistance";
    public static final String SENSOR_ACCELEROMETER_X = "5000_accelerometer_x";
    public static final String SENSOR_ACCELEROMETER_Y = "5001_accelerometer_y";
    public static final String SENSOR_ACCELEROMETER_Z = "5002_accelerometer_z";
    public static final String SENSOR_LINEAR_ACCELEROMETER = "5003_linear_accelerometer";
    public static final String SENSOR_GYROSCOPE_X = "6001_gyroscope_x";
    public static final String SENSOR_GYROSCOPE_Y = "6002_gyroscope_y";
    public static final String SENSOR_GYROSCOPE_Z = "6003_gyroscope_z";
    public static final String SENSOR_MAGNETOMETER = "7001_magnetometer";

    public static final String HANDLER_RAW = "raw";
    public static final String HANDLER_TEMPERATURE_CELSIUS = "temperature_celsius";
    public static final String HANDLER_TEMPERATURE_FAHRENHEIT = "temperature_fahrenheit";
    public static final String HANDLER_LIGHT = "light";

    private String mAddress;

    private String mCharacteristic;

    private ValueHandler mValueHandler;

    public MkrSciBleSensor(String sensorId, MkrSciBleSensorSpec spec) {
        super(sensorId, AppSingleton.getUiThreadExecutor());
        mAddress = spec.getAddress();
        final String sensorKind = spec.getSensor();
        final String sensorHandler = spec.getHandler();
        switch (sensorKind) {
            case SENSOR_INPUT_1:
                mCharacteristic = MkrSciBleManager.INPUT_1_UUID;
                switch (sensorHandler) {
                    case HANDLER_TEMPERATURE_CELSIUS:
                        mValueHandler = new TemperatureCelsiusValueHandler(0);
                        break;
                    case HANDLER_TEMPERATURE_FAHRENHEIT:
                        mValueHandler = new TemperatureFahrenheitValueHandler(0);
                        break;
                    case HANDLER_LIGHT:
                        mValueHandler = new LightValueHandler(0);
                        break;
                    default:
                        mValueHandler = new SimpleValueHandler(0);
                        break;
                }
                break;
            case SENSOR_INPUT_2:
                mCharacteristic = MkrSciBleManager.INPUT_2_UUID;
                switch (sensorHandler) {
                    case HANDLER_LIGHT:
                        mValueHandler = new LightValueHandler(0);
                        break;
                    default:
                        mValueHandler = new SimpleValueHandler(0);
                        break;
                }
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
                mValueHandler = new ResistanceValueHandler(0);
                break;
            case SENSOR_ACCELEROMETER_X:
                mCharacteristic = MkrSciBleManager.ACCELEROMETER_UUID;
                mValueHandler = new AccelerometerValueHandler(0);
                break;
            case SENSOR_ACCELEROMETER_Y:
                mCharacteristic = MkrSciBleManager.ACCELEROMETER_UUID;
                mValueHandler = new AccelerometerValueHandler(1);
                break;
            case SENSOR_ACCELEROMETER_Z:
                mCharacteristic = MkrSciBleManager.ACCELEROMETER_UUID;
                mValueHandler = new AccelerometerValueHandler(2);
                break;
            case SENSOR_LINEAR_ACCELEROMETER:
                mCharacteristic = MkrSciBleManager.ACCELEROMETER_UUID;
                mValueHandler = new LinearAccelerometerValueHandler();
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
            case SENSOR_MAGNETOMETER:
                mCharacteristic = MkrSciBleManager.MAGNETOMETER_UUID;
                mValueHandler = new MagnetometerValueHandler();
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

    private static class TemperatureCelsiusValueHandler implements ValueHandler {
        private int index;

        private TemperatureCelsiusValueHandler(int index) {
            this.index = index;
        }

        @Override
        public void handle(StreamConsumer c, long ts, double[] values) {
            if (values.length > index) {
                c.addData(ts, (((values[index] * 3300d) / 1023d) - 500) * 0.1d);
            }
        }
    }

    private static class TemperatureFahrenheitValueHandler implements ValueHandler {
        private int index;

        private TemperatureFahrenheitValueHandler(int index) {
            this.index = index;
        }

        @Override
        public void handle(StreamConsumer c, long ts, double[] values) {
            if (values.length > index) {
                final double celsius = (((values[index] * 3300d) / 1023d) - 500) * 0.1d;
                c.addData(ts, (celsius * (9d / 5d)) + 32d);
            }
        }
    }

    private static class LightValueHandler implements ValueHandler {
        private int index;

        private LightValueHandler(int index) {
            this.index = index;
        }

        @Override
        public void handle(StreamConsumer c, long ts, double[] values) {
            if (values.length > index) {
                c.addData(ts, ((values[index] * 3300d) / 1023d) * 0.5d);
            }
        }
    }

    private static class AccelerometerValueHandler implements ValueHandler {
        private int index;

        private AccelerometerValueHandler(int index) {
            this.index = index;
        }

        @Override
        public void handle(StreamConsumer c, long ts, double[] values) {
            if (values.length > index) {
                c.addData(ts, values[index] * 10);
            }
        }
    }

    private static class LinearAccelerometerValueHandler implements ValueHandler {
        @Override
        public void handle(StreamConsumer c, long ts, double[] values) {
            if (values.length < 3) {
                return;
            }
            c.addData(ts,
                    Math.sqrt((values[0] * values[0])
                            + (values[1] * values[1])
                            + (values[2] * values[2])
                    ) * 10
            );
        }
    }

    private static class ResistanceValueHandler implements ValueHandler {

        private int index;

        private ResistanceValueHandler(int index) {
            this.index = index;
        }

        @Override
        public void handle(StreamConsumer c, long ts, double[] values) {
            if (values.length > index) {
                double v = values[index] / 1000D;
                if (v > 1000D) {
                    c.addData(ts, 1000D);
                } else if (v < 0D) {
                    c.addData(ts, 0D);
                } else {
                    c.addData(ts, v);
                }
            }
        }

    }

    private static class MagnetometerValueHandler implements ValueHandler {
        @Override
        public void handle(StreamConsumer c, long ts, double[] values) {
            if (values.length < 3) {
                return;
            }
            c.addData(ts, Math.sqrt(
                    (values[0] * values[0]) + (values[1] * values[1]) + (values[2] * values[2]))
                    * 100);
        }
    }

    private static class VectorValueHandler implements ValueHandler {
        @Override
        public void handle(StreamConsumer c, long ts, double[] values) {
            if (values.length < 3) {
                return;
            }
            c.addData(ts, Math.sqrt(
                    (values[0] * values[0]) + (values[1] * values[1]) + (values[2] * values[2])));
        }
    }

}
