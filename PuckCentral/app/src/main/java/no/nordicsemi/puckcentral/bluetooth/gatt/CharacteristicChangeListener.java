package no.nordicsemi.puckcentral.bluetooth.gatt;

import android.bluetooth.BluetoothGattCharacteristic;

public interface CharacteristicChangeListener {
    public void onCharacteristicChanged(String deviceAddress, BluetoothGattCharacteristic characteristic);
}
