package no.nordicsemi.activities;

import android.app.AlertDialog;
import android.app.Dialog;
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

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.R;
import no.nordicsemi.actuators.Actuator;
import no.nordicsemi.adapters.RuleAdapter;
import no.nordicsemi.db.ActionManager;
import no.nordicsemi.db.PuckManager;
import no.nordicsemi.db.RuleManager;
import no.nordicsemi.models.Action;
import no.nordicsemi.models.Puck;
import no.nordicsemi.models.Rule;
import no.nordicsemi.services.GattService;
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
    }

    @ReceiveEvents(name = Trigger.TRIGGER_UPDATE_CLOSEST_PUCK_TV)
    public void updateTV(String _, Object toDisplay) {
        mClosestPuck.setText((String) toDisplay);
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

    boolean currentlyAddingZone = false;
    @ReceiveEvents(name = Trigger.TRIGGER_ZONE_DISCOVERED)
    public void addDiscoveredZoneModal(String _, final IBeacon iBeacon) {
        if (currentlyAddingZone) {
            return;
        }
        currentlyAddingZone = true;

        if (mPuckManager.forIBeacon(iBeacon) != null) {
            Toast.makeText(this,
                    getString(R.string.location_puck_already_paired),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: serviceUUID is used to find out which triggers a puck supports.
        // This should be fetched via the bluetooth service, as opposed to hardcoding it here.
        ArrayList<String> serviceUUIDs = new ArrayList<>();
        serviceUUIDs.add(Integer.toString(0xC175));
        final Puck newPuck = new Puck(null,
                iBeacon.getMinor(),
                iBeacon.getMajor(),
                iBeacon.getProximityUuid(),
                iBeacon.getBluetoothAddress(),
                serviceUUIDs);

        final View view = getLayoutInflater().inflate(R.layout.dialog_location_puck_add, null);
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
        String serviceUUID = rule.getPuck().getServiceUUIDs().get(0);
        final String[] triggerIdentifiers = GattService.getTriggersForServiceUUID(serviceUUID);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_trigger))
                .setItems(triggerIdentifiers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rule.setTrigger(triggerIdentifiers[which]);
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
