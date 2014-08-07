package no.nordicsemi.puckcentral.actuators;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.droidparts.activity.Activity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import no.nordicsemi.puckcentral.R;
import no.nordicsemi.puckcentral.bluetooth.gatt.GattOperationBundle;
import no.nordicsemi.puckcentral.bluetooth.gatt.operations.GattCharacteristicWriteOperation;
import no.nordicsemi.puckcentral.bluetooth.gatt.operations.GattDisconnectOperation;
import no.nordicsemi.puckcentral.models.Action;
import no.nordicsemi.puckcentral.models.Puck;
import no.nordicsemi.puckcentral.models.Rule;
import no.nordicsemi.puckcentral.services.GattServices;
import no.nordicsemi.puckcentral.utils.UUIDUtils;

public class IRActuator extends PuckActuator {

    public static final String ARGUMENT_CODE = "code";

    public static final String CODE = "Remote control code";
    public static final UUID CHARACTERISTIC_COMMAND_UUID = UUIDUtils.stringToUUID("bftj ir command ");
    public static final UUID CHARACTERISTIC_DATA_UUID = UUIDUtils.stringToUUID("bftj ir data    ");
    public static final UUID CHARACTERISTIC_PERIOD_UUID = UUIDUtils.stringToUUID("bftj ir period  ");

    private static final byte COMMAND_BEGIN_CODE_TRANSMISSION = 0;
    private static final byte COMMAND_END_CODE_TRANSMISSION = 1;

    @Override
    public String getDescription() {
        return "Turn on or off devices using IR";
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public AlertDialog getActuatorDialog(Activity activity, final Action action, final Rule rule, final ActuatorDialogFinishListener listener) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.dialog_actuator_single_select_single_textinput, null);
        final EditText editText1 = (EditText) view.findViewById(R.id.etDialogActuatorEditText1);
        editText1.setHint(CODE);

        final List<Puck> irPucks = mPuckManager.withServiceUUID(GattServices.IR_SERVICE_UUID);

        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner1);
        List<String> irPuckNames = new ArrayList<>();
        for (Puck irPuck : irPucks) {
            irPuckNames.add(irPuck.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item, irPuckNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setView(view)
                .setTitle(getDescription())
                .setPositiveButton(activity.getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int idx = spinner.getSelectedItemPosition();
                        Puck puck = irPucks.get(idx);

                        String arguments = Action.jsonStringBuilder(
                                IRActuator.ARGUMENT_UUID, puck.getProximityUUID(),
                                IRActuator.ARGUMENT_MAJOR, puck.getMajor(),
                                IRActuator.ARGUMENT_MINOR, puck.getMinor(),
                                IRActuator.ARGUMENT_CODE, editText1.getText().toString());

                        action.setArguments(arguments);
                        rule.addAction(action);
                        listener.onActuatorDialogFinish(action, rule);
                    }
                })
                .setNegativeButton(activity.getString(R.string.reject), null);
        return builder.create();
    }

    @Override
    public void actuateOnPuck(BluetoothDevice device, JSONObject arguments) throws JSONException {
        GattOperationBundle bundle = new GattOperationBundle();
        bundle.addOperation(new GattCharacteristicWriteOperation(
                device,
                GattServices.IR_SERVICE_UUID,
                CHARACTERISTIC_PERIOD_UUID,
                new byte[]{ 26 } ));
        bundle.addOperation(new GattCharacteristicWriteOperation(
                device,
                GattServices.IR_SERVICE_UUID,
                CHARACTERISTIC_COMMAND_UUID,
                new byte[] { COMMAND_BEGIN_CODE_TRANSMISSION }));

        int ON = 1260;
        int E = 420;
        int ZE = 420;
        int RO = 1260;
        int PAU = 0;
        int SE = 20 * 1680;

        int source[] = new int[] {
                ON,E,    ON,E,    ON,E,    ON,E,
                ZE,RO,   ZE,RO,   ZE,RO,   ZE,RO,
                ZE,RO,   ON,E,    ZE,RO,   ZE,RO,

                PAU,SE,

                ON,E,    ON,E,    ON,E,    ON,E,
                ZE,RO,   ZE,RO,   ZE,RO,   ZE,RO,
                ZE,RO,   ON,E,    ZE,RO,   ZE,RO
        };

        assert(source.length % 10 == 0);

        byte array[] = new byte[20];
        for(int i = 0; i < source.length; i++) {
            array[(i % 10) * 2] = (byte) ((source[i] & 0xFF00) >> 8);
            array[(i % 10) * 2 + 1] = (byte) (source[i] & 0xFF);
            if(i % 10 == 9) {
                bundle.addOperation(new GattCharacteristicWriteOperation(device,
                        GattServices.IR_SERVICE_UUID,
                        CHARACTERISTIC_DATA_UUID, array));
                array = new byte[20];
            }
        }
        bundle.addOperation(new GattCharacteristicWriteOperation(device,
                GattServices.IR_SERVICE_UUID,
                CHARACTERISTIC_COMMAND_UUID,
                new byte[] { COMMAND_END_CODE_TRANSMISSION }));
        bundle.addOperation(new GattDisconnectOperation(device));
        mGattManager.queue(bundle);
    }
}
