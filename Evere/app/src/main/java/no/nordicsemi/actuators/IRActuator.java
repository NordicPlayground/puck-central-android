package no.nordicsemi.actuators;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
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

import no.nordicsemi.R;
import no.nordicsemi.models.Action;
import no.nordicsemi.models.Puck;
import no.nordicsemi.models.Rule;
import no.nordicsemi.services.GattServices;
import no.nordicsemi.utils.NumberUtils;
import no.nordicsemi.utils.UUIDUtils;

public class IRActuator extends PuckActuator {

    public static final String ARGUMENT_HEADER = "header";
    public static final String ARGUMENT_ONE = "one";
    public static final String ARGUMENT_ZERO = "zero";
    public static final String ARGUMENT_PTRAIL = "ptrail";
    public static final String ARGUMENT_PREDATA = "predata";
    public static final String ARGUMENT_CODE = "code";

    public static final String CODE = "Remote control code";
    public static final UUID CHARACTERISTIC_HEADER_UUID = UUIDUtils.stringToUUID("bftj ir header  ");
    public static final UUID CHARACTERISTIC_ONE_UUID = UUIDUtils.stringToUUID("bftj ir one     ");
    public static final UUID CHARACTERISTIC_ZERO_UUID = UUIDUtils.stringToUUID("bftj ir zero    ");
    public static final UUID CHARACTERISTIC_PTRAIL_UUID = UUIDUtils.stringToUUID("bftj ir ptrail  ");
    public static final UUID CHARACTERISTIC_PREDATA_UUID = UUIDUtils.stringToUUID("bftj ir predata ");
    public static final UUID CHARACTERISTIC_CODE_UUID = UUIDUtils.stringToUUID("bftj ir code    ");

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
                                IRActuator.ARGUMENT_HEADER, new int[]{9000, 4500},
                                IRActuator.ARGUMENT_ONE, new int[]{560, 1680},
                                IRActuator.ARGUMENT_ZERO, new int[]{560, 560},
                                IRActuator.ARGUMENT_PTRAIL, 560,
                                IRActuator.ARGUMENT_PREDATA, "E0E0",
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
        if(arguments.has(ARGUMENT_HEADER)) {
            writeInt16Array(device, GattServices.IR_SERVICE_UUID, CHARACTERISTIC_HEADER_UUID, arguments.getJSONArray(ARGUMENT_HEADER));
        }
        if(arguments.has(ARGUMENT_ONE)) {
            writeInt16Array(device, GattServices.IR_SERVICE_UUID, CHARACTERISTIC_ONE_UUID, arguments.getJSONArray(ARGUMENT_ONE));
        }
        if(arguments.has(ARGUMENT_ZERO)) {
            writeInt16Array(device, GattServices.IR_SERVICE_UUID, CHARACTERISTIC_ZERO_UUID, arguments.getJSONArray(ARGUMENT_ZERO));
        }
        if(arguments.has(ARGUMENT_PTRAIL)) {
            byte[] array = NumberUtils.stringNumberToByteArray("" + arguments.get(ARGUMENT_PTRAIL), 10, 2);
            write(device, GattServices.IR_SERVICE_UUID, CHARACTERISTIC_PTRAIL_UUID, array);
        }
        if(arguments.has(ARGUMENT_PREDATA)) {
            byte[] array = NumberUtils.stringNumberToByteArray(arguments.getString(ARGUMENT_PREDATA), 16, 2);
            write(device, GattServices.IR_SERVICE_UUID, CHARACTERISTIC_PREDATA_UUID, array);
        }
        if(arguments.has(ARGUMENT_CODE)) {
            byte[] array = NumberUtils.stringNumberToByteArray(arguments.getString(ARGUMENT_CODE), 16, 2);
            write(device, GattServices.IR_SERVICE_UUID, CHARACTERISTIC_CODE_UUID, array);
        }
        disconnect(device);
    }
}
