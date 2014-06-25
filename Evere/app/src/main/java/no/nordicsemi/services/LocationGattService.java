package no.nordicsemi.services;

import no.nordicsemi.triggers.Trigger;

public class LocationGattService extends GattService {
    private String mServiceUUID = Integer.toString(0xC175);
    private String[] mTriggers = new String[] {
            Trigger.TRIGGER_PROXIMITY_IMMEDIATE,
            Trigger.TRIGGER_PROXIMITY_NEAR,
            Trigger.TRIGGER_PROXIMITY_FAR
    };

    public String getServiceUUID() {
        return mServiceUUID;
    }

    @Override
    public String[] getTriggers() {
        return mTriggers;
    }
}
