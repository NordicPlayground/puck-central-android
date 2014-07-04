package no.nordicsemi.actuators;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.util.L;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import no.nordicsemi.R;
import no.nordicsemi.bluetooth.GattCharacteristicWriteOperation;
import no.nordicsemi.db.PuckManager;
import no.nordicsemi.models.Puck;
import no.nordicsemi.utils.NumberUtils;

public abstract class PuckActuator extends Actuator {

    public static final String ARGUMENT_UUID = "UUID";
    public static final String ARGUMENT_MAJOR = "major";
    public static final String ARGUMENT_MINOR = "minor";

    @InjectDependency
    PuckManager mPuckManager;

    LinkedList<GattCharacteristicWriteOperation> writeQueue;

    @Override
    abstract public int getId();

    public PuckActuator() {
        super();
        writeQueue = new LinkedList<>();
    }

    public void write(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] value) {
        writeQueue.add(new GattCharacteristicWriteOperation(gatt, characteristic, value));
        if (writeQueue.size() == 1) {
            driveWriteQueue();
        }
    }

    public void writeInt16Array(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, JSONArray integers) {
        if (integers.length() > 10) {
            throw new IllegalArgumentException(mContext.getString(R.string.error_too_many_integers));
        }
        byte[] array = new byte[integers.length() * 2];
        int index = 0;
        for (int i = 0; i < integers.length(); i++) {
            byte[] temp;
            try {
                temp = NumberUtils.stringNumberToByteArray(integers.getString(i), 10, 2);
                System.arraycopy(temp, 0, array, index, temp.length);
                index += temp.length;
            } catch (JSONException e) {
                L.e(e);
            }
        }
        write(gatt, characteristic, array);
    }

    private void driveWriteQueue() {
        if(!writeQueue.isEmpty()) {
            GattCharacteristicWriteOperation write = writeQueue.peek();
            BluetoothGatt gatt = write.getGatt();
            BluetoothGattCharacteristic nextWriteCharacteristic = write.getCharacteristic();
            nextWriteCharacteristic.setValue(write.getValue());
            gatt.writeCharacteristic(nextWriteCharacteristic);
        }
    }

    @Override
    void actuate(final JSONObject arguments) throws JSONException {
        String UUID = arguments.getString(ARGUMENT_UUID);
        int major = arguments.getInt(ARGUMENT_MAJOR);
        int minor = arguments.getInt(ARGUMENT_MINOR);
        Puck puck = mPuckManager.read(UUID, major, minor);

        BluetoothAdapter bluetoothAdapter = ((BluetoothManager) mContext.getSystemService
                (Context.BLUETOOTH_SERVICE)).getAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(puck.getAddress());
        device.connectGatt(mContext, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices();
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                writeQueue.removeFirst();
                driveWriteQueue();
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                try {
                    actuateOnPuck(gatt, arguments);
                } catch (JSONException e) {
                    L.e(e);
                }
            }
        });
    }

    public abstract void actuateOnPuck(BluetoothGatt bluetoothGatt, JSONObject arguments) throws JSONException;
}
