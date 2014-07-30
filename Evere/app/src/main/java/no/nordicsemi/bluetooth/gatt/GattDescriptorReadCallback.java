package no.nordicsemi.bluetooth.gatt;

public interface GattDescriptorReadCallback {
    void call(byte[] value);
}
