package com.google.android.apps.forscience.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MkrSciBleManager {

    public static final String SERVICE_UUID = "555a0001-0000-467a-9538-01f0652c74e8";

    private static final ManagedCharacteristic CHARACTERISTIC_INPUT_1 =
            new ManagedCharacteristic("555a0001-2001-467a-9538-01f0652c74e8", ValueType.UINT16);

    private static final ManagedCharacteristic CHARACTERISTIC_INPUT_2 =
            new ManagedCharacteristic("555a0001-2002-467a-9538-01f0652c74e8", ValueType.UINT16);

    private static final ManagedCharacteristic CHARACTERISTIC_INPUT_3 =
            new ManagedCharacteristic("555a0001-2003-467a-9538-01f0652c74e8", ValueType.UINT16);

    private static final ManagedCharacteristic CHARACTERISTIC_VOLTAGE =
            new ManagedCharacteristic("555a0001-4001-467a-9538-01f0652c74e8", ValueType.SFLOAT);

    private static final ManagedCharacteristic CHARACTERISTIC_CURRENT =
            new ManagedCharacteristic("555a0001-4002-467a-9538-01f0652c74e8", ValueType.SFLOAT);

    private static final ManagedCharacteristic CHARACTERISTIC_RESISTANCE =
            new ManagedCharacteristic("555a0001-4003-467a-9538-01f0652c74e8", ValueType.SFLOAT);

    private static final ManagedCharacteristic CHARACTERISTIC_TEMPERATURE =
            new ManagedCharacteristic("555a0001-000b-467a-9538-01f0652c74e8", ValueType.SFLOAT);

    private static final ManagedCharacteristic CHARACTERISTIC_ACCELEROMETER =
            new ManagedCharacteristic("555a0001-5001-467a-9538-01f0652c74e8", ValueType.SFLOAT_ARR);

    private static final ManagedCharacteristic CHARACTERISTIC_GYROSCOPE =
            new ManagedCharacteristic("555a0001-5002-467a-9538-01f0652c74e8", ValueType.SFLOAT_ARR);

    private static final ManagedCharacteristic CHARACTERISTIC_MAGNETOMETER =
            new ManagedCharacteristic("555a0001-5003-467a-9538-01f0652c74e8", ValueType.SFLOAT_ARR);

    private static final ManagedCharacteristic[] MANAGED_CHARACTERISTICS = {
            CHARACTERISTIC_INPUT_1,
            CHARACTERISTIC_INPUT_2,
            CHARACTERISTIC_INPUT_3,
            CHARACTERISTIC_VOLTAGE,
            CHARACTERISTIC_CURRENT,
            CHARACTERISTIC_RESISTANCE,
            CHARACTERISTIC_TEMPERATURE,
            CHARACTERISTIC_ACCELEROMETER,
            CHARACTERISTIC_GYROSCOPE,
            CHARACTERISTIC_MAGNETOMETER
    };

    private static final Map<String, List<Listener>> sListeners = new HashMap<>();

    private static final Map<String, BluetoothGatt> sConnectedGattServices = new HashMap<>();

    public static void subscribe(Context context, String address, Listener listener) {
        synchronized (sListeners) {
            List<Listener> listeners = sListeners.get(address);
            if (listeners == null) {
                listeners = new ArrayList<>();
                sListeners.put(address, listeners);
                connect(context, address);
            }
            listeners.add(listener);
        }

    }

    public static void unsubscribe(String address, Listener listener) {
        synchronized (sListeners) {
            List<Listener> listeners = sListeners.get(address);
            if (listeners != null && listeners.remove(listener)) {
                if (listeners.size() == 0) {
                    sListeners.remove(address);
                    disconnect(address);
                }
            }
        }
    }

    private static void connect(Context context, String address) {
        BluetoothManager manager = (BluetoothManager) context.getSystemService(
                Context.BLUETOOTH_SERVICE);
        if (manager == null) {
            return;
        }
        BluetoothAdapter adapter = manager.getAdapter();
        BluetoothDevice device = adapter.getRemoteDevice(address);
        device.connectGatt(context, true, new BluetoothGattCallback() {

            private final List<CharacteristicItem> pendingCharacteristics = new ArrayList<>();

            private final List<CharacteristicItem> subscribedCharacteristics = new ArrayList<>();

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (status == BluetoothGatt.GATT_SUCCESS
                        && newState == BluetoothProfile.STATE_CONNECTED) {
                    sConnectedGattServices.put(address, gatt);
                    pendingCharacteristics.clear();
                    subscribedCharacteristics.clear();
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    gatt.disconnect();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                BluetoothGattService service = gatt.getService(UUID.fromString(SERVICE_UUID));
                if (service != null) {
                    final List<CharacteristicItem> items = new ArrayList<>();
                    List<BluetoothGattCharacteristic> characteristics =
                            service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        UUID uuid = characteristic.getUuid();
                        for (ManagedCharacteristic c : MANAGED_CHARACTERISTICS) {
                            if (c.uuid.equals(uuid)) {
                                final CharacteristicItem item = new CharacteristicItem();
                                item.managedCharacteristic = c;
                                item.bluetoothCharacteristic = characteristic;
                                items.add(item);
                                break;
                            }
                        }
                    }
                    subscribedCharacteristics.addAll(items);
                    pendingCharacteristics.addAll(items);
                    subscribeNextCharacteristic(gatt);
                }
            }

            private void subscribeNextCharacteristic(BluetoothGatt gatt) {
                if (pendingCharacteristics.size() == 0) {
                    return;
                }
                CharacteristicItem item = pendingCharacteristics.remove(0);
                gatt.setCharacteristicNotification(item.bluetoothCharacteristic, true);
                BluetoothGattDescriptor descriptor = item.bluetoothCharacteristic.getDescriptor(
                        UUID.fromString(("00002902-0000-1000-8000-00805f9b34fb")));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
                gatt.readCharacteristic(item.bluetoothCharacteristic);
            }

            @Override
            public void onDescriptorWrite(
                    BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                subscribeNextCharacteristic(gatt);
            }

            @Override
            public void onCharacteristicRead(
                    BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                subscribeNextCharacteristic(gatt);
            }

            @Override
            public void onCharacteristicChanged(
                    BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                synchronized (subscribedCharacteristics) {
                    for (int i = 0; i < subscribedCharacteristics.size(); i++) {
                        final CharacteristicItem item = subscribedCharacteristics.get(i);
                        if (item.bluetoothCharacteristic.equals(characteristic)) {
                            double[] values;
                            try {
                                values = parse(item.managedCharacteristic.valueType,
                                        characteristic.getValue());
                            } catch (Throwable t) {
                                Log.e("MkrSciManager",
                                        "Error parsing characteristic value", t);
                                return;
                            }
                            notifyCharacteristicValues(address, item.managedCharacteristic, values);
                        }
                    }
                }
            }
        });
    }

    private static void disconnect(String address) {
        BluetoothGatt gatt = sConnectedGattServices.get(address);
        if (gatt != null) {
            gatt.disconnect();
        }
    }

    private static void notifyCharacteristicValues(
            String address, ManagedCharacteristic characteristic, double[] values) {
        synchronized (sListeners) {
            List<Listener> listeners = sListeners.get(address);
            if (listeners == null) {
                return;
            }
            if (CHARACTERISTIC_INPUT_1 == characteristic) {
                if (values.length < 1) {
                    return;
                }
                Log.i("h42", "Input 1:" + values[0]);
                for (Listener l : listeners) {
                    l.onInput1Updated(values[0]);
                }
            } else if (CHARACTERISTIC_INPUT_2 == characteristic) {
                if (values.length < 1) {
                    return;
                }
                Log.i("h42", "Input 2:" + values[0]);
                for (Listener l : listeners) {
                    l.onInput2Updated(values[0]);
                }
            } else if (CHARACTERISTIC_INPUT_3 == characteristic) {
                if (values.length < 1) {
                    return;
                }
                Log.i("h42", "Input 3:" + values[0]);
                for (Listener l : listeners) {
                    l.onInput3Updated(values[0]);
                }
            } else if (CHARACTERISTIC_VOLTAGE == characteristic) {
                if (values.length < 1) {
                    return;
                }
                Log.i("h42", "Voltage:" + values[0]);
                for (Listener l : listeners) {
                    l.onVoltageUpdated(values[0]);
                }
            } else if (CHARACTERISTIC_CURRENT == characteristic) {
                if (values.length < 1) {
                    return;
                }
                Log.i("h42", "Current:" + values[0]);
                for (Listener l : listeners) {
                    l.onCurrentUpdated(values[0]);
                }
            } else if (CHARACTERISTIC_RESISTANCE == characteristic) {
                if (values.length < 1) {
                    return;
                }
                Log.i("h42", "Resistance:" + values[0]);
                for (Listener l : listeners) {
                    l.onResistanceUpdated(values[0]);
                }
            } else if (CHARACTERISTIC_TEMPERATURE == characteristic) {
                if (values.length < 1) {
                    return;
                }
                Log.i("h42", "Temperature:" + values[0]);
                for (Listener l : listeners) {
                    l.onTemperatureUpdated(values[0]);
                }
            } else if (CHARACTERISTIC_ACCELEROMETER == characteristic) {
                if (values.length < 3) {
                    return;
                }
                Log.i("h42", "Accelerometer X:" + values[0]);
                Log.i("h42", "Accelerometer Y:" + values[1]);
                Log.i("h42", "Accelerometer Z:" + values[2]);
                for (Listener l : listeners) {
                    l.onAccelerometerUpdated(values[0], values[1], values[2]);
                }
            } else if (CHARACTERISTIC_GYROSCOPE == characteristic) {
                if (values.length < 3) {
                    return;
                }
                Log.i("h42", "Gyroscope X:" + values[0]);
                Log.i("h42", "Gyroscope Y:" + values[1]);
                Log.i("h42", "Gyroscope Z:" + values[2]);
                for (Listener l : listeners) {
                    l.onGyroscopeUpdated(values[0], values[1], values[2]);
                }
            } else if (CHARACTERISTIC_MAGNETOMETER == characteristic) {
                if (values.length < 3) {
                    return;
                }
                Log.i("h42", "Magnetometer X:" + values[0]);
                Log.i("h42", "Magnetometer Y:" + values[1]);
                Log.i("h42", "Magnetometer Z:" + values[2]);
                for (Listener l : listeners) {
                    l.onMagnetometerUpdated(values[0], values[1], values[2]);
                }
            }
        }
    }

    private static double[] parse(ValueType valueType, byte[] value) {
        if (ValueType.UINT8.equals(valueType)) {
            if (value.length < 1) {
                return null;
            }
            final ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.put((byte) 0);
            buffer.put((byte) 0);
            buffer.put((byte) 0);
            buffer.put(value[0]);
            buffer.position(0);
            return new double[]{buffer.getInt()};
        }
        if (ValueType.UINT16.equals(valueType)) {
            if (value.length < 2) {
                return null;
            }
            final ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.put((byte) 0);
            buffer.put((byte) 0);
            buffer.put(value[1]);
            buffer.put(value[0]);
            buffer.position(0);
            return new double[]{buffer.getInt()};
        }
        if (ValueType.UINT32.equals(valueType)) {
            if (value.length < 4) {
                return null;
            }
            final ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.put((byte) 0);
            buffer.put((byte) 0);
            buffer.put((byte) 0);
            buffer.put((byte) 0);
            buffer.put(value[3]);
            buffer.put(value[2]);
            buffer.put(value[1]);
            buffer.put(value[0]);
            buffer.position(0);
            return new double[]{buffer.getLong()};
        }
        if (ValueType.SFLOAT.equals(valueType)) {
            if (value.length < 4) {
                return null;
            }
            final ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.put(value[3]);
            buffer.put(value[2]);
            buffer.put(value[1]);
            buffer.put(value[0]);
            buffer.position(0);
            return new double[]{buffer.getFloat()};
        }
        if (ValueType.SFLOAT_ARR.equals(valueType)) {
            final int size = value.length / 4;
            final double[] array = new double[size];
            final ByteBuffer buffer = ByteBuffer.allocate(4);
            for (int i = 0; i < size; i++) {
                buffer.position(0);
                buffer.put(value[3]);
                buffer.put(value[2]);
                buffer.put(value[1]);
                buffer.put(value[0]);
                buffer.position(0);
                array[i] = buffer.getFloat();
            }
            return array;
        }
        return null;
    }

    private enum ValueType {
        UINT8, UINT16, UINT32, SFLOAT, SFLOAT_ARR
    }

    private static class ManagedCharacteristic {
        private UUID uuid;
        private ValueType valueType;

        private ManagedCharacteristic(String uuid, ValueType valueType) {
            this.uuid = UUID.fromString(uuid);
            this.valueType = valueType;
        }
    }

    private static class CharacteristicItem {
        private ManagedCharacteristic managedCharacteristic;
        private BluetoothGattCharacteristic bluetoothCharacteristic;
    }

    public interface Listener {
        void onInput1Updated(double value);

        void onInput2Updated(double value);

        void onInput3Updated(double value);

        void onVoltageUpdated(double value);

        void onCurrentUpdated(double value);

        void onResistanceUpdated(double value);

        void onTemperatureUpdated(double value);

        void onAccelerometerUpdated(double x, double y, double z);

        void onGyroscopeUpdated(double x, double y, double z);

        void onMagnetometerUpdated(double x, double y, double z);
    }

}
