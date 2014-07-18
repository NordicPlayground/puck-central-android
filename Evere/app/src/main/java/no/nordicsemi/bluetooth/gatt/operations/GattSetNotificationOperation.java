package no.nordicsemi.bluetooth.gatt.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import org.droidparts.Injector;
import org.droidparts.annotation.inject.InjectDependency;

import java.util.UUID;
import java.util.concurrent.Callable;

import no.nordicsemi.bluetooth.gatt.CharacteristicChangeListener;
import no.nordicsemi.bluetooth.gatt.GattManager;

public class GattSetNotificationOperation extends GattOperation {

    @InjectDependency
    GattManager mGattManager;

    private final UUID mServiceUuid;
    private final UUID mCharacteristicUuid;
    private final UUID mDescriptorUuid;
    private CharacteristicChangeListener mCharacteristicChangeListener;

    public GattSetNotificationOperation(BluetoothDevice device, UUID serviceUuid, UUID characteristicUuid, UUID descriptorUuid, CharacteristicChangeListener characteristicChangeListener) {
        super(device);
        Injector.inject(Injector.getApplicationContext(), this);
        mServiceUuid = serviceUuid;
        mCharacteristicUuid = characteristicUuid;
        mDescriptorUuid = descriptorUuid;
        mCharacteristicChangeListener = characteristicChangeListener;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        BluetoothGattCharacteristic characteristic = gatt.getService(mServiceUuid).getCharacteristic(mCharacteristicUuid);
        boolean enable = true;
        gatt.setCharacteristicNotification(characteristic, enable);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(mDescriptorUuid);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
        mGattManager.addCharacteristicChangeListener(mCharacteristicUuid, mCharacteristicChangeListener);
    }
}
