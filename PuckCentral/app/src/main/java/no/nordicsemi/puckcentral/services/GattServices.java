package no.nordicsemi.puckcentral.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import no.nordicsemi.puckcentral.triggers.Trigger;
import no.nordicsemi.puckcentral.utils.UUIDUtils;


public abstract class GattServices {
    public static final UUID CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID
            = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final UUID LOCATION_SERVICE_UUID = UUIDUtils.stringToUUID("bftj location   ");
    public static final UUID IR_SERVICE_UUID =  UUIDUtils.stringToUUID("bftj ir         ");
    public static final UUID CUBE_SERVICE_UUID = UUIDUtils.stringToUUID("bftj cube       ");
    public static final UUID DISPLAY_SERVICE_UUID = UUIDUtils.stringToUUID("bftj display    ");

    public static final UUID CUBE_CHARACTERISTIC_DIRECTION_UUID
            = UUIDUtils.stringToUUID("bftj cube dirctn");

    public static final String[] LOCATION_TRIGGERS = new String[] {
            Trigger.TRIGGER_ENTER_ZONE,
            Trigger.TRIGGER_LEAVE_ZONE
    };

    public static final String[] CUBE_TRIGGERS = new String[] {
            Trigger.TRIGGER_ROTATE_CUBE_UP,
            Trigger.TRIGGER_ROTATE_CUBE_DOWN,
            Trigger.TRIGGER_ROTATE_CUBE_LEFT,
            Trigger.TRIGGER_ROTATE_CUBE_RIGHT,
            Trigger.TRIGGER_ROTATE_CUBE_FRONT,
            Trigger.TRIGGER_ROTATE_CUBE_BACK
    };

    public static Map<UUID, String[]> mServicesToTriggers;

    static {
        mServicesToTriggers = new HashMap<>();
        mServicesToTriggers.put(LOCATION_SERVICE_UUID, LOCATION_TRIGGERS);
        mServicesToTriggers.put(IR_SERVICE_UUID, LOCATION_TRIGGERS);
        mServicesToTriggers.put(CUBE_SERVICE_UUID, CUBE_TRIGGERS);
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
