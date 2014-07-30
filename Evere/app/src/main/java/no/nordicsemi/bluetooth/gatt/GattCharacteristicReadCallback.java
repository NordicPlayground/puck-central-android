package no.nordicsemi.bluetooth.gatt;

public interface GattCharacteristicReadCallback {
    void call(byte[] characteristic);
}
