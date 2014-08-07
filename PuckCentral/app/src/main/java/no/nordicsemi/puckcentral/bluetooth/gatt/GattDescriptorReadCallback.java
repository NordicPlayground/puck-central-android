package no.nordicsemi.puckcentral.bluetooth.gatt;

public interface GattDescriptorReadCallback {
    void call(byte[] value);
}
