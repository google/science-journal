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

import android.util.Log;

public class PinTypeProvider {
    public static final PinType DEFAULT_PIN = new PinType(PinSignalType.ANALOG, 0);

    private PinType[] mPins;

    public PinTypeProvider() {
        mPins = new PinType[]{
                DEFAULT_PIN,
                new PinType(PinSignalType.ANALOG, 1),
                new PinType(PinSignalType.ANALOG, 2),
                new PinType(PinSignalType.ANALOG, 3),
                new PinType(PinSignalType.ANALOG, 4),
                new PinType(PinSignalType.ANALOG, 5),
                new PinType(PinSignalType.ANALOG, 6),
                new PinType(PinSignalType.ANALOG, 7),
                new PinType(PinSignalType.ANALOG, 8),
                new PinType(PinSignalType.ANALOG, 9),
                new PinType(PinSignalType.ANALOG, 10),
                new PinType(PinSignalType.ANALOG, 11),
                new PinType(PinSignalType.ANALOG, 12),
                new PinType(PinSignalType.ANALOG, 13),
                new PinType(PinSignalType.ANALOG, 14),
                new PinType(PinSignalType.ANALOG, 15),
                new PinType(PinSignalType.ANALOG, 16),
                new PinType(PinSignalType.ANALOG, 17),
                new PinType(PinSignalType.ANALOG, 18),
                new PinType(PinSignalType.ANALOG, 19),
                new PinType(PinSignalType.ANALOG, 20),
                new PinType(PinSignalType.ANALOG, 21),
                new PinType(PinSignalType.ANALOG, 22),
                new PinType(PinSignalType.ANALOG, 23),
                new PinType(PinSignalType.ANALOG, 24),
                new PinType(PinSignalType.ANALOG, 25),
                new PinType(PinSignalType.ANALOG, 26),
                new PinType(PinSignalType.ANALOG, 27),
                new PinType(PinSignalType.ANALOG, 28),
                new PinType(PinSignalType.ANALOG, 29),
                new PinType(PinSignalType.ANALOG, 30),
                new PinType(PinSignalType.ANALOG, 31),
                new PinType(PinSignalType.ANALOG, 32),
                new PinType(PinSignalType.ANALOG, 33),
                new PinType(PinSignalType.ANALOG, 34),
                new PinType(PinSignalType.ANALOG, 35),
                new PinType(PinSignalType.ANALOG, 36),
                new PinType(PinSignalType.ANALOG, 37),
                new PinType(PinSignalType.ANALOG, 38),
                new PinType(PinSignalType.ANALOG, 39),
                new PinType(PinSignalType.ANALOG, 40),
                new PinType(PinSignalType.ANALOG, 41),
                new PinType(PinSignalType.ANALOG, 42),
                new PinType(PinSignalType.DIGITAL, 1),
                new PinType(PinSignalType.DIGITAL, 2),
                new PinType(PinSignalType.DIGITAL, 3),
                new PinType(PinSignalType.DIGITAL, 4),
                new PinType(PinSignalType.DIGITAL, 5),
                new PinType(PinSignalType.DIGITAL, 6),
                new PinType(PinSignalType.DIGITAL, 7),
                new PinType(PinSignalType.DIGITAL, 8),
                new PinType(PinSignalType.DIGITAL, 9),
                new PinType(PinSignalType.DIGITAL, 10),
                new PinType(PinSignalType.DIGITAL, 11),
                new PinType(PinSignalType.DIGITAL, 12),
                new PinType(PinSignalType.DIGITAL, 13),
                new PinType(PinSignalType.DIGITAL, 14),
                new PinType(PinSignalType.DIGITAL, 15),
                new PinType(PinSignalType.DIGITAL, 16),
                new PinType(PinSignalType.DIGITAL, 17),
                new PinType(PinSignalType.DIGITAL, 18),
                new PinType(PinSignalType.DIGITAL, 19),
                new PinType(PinSignalType.DIGITAL, 20),
                new PinType(PinSignalType.DIGITAL, 21),
                new PinType(PinSignalType.DIGITAL, 22),
                new PinType(PinSignalType.DIGITAL, 23),
                new PinType(PinSignalType.DIGITAL, 24),
                new PinType(PinSignalType.DIGITAL, 25),
                new PinType(PinSignalType.DIGITAL, 26),
                new PinType(PinSignalType.DIGITAL, 27),
                new PinType(PinSignalType.DIGITAL, 28),
                new PinType(PinSignalType.DIGITAL, 29),
                new PinType(PinSignalType.DIGITAL, 30),
                new PinType(PinSignalType.DIGITAL, 31),
                new PinType(PinSignalType.DIGITAL, 32),
                new PinType(PinSignalType.DIGITAL, 33),
                new PinType(PinSignalType.DIGITAL, 34),
                new PinType(PinSignalType.DIGITAL, 35),
                new PinType(PinSignalType.DIGITAL, 36),
                new PinType(PinSignalType.DIGITAL, 37),
                new PinType(PinSignalType.DIGITAL, 38),
                new PinType(PinSignalType.DIGITAL, 39),
                new PinType(PinSignalType.DIGITAL, 40),
        };
    }

    public enum PinSignalType {
        ANALOG,
        DIGITAL,
        VIRTUAL,
    }

    public static class PinType {
        private String TAG = "PinType";
        private PinSignalType mPinSignalType;
        private int mPinNumber;

        PinType(PinSignalType pinSignalType, int pinNumber) {
            mPinSignalType = pinSignalType;
            mPinNumber = pinNumber;
        }

        public PinSignalType getPinSignalType() {
            return mPinSignalType;
        }

        public int getPinNumber() {
            return mPinNumber;
        }

        @Override
        public String toString() {
            String prefix;
            switch (mPinSignalType) {
                case ANALOG:
                    prefix = "A";
                    break;
                case DIGITAL:
                    prefix = "D";
                    break;
                case VIRTUAL:
                    prefix = "V";
                    break;
                default:
                    Log.wtf(TAG, "Unexpected enum value: " + mPinSignalType);
                    prefix = "X";
            }
            return prefix + mPinNumber;
        }
    }

    public PinType[] getPins() { return mPins; }

    /* Parse a pin in the format "A0", "D1", or "V10".  Returns null on parse failure. */
    public PinType parsePinName(String pinName) {
        if (pinName.isEmpty()) {
            // default
            return new PinType(PinSignalType.ANALOG, 0);
        } else if (pinName.startsWith("A")) {
            return new PinType(PinSignalType.ANALOG, Integer.valueOf(pinName.substring(1)));
        } else if (pinName.startsWith("D")) {
            return new PinType(PinSignalType.DIGITAL, Integer.valueOf(pinName.substring(1)));
        } else if (pinName.startsWith("V")) {
            return new PinType(PinSignalType.VIRTUAL, Integer.valueOf(pinName.substring(1)));
        } else {
            return null;
        }
    }
}
