package no.nordicsemi.bluetooth.gatt;

import android.bluetooth.BluetoothGattCharacteristic;

public interface CharacteristicChangeListener {
    public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic);
}
