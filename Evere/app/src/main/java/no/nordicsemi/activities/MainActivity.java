package no.nordicsemi.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.radiusnetworks.ibeacon.IBeacon;

import org.droidparts.activity.Activity;
import org.droidparts.annotation.bus.ReceiveEvents;
import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.annotation.inject.InjectView;
import org.droidparts.concurrent.task.AsyncTaskResultListener;
import org.droidparts.concurrent.task.SimpleAsyncTask;
import org.droidparts.util.L;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import no.nordicsemi.R;
import no.nordicsemi.actuators.Actuator;
import no.nordicsemi.adapters.RuleAdapter;
import no.nordicsemi.db.ActionManager;
import no.nordicsemi.db.PuckManager;
import no.nordicsemi.db.RuleManager;
import no.nordicsemi.enums.CubeOrientation;
import no.nordicsemi.models.Action;
import no.nordicsemi.models.Puck;
import no.nordicsemi.models.Rule;
import no.nordicsemi.services.GattServices;
import no.nordicsemi.triggers.Trigger;


public class MainActivity extends Activity {

    @InjectView(id = R.id.lvRules)
    ListView mLvRules;

    @InjectView(id = R.id.tvClosestPuck)
    TextView mClosestPuck;

    @InjectDependency
    private ActionManager mActionManager;

    @InjectDependency
    private RuleManager mRuleManager;

    @InjectDependency
    private PuckManager mPuckManager;

    private RuleAdapter mRuleAdapter;

    @Override
    public void onPreInject() {
        super.onPreInject();
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRuleAdapter = new RuleAdapter(this, mRuleManager.select());
        mLvRules.setAdapter(mRuleAdapter);

        bindBluetoothListeners();
    }

    public void bindBluetoothListeners() {
        final List<Rule> rules = mRuleManager.getRulesForTriggers(
                Trigger.TRIGGER_ROTATE_CUBE_UP,
                Trigger.TRIGGER_ROTATE_CUBE_DOWN,
                Trigger.TRIGGER_ROTATE_CUBE_LEFT,
                Trigger.TRIGGER_ROTATE_CUBE_RIGHT,
                Trigger.TRIGGER_ROTATE_CUBE_FRONT,
                Trigger.TRIGGER_ROTATE_CUBE_BACK);

        // Multiple rules may point to the same puck,
        // no need to bind up duplicate listeners.
        final Set<Puck> puckSet = new HashSet<>();
        for (Rule rule : rules) {
            puckSet.add(rule.getPuck());
        }

        new SimpleAsyncTask<Void>(this, null) {
            @Override
            protected Void onExecute() throws Exception {
                for (Puck puck : puckSet) {
                    bindBluetoothListener(puck);
                    // Android BLE stack might have issues connecting to
                    // multiple Gatt services right after another.
                    // See: http://stackoverflow.com/questions/21237093/android-4-3-how-to-connect-to-multiple-bluetooth-low-energy-devices
                    Thread.sleep(1000);
                }
                return null;
            }
        }.execute();
    }

    public void bindBluetoothListener(final Puck puck) {
        L.e("Binding GATT callback to %s (%s)", puck.getName(), puck.getAddress());
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager) getSystemService
                (Context.BLUETOOTH_SERVICE)).getAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(puck.getAddress());

        // AutoConnect allows us to bind once, with BLE stack handling the rest for us.
        device.connectGatt(this, true, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    L.e("Connected to %s (%s)", puck.getName(), puck.getAddress());
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    L.e("Disconnected from %s (%s)", puck.getName(), puck.getAddress());
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);

                BluetoothGattCharacteristic directionCharacteristic = gatt
                        .getService(GattServices.CUBE_SERVICE_UUID)
                        .getCharacteristic(GattServices.CUBE_CHARACTERISTIC_DIRECTION_UUID);
                gatt.setCharacteristicNotification(directionCharacteristic, true);

                BluetoothGattDescriptor descriptor =
                        directionCharacteristic.getDescriptor(
                                GattServices.CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
                L.e("Listener bound to %s (%s)", puck.getName(), puck.getAddress());
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);

                final int idx = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                CubeOrientation orientation = CubeOrientation.values()[idx];

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                puck.getName() + " rotation: " + idx,
                                Toast.LENGTH_SHORT).show();
                    }
                });

                switch (orientation) {
                    case UP:
                        Trigger.trigger(puck, Trigger.TRIGGER_ROTATE_CUBE_UP);
                        break;

                    case DOWN:
                        Trigger.trigger(puck, Trigger.TRIGGER_ROTATE_CUBE_DOWN);
                        break;

                    case LEFT:
                        Trigger.trigger(puck, Trigger.TRIGGER_ROTATE_CUBE_LEFT);
                        break;

                    case RIGHT:
                        Trigger.trigger(puck, Trigger.TRIGGER_ROTATE_CUBE_RIGHT);
                        break;

                    case FRONT:
                        Trigger.trigger(puck, Trigger.TRIGGER_ROTATE_CUBE_FRONT);
                        break;

                    case BACK:
                        Trigger.trigger(puck, Trigger.TRIGGER_ROTATE_CUBE_BACK);
                        break;

                    case UNDEFINED:
                        break;
                }
            }
        });
    }

    @ReceiveEvents(name = Trigger.TRIGGER_UPDATE_CLOSEST_PUCK_TV)
    public void updateTV(String _, Object toDisplay) {
        mClosestPuck.setText(String.valueOf(toDisplay));
    }

    @ReceiveEvents(name = Trigger.TRIGGER_ADD_ACTUATOR_FOR_EXISTING_RULE)
    public void addActuatorForExistingRule(String _, Object rule) {
        selectActuatorDialog((Rule) rule);
    }

    @ReceiveEvents(name = Trigger.TRIGGER_REMOVE_RULE)
    public void removeRule(String _, final Object rule) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.rule_remove)
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRuleAdapter.delete((Rule) rule);
                    }
                })
                .setNegativeButton(getString(R.string.abort), null);
        builder.create().show();
    }

    public void removePuck() {
        final List<Puck> puckList = mPuckManager.getAll();
        if (puckList.size() == 0) {
            Toast.makeText(this, getString(R.string.no_pucks_added), Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> puckNames = new ArrayList<>();
        for (Puck puck : puckList) {
            puckNames.add(puck.getName());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.puck_remove))
                .setItems(puckNames.toArray(new CharSequence[puckNames.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Puck puck = puckList.get(i);
                        mRuleManager.deteRulesWithPuckId(puck.id);
                        mRuleAdapter.requeryData();
                        mPuckManager.delete(puck.id);
                    }
                })
                .setNegativeButton(getString(R.string.abort), null);

        builder.create().show();
    }

    boolean currentlyAddingZone = false;
    @ReceiveEvents(name = Trigger.TRIGGER_ZONE_DISCOVERED)
    public void createDiscoveredZoneModal(String _, final IBeacon iBeacon) {
        if (currentlyAddingZone) {
            return;
        } else if (mPuckManager.forIBeacon(iBeacon) != null) {
            Toast.makeText(this,
                    getString(R.string.location_puck_already_paired),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        currentlyAddingZone = true;

        ArrayList<UUID> defaultServiceUUIDs = new ArrayList<>();
        defaultServiceUUIDs.add(GattServices.LOCATION_SERVICE_UUID);
        final Puck newPuck = new Puck(null,
                iBeacon.getMinor(),
                iBeacon.getMajor(),
                iBeacon.getProximityUuid(),
                iBeacon.getBluetoothAddress(),
                defaultServiceUUIDs);

        final View view = getLayoutInflater().inflate(R.layout.dialog_location_puck_add, null, false);
        ((TextView) view.findViewById(R.id.tvLocationPuckIdentifier)).setText(newPuck.getFormattedUUID());

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle(getString(R.string.puck_discovered_dialog_title))
                .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String locationPuckName = ((TextView) view.findViewById(R.id
                                .etLocationPuckName)).getText().toString();
                        newPuck.setName(locationPuckName);
                        mPuckManager.create(newPuck);

                        new FetchPuckServices(MainActivity.this, null, newPuck).execute();
                    }
                })
                .setNegativeButton(getString(R.string.reject), null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        currentlyAddingZone = false;
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Fetches gatt service UUIDs for a given puck,
     * and adds themthe pucks list of serviceUUIDs.
     *
     * If a puck advertises as non-connectable, the onServicesDiscovered callback
     * will (propably) never be triggered, not updating the puck.
     *
     * This approach was chosen as android provides no way to check if a BLE device
     * is connectable or not (that i could find).
     */
    private class FetchPuckServices extends SimpleAsyncTask<Void> {
        private Puck mPuck;

        public FetchPuckServices(Context ctx, AsyncTaskResultListener<Void> resultListener, Puck puck) {
            super(ctx, resultListener);
            mPuck = puck;
        }

        @Override
        protected Void onExecute() throws Exception {
            BluetoothAdapter bluetoothAdapter = ((BluetoothManager) getSystemService(Context
                    .BLUETOOTH_SERVICE)).getAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mPuck.getAddress());
            L.e("Starting service discovery");
            device.connectGatt(MainActivity.this, false, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);

                    L.e("Got status " + status + " and state " + newState);
                    // Catch-all for a variety of undocumented error codes.
                    // Documented at https://code.google.com/r/naranjomanuel-opensource-broadcom-ble/source/browse/api/java/src/com/broadcom/bt/le/api/BleConstants.java?r=f535f31ec89eb3076a2b75ddf586f4b3fc44384b
                    if (status != BluetoothGatt.GATT_SUCCESS) {
                        L.e("Ouch! Disconnecting! status: " + status + " newState " + newState);
                        gatt.disconnect();
                        return;
                    }

                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        L.e("Connected to service!");
                        gatt.discoverServices();
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        L.e("Link disconnected");
                    } else {
                        L.e("Received something else, ");
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    ArrayList<UUID> serviceUUIDs = mPuck.getServiceUUIDs();

                    for (BluetoothGattService service : gatt.getServices()) {
                        serviceUUIDs.add(service.getUuid());
                    }
                    mPuck.setServiceUUIDs(serviceUUIDs);
                    mPuckManager.update(mPuck);
                    L.e("Now has services: " + mPuck.getServiceUUIDs());
                    gatt.disconnect();
                }
            });
            return null;
        }
    }

    public void selectDeviceDialog() {
        final List<Puck> puckList = mPuckManager.getAll();
        if (puckList.size() == 0) {
            Toast.makeText(this, getString(R.string.no_pucks_added), Toast.LENGTH_SHORT).show();
            return;
        }

        String[] names = new String[puckList.size()];
        for (int i=0; i< puckList.size(); i++) {
            names[i] = puckList.get(i).getName();
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_puck))
                .setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Rule rule = new Rule();
                        rule.setPuck(puckList.get(which));
                        selectTriggerDialog(rule);
                    }
                })
                .setNegativeButton(getString(R.string.abort), null);

        builder.create().show();
    }

    public void selectTriggerDialog(final Rule rule) {
        ArrayList<UUID> serviceUUIDs = rule.getPuck().getServiceUUIDs();
        final String[] triggers = GattServices.getTriggersForServiceUUIDs(serviceUUIDs);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_trigger))
                .setItems(triggers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rule.setTrigger(triggers[which]);
                        selectActuatorDialog(rule);
                    }
                })
                .setNegativeButton(getString(R.string.abort), null);

        builder.create().show();
    }

    public void selectActuatorDialog(final Rule rule) {
        final ArrayList<Actuator> actuators = Actuator.getActuators();
        String[] actuatorDescriptions = new String[actuators.size()];
        for (int i=0; i< actuators.size(); i++) {
            actuatorDescriptions[i] = actuators.get(i).getDescription();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_actuator))
                .setItems(actuatorDescriptions,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Action action = new Action(
                                        actuators.get(which).getId(),
                                        null);

                                Dialog actuatorDialog = actuators.get(which)
                                        .getActuatorDialog(MainActivity.this, action, rule, new Actuator.ActuatorDialogFinishListener() {
                                            @Override
                                            public void onActuatorDialogFinish(Action action, Rule rule) {
                                                mActionManager.create(action);
                                                mRuleAdapter.createOrUpdate(rule);
                                            }
                                        });

                                actuatorDialog.show();
                            }
                        }
                )
                .setNegativeButton(getString(R.string.abort), null);

        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;

            case R.id.action_add_rule:
                selectDeviceDialog();
                return true;

            case R.id.action_remove_puck:
                removePuck();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
