package no.nordicsemi.puckcentral.bluetooth.gatt.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import org.droidparts.util.L;

import java.util.UUID;

public class GattCharacteristicWriteOperation extends GattOperation {

    private final UUID mService;
    private final UUID mCharacteristic;
    private final byte[] mValue;

    public GattCharacteristicWriteOperation(BluetoothDevice device, UUID service, UUID characteristic, byte[] value) {
        super(device);
        mService = service;
        mCharacteristic = characteristic;
        mValue = value;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        L.d("writing to " + mCharacteristic);
        BluetoothGattCharacteristic characteristic = gatt.getService(mService).getCharacteristic(mCharacteristic);
        characteristic.setValue(mValue);
        gatt.writeCharacteristic(characteristic);
    }

    @Override
    public boolean hasAvailableCompletionCallback() {
        return true;
    }
}
