package no.nordicsemi.puckcentral.bluetooth.gatt.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import org.droidparts.util.L;

import java.util.UUID;

public class GattDescriptorWriteOperation extends GattOperation {

    private final UUID mService;
    private final UUID mCharacteristic;
    private final UUID mDescriptor;

    public GattDescriptorWriteOperation(BluetoothDevice device, UUID service, UUID characteristic, UUID descriptor) {
        super(device);
        mService = service;
        mCharacteristic = characteristic;
        mDescriptor = descriptor;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        L.d("Writing to " + mDescriptor);
        BluetoothGattDescriptor descriptor = gatt.getService(mService).getCharacteristic(mCharacteristic).getDescriptor(mDescriptor);
        gatt.writeDescriptor(descriptor);
    }

    @Override
    public boolean hasAvailableCompletionCallback() {
        return true;
    }
}
