/*
 *  Copyright 2016 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.android.apps.forscience.whistlepunk.devicemanager;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.ParcelUuid;
import android.support.annotation.VisibleForTesting;

import com.google.android.apps.forscience.ble.DeviceDiscoverer;
import com.google.android.apps.forscience.ble.MkrSciBleManager;
import com.google.android.apps.forscience.javalib.FailureListener;
import com.google.android.apps.forscience.whistlepunk.PermissionUtils;
import com.google.android.apps.forscience.whistlepunk.R;
import com.google.android.apps.forscience.whistlepunk.SensorProvider;
import com.google.android.apps.forscience.whistlepunk.api.scalarinput.InputDeviceSpec;
import com.google.android.apps.forscience.whistlepunk.data.GoosciSensorSpec;
import com.google.android.apps.forscience.whistlepunk.metadata.ExternalSensorSpec;
import com.google.android.apps.forscience.whistlepunk.metadata.MkrSciBleDeviceSpec;
import com.google.android.apps.forscience.whistlepunk.metadata.MkrSciBleSensorSpec;
import com.google.android.apps.forscience.whistlepunk.sensorapi.SensorChoice;
import com.google.android.apps.forscience.whistlepunk.sensors.MkrSciBleSensor;

/**
 * Discovers BLE sensors that speak the Arduino "MkrSci" Science Journal protocol.
 */
public class MkrSciBleDiscoverer implements SensorDiscoverer {

    private static final SensorProvider PROVIDER = new SensorProvider() {
        @Override
        public SensorChoice buildSensor(String sensorId, ExternalSensorSpec spec) {
            return new MkrSciBleSensor(sensorId, (MkrSciBleSensorSpec) spec);
        }

        @Override
        public ExternalSensorSpec buildSensorSpec(String name, byte[] config) {
            return new MkrSciBleSensorSpec(name, config);
        }
    };

    private static final String SERVICE_ID =
            "com.google.android.apps.forscience.whistlepunk.mkrscible";

    private DeviceDiscoverer mDeviceDiscoverer;
    private Runnable mOnScanDone;
    private Context mContext;

    public MkrSciBleDiscoverer(Context context) {
        mContext = context;
    }

    @Override
    public SensorProvider getProvider() {
        return PROVIDER;
    }

    @Override
    public boolean startScanning(final ScanListener listener, FailureListener onScanError) {
        stopScanning();

        mOnScanDone = () -> {
            listener.onServiceScanComplete(SERVICE_ID);
            listener.onScanDone();
        };

        mDeviceDiscoverer = createDiscoverer(mContext);
        final boolean canScan = mDeviceDiscoverer.canScan() && hasScanPermission();


        listener.onServiceFound(new DiscoveredService() {
            @Override
            public String getServiceId() {
                return SERVICE_ID;
            }

            @Override
            public String getName() {
                // return mContext.getString(R.string.native_ble_service_name);
                return "MKR Science Boards";
            }

            @Override
            public Drawable getIconDrawable(Context context) {
                return context.getResources().getDrawable(R.drawable.ic_bluetooth_white_24dp);
            }

            @Override
            public ServiceConnectionError getConnectionErrorIfAny() {
                if (canScan) {
                    return null;
                } else {
                    return new ServiceConnectionError() {
                        @Override
                        public String getErrorMessage() {
                            return mContext.getString(R.string.btn_enable_bluetooth);
                        }

                        @Override
                        public boolean canBeResolved() {
                            return true;
                        }

                        @Override
                        public void tryToResolve(FragmentManager fragmentManager) {
                            ScanDisabledDialogFragment.newInstance().show(fragmentManager,
                                    "scanDisabledDialog");
                        }
                    };
                }
            }
        });

        if (!canScan) {
            stopScanning();
            return false;
        }

        mDeviceDiscoverer.startScanning(new ParcelUuid[]{
                ParcelUuid.fromString(MkrSciBleManager.SERVICE_UUID)
        }, new DeviceDiscoverer.Callback() {
            @Override
            public void onDeviceFound(final DeviceDiscoverer.DeviceRecord record) {
                onDeviceRecordFound(record, listener);
            }

            @Override
            public void onError(int error) {
            }
        });
        return true;
    }

    @VisibleForTesting
    protected boolean hasScanPermission() {
        return PermissionUtils.hasPermission(mContext,
                PermissionUtils.REQUEST_ACCESS_COARSE_LOCATION);
    }

    protected DeviceDiscoverer createDiscoverer(Context context) {
        return DeviceDiscoverer.getNewInstance(context);
    }

    @Override
    public void stopScanning() {
        if (mDeviceDiscoverer != null) {
            mDeviceDiscoverer.stopScanning();
            mDeviceDiscoverer = null;
        }
        if (mOnScanDone != null) {
            mOnScanDone.run();
            mOnScanDone = null;
        }
    }

    private void onDeviceRecordFound(DeviceDiscoverer.DeviceRecord record,
                                     ScanListener scanListener) {
        WhistlepunkBleDevice device = record.device;

        final String address = device.getAddress();

        // sensorScanCallbacks will handle duplicates

        final MkrSciBleDeviceSpec spec = new MkrSciBleDeviceSpec(address, device.getName());

        scanListener.onDeviceFound(new DiscoveredDevice() {
            @Override
            public String getServiceId() {
                return SERVICE_ID;
            }

            @Override
            public InputDeviceSpec getSpec() {
                return DeviceRegistry.createHoldingDevice(spec);
            }
        });

        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_INPUT_1, "Input 1");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_INPUT_2, "Input 2");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_INPUT_3, "Input 3");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_VOLTAGE, "Voltage");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_CURRENT, "Current");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_RESISTANCE, "Resistance");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_TEMPERATURE, "Temperature");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_ACCELEROMETER_X, "Accelerometer X");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_ACCELEROMETER_Y, "Accelerometer Y");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_ACCELEROMETER_Z, "Accelerometer Z");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_GYROSCOPE_X, "Gyroscope X");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_GYROSCOPE_Y, "Gyroscope Y");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_GYROSCOPE_Z, "Gyroscope Z");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_MAGNETOMETER_X, "Magnetometer X");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_MAGNETOMETER_Y, "Magnetometer Y");
        addSensor(scanListener, address,
                MkrSciBleSensor.SENSOR_MAGNETOMETER_Z, "Magnetometer Z");
    }

    private void addSensor(ScanListener scanListener, String address, String sensor, String name) {
        final MkrSciBleSensorSpec spec = new MkrSciBleSensorSpec(address, sensor, name);
        scanListener.onSensorFound(new DiscoveredSensor() {
            @Override
            public GoosciSensorSpec.SensorSpec getSensorSpec() {
                return spec.asGoosciSpec();
            }

            @Override
            public SettingsInterface getSettingsInterface() {
                return null;
            }

            @Override
            public boolean shouldReplaceStoredSensor(ConnectableSensor oldSensor) {
                return false;
            }
        });
    }

}
