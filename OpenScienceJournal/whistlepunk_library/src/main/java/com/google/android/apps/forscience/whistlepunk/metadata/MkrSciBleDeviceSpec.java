package com.google.android.apps.forscience.whistlepunk.metadata;

import com.google.android.apps.forscience.whistlepunk.SensorAppearance;
import com.google.android.apps.forscience.whistlepunk.devicemanager.SensorTypeProvider;

public class MkrSciBleDeviceSpec extends ExternalSensorSpec {

    public static final String TYPE = "bluetooth_le";

    private String mAddress;

    private String mName;

    public MkrSciBleDeviceSpec(String address, String name) {
        mAddress = address;
        mName = name;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getAddress() {
        return mAddress;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public SensorAppearance getSensorAppearance() {
        return SensorTypeProvider.getSensorAppearance(SensorTypeProvider.TYPE_CUSTOM, mName);
    }

    @Override
    public byte[] getConfig() {
        return null;
    }

    @Override
    public boolean shouldShowOptionsOnConnect() {
        return false;
    }

}
