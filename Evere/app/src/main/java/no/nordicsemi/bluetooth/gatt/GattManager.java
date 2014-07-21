package no.nordicsemi.bluetooth.gatt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;

import org.droidparts.Injector;
import org.droidparts.util.L;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import no.nordicsemi.bluetooth.gatt.operations.GattOperation;

public class GattManager {

    private ConcurrentLinkedQueue<GattOperation> mQueue;
    private ConcurrentHashMap<String, BluetoothGatt> mGatts;
    private GattOperation mCurrentOperation;
    private HashMap<UUID, ArrayList<CharacteristicChangeListener>> mCharacteristicChangeListeners;

    public GattManager() {
        mQueue = new ConcurrentLinkedQueue<>();
        mGatts = new ConcurrentHashMap<>();
        mCurrentOperation = null;
        mCharacteristicChangeListeners = new HashMap<>();
    }

    public synchronized void queue(GattOperation gattOperation) {
        mQueue.add(gattOperation);
        L.v("Queueing Gatt operation, size will now become: " + mQueue.size());
        if(mQueue.size() == 1) {
            drive();
        }
    }

    private void drive() {
        if(mCurrentOperation != null) {
            L.e("tried to drive, but currentOperation was not null, " + mCurrentOperation);
            return;
        }
        if( mQueue.size() == 0) {
            L.v("Queue empty, drive loop stopped.");
            mCurrentOperation = null;
            return;
        }

        final GattOperation operation = mQueue.poll();
        L.v("Driving Gatt queue, size will now become: " + mQueue.size());
        setCurrentOperation(operation);
        final BluetoothDevice device = operation.getDevice();
        if(mGatts.containsKey(device.getAddress())) {
            operation.execute(mGatts.get(device.getAddress()));
        } else {
            device.connectGatt(Injector.getApplicationContext(), false, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);

                    if(status == 133) {
                        L.e("Got the status 133 bug, closing gatt");
                        gatt.close();
                        mGatts.remove(device.getAddress());
                        return;
                    }
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        L.d("Gatt connected to device " + device.getAddress());
                        mGatts.put(device.getAddress(), gatt);
                        gatt.discoverServices();
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        L.d("Gatt not connected to device " + device.getAddress() + ", newState: " + newState);
                        mGatts.remove(device.getAddress());
                        setCurrentOperation(null);
                        gatt.close();
                        drive();
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    L.d("services discovered, status: " + status);
                    operation.execute(gatt);
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                    L.d("Characteristic " + characteristic.getUuid() + "written to on device " + device.getAddress());
                    setCurrentOperation(null);
                    drive();
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    L.i("Characteristic " + characteristic.getUuid() + "was changed, device: " + device.getAddress());
                    if(mCharacteristicChangeListeners.containsKey(characteristic.getUuid())) {
                        for (CharacteristicChangeListener listener : mCharacteristicChangeListeners.get(characteristic.getUuid())) {
                            listener.onCharacteristicChanged(characteristic);
                        }
                    }
                }
            });
        }
    }

    public synchronized void setCurrentOperation(GattOperation currentOperation) {
        mCurrentOperation = currentOperation;
    }

    public BluetoothGatt getGatt(BluetoothDevice device) {
        return mGatts.get(device);
    }

    public void addCharacteristicChangeListener(UUID characteristicUuid, CharacteristicChangeListener characteristicChangeListener) {
        if(!mCharacteristicChangeListeners.containsKey(characteristicUuid)) {
            mCharacteristicChangeListeners.put(characteristicUuid, new ArrayList<CharacteristicChangeListener>());
        }
        mCharacteristicChangeListeners.get(characteristicUuid).add(characteristicChangeListener);
    }
}
