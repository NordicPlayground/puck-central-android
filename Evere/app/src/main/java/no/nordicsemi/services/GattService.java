package no.nordicsemi.services;

import java.util.HashMap;


public abstract class GattService {

    private static HashMap<String, GattService> mServices;

    static {
        mServices = createServicesList();
    }

    public abstract String[] getTriggers();

    public static String[] getTriggersForServiceUUID(String serviceUUID) {
        return mServices.get(serviceUUID).getTriggers();
    }

    private static HashMap<String, GattService> createServicesList() {
        HashMap<String, GattService> services = new HashMap<>();

        LocationGattService locationService = new LocationGattService();
        services.put(locationService.getServiceUUID(), new LocationGattService());

        return services;
    }
}
