package no.nordicsemi.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import no.nordicsemi.triggers.Trigger;
import no.nordicsemi.utils.UUIDUtils;


public abstract class GattServices {
    public static final UUID LOCATION_SERVICE_UUID = UUIDUtils.stringToUUID("bftj location   ");
    public static final UUID IR_SERVICE_UUID =  UUIDUtils.stringToUUID("bftj ir         ");

    public static final String[] LOCATION_TRIGGERS = new String[] {
            Trigger.TRIGGER_ENTER_ZONE,
            Trigger.TRIGGER_LEAVE_ZONE
    };

    public static Map<UUID, String[]> mServicesToTriggers;

    static {
        mServicesToTriggers = new HashMap<>();
        mServicesToTriggers.put(LOCATION_SERVICE_UUID, LOCATION_TRIGGERS);
        mServicesToTriggers.put(IR_SERVICE_UUID, LOCATION_TRIGGERS);
    }

    public static String[] getTriggersForServiceUUID(UUID serviceUUID) {
        if (mServicesToTriggers.containsKey(serviceUUID)) {
            return mServicesToTriggers.get(serviceUUID);
        }
        return new String[0];
    }

    public static String[] getTriggersForServiceUUIDs(ArrayList<UUID> serviceUUIDs) {
        Set<String> triggers = new HashSet<>();
        for (UUID serviceUUID : serviceUUIDs) {
            triggers.addAll(Arrays.asList(getTriggersForServiceUUID(serviceUUID)));
        }
        return triggers.toArray(new String[triggers.size()]);
    }
}
