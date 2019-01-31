package com.google.android.apps.forscience.whistlepunk.metadata;

import android.util.Log;

import com.google.android.apps.forscience.whistlepunk.MkrSciBleSensorAppearance;
import com.google.android.apps.forscience.whistlepunk.SensorAppearance;
import com.google.android.apps.forscience.whistlepunk.data.GoosciMkrSciSensorConfig;
import com.google.common.annotations.VisibleForTesting;

import java.util.Objects;

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
        return MkrSciBleSensorAppearance.get(mConfig.sensor, mConfig.handler);
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
