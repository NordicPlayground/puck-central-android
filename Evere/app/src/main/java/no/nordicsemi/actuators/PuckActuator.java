package no.nordicsemi.actuators;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.util.L;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import no.nordicsemi.R;
import no.nordicsemi.bluetooth.gatt.GattManager;
import no.nordicsemi.bluetooth.gatt.operations.GattCharacteristicWriteOperation;
import no.nordicsemi.bluetooth.gatt.operations.GattDisconnectOperation;
import no.nordicsemi.db.PuckManager;
import no.nordicsemi.models.Puck;
import no.nordicsemi.utils.NumberUtils;

public abstract class PuckActuator extends Actuator {

    public static final String ARGUMENT_UUID = "UUID";
    public static final String ARGUMENT_MAJOR = "major";
    public static final String ARGUMENT_MINOR = "minor";

    @InjectDependency
    PuckManager mPuckManager;

    @InjectDependency
    GattManager mGattManager;

    @Override
    abstract public int getId();

    public void write(BluetoothDevice device, UUID service, UUID characteristic, byte[] value) {
        mGattManager.queue(new GattCharacteristicWriteOperation(device, service, characteristic, value));
    }

    public void disconnect(BluetoothDevice device) {
        mGattManager.queue(new GattDisconnectOperation(device));
    }

    public void writeInt16Array(BluetoothDevice device, UUID service, UUID characteristic, JSONArray integers) {
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
        write(device, service, characteristic, array);
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
        actuateOnPuck(device, arguments);
    }

    public abstract void actuateOnPuck(BluetoothDevice device, JSONObject arguments) throws JSONException;
}
