package no.nordicsemi.puckcentral.bluetooth.gatt;

public interface GattCharacteristicReadCallback {
    void call(byte[] characteristic);
}
