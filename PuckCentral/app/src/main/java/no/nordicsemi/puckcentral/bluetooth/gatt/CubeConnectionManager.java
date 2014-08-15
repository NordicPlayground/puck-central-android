package no.nordicsemi.puckcentral.bluetooth.gatt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import org.droidparts.concurrent.task.SimpleAsyncTask;
import org.droidparts.util.L;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.puckcentral.bluetooth.gatt.operations.GattSetNotificationOperation;
import no.nordicsemi.puckcentral.models.Puck;
import no.nordicsemi.puckcentral.services.GattServices;
import no.nordicsemi.puckcentral.triggers.Trigger;

public class CubeConnectionManager {

    private final Context mCtx;
    private List<Puck> mConnectedCubes;

    GattManager mGattManager;

    public CubeConnectionManager(Context ctx, GattManager gattManager) {
        mConnectedCubes = new ArrayList<>();
        mGattManager = gattManager;
        mCtx = ctx;

        L.e("Binding characteristic change listener for cube rotates");
        mGattManager.addCharacteristicChangeListener(
                GattServices.CUBE_CHARACTERISTIC_DIRECTION_UUID,
                new CharacteristicChangeListener() {
                    @Override
                    public void onCharacteristicChanged(String deviceAddress, BluetoothGattCharacteristic characteristic) {
                        Puck cube = null;
                        for (Puck puck : mConnectedCubes) {
                            if (puck.getAddress().equals(deviceAddress)) {
                                cube = puck;
                                break;
                            }
                        }

                        if (cube == null) {
                            return;
                        }

                        int orientation = characteristic.getValue()[0];
                        L.e("Rotated cube to " + orientation);
                        final int UP = 0;
                        final int DOWN = 1;
                        final int LEFT = 2;
                        final int RIGHT = 3;
                        final int FRONT = 4;
                        final int BACK = 5;
                        switch (orientation) {
                            case UP:
                                Trigger.trigger(cube, Trigger.TRIGGER_ROTATE_CUBE_UP);
                                break;
                            case DOWN:
                                Trigger.trigger(cube, Trigger.TRIGGER_ROTATE_CUBE_DOWN);
                                break;
                            case LEFT:
                                Trigger.trigger(cube, Trigger.TRIGGER_ROTATE_CUBE_LEFT);
                                break;
                            case RIGHT:
                                Trigger.trigger(cube, Trigger.TRIGGER_ROTATE_CUBE_RIGHT);
                                break;
                            case FRONT:
                                Trigger.trigger(cube, Trigger.TRIGGER_ROTATE_CUBE_FRONT);
                                break;
                            case BACK:
                                Trigger.trigger(cube, Trigger.TRIGGER_ROTATE_CUBE_BACK);
                                break;
                        }
                    }
                });
    }


    public synchronized void checkAndConnectToPuck(final Puck puck) {
        if (mConnectedCubes.contains(puck)
                || !puck.getServiceUUIDs().contains(GattServices.CUBE_SERVICE_UUID)) {
            return;
        }

        L.i("Found Cube Puck. Initiating gatt subscribe to " + puck);
        mConnectedCubes.add(puck);

        new SimpleAsyncTask<Void>(mCtx, null) {

            @Override
            protected Void onExecute() throws Exception {
                BluetoothAdapter adapter = ((BluetoothManager) mCtx.getSystemService
                        (Context.BLUETOOTH_SERVICE)).getAdapter();
                BluetoothDevice device = adapter.getRemoteDevice(puck.getAddress());
                mGattManager.queue(new GattSetNotificationOperation(
                        device,
                        GattServices.CUBE_SERVICE_UUID,
                        GattServices.CUBE_CHARACTERISTIC_DIRECTION_UUID,
                        GattServices.CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID));
                // Android BLE stack might have issues connecting to
                // multiple Gatt services right after another.
                // See: http://stackoverflow.com/questions/21237093/android-4-3-how-to-connect-to-multiple-bluetooth-low-energy-devices
                Thread.sleep(1000);
                return null;
            }
        }.execute();
    }

    public void connectionStateChanged(GattManager.ConnectionStateChangedBundle bundle) {
        for (Puck puck : mConnectedCubes) {
            if (puck.getAddress().equals(bundle.mAddress)) {
                if (bundle.mNewState == BluetoothProfile.STATE_DISCONNECTED
                        || bundle.mNewState == 133) {
                    L.i("Disconnected from " + bundle.mAddress + ". Removing from subscribed list.");
                    mConnectedCubes.remove(puck);
                }
            }
        }
    }
}
