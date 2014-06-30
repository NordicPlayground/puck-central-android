package no.nordicsemi.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class GattCharacteristicWriteOperation {

    private BluetoothGatt mGatt;
    private BluetoothGattCharacteristic mCharacteristic;
    private byte[] mValue;

    public GattCharacteristicWriteOperation(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] value) {
        mGatt = gatt;
        mCharacteristic = characteristic;
        mValue = value;
    }

    public BluetoothGatt getGatt() {
        return mGatt;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return mCharacteristic;
    }

    public byte[] getValue() {
        return mValue;
    }
}
