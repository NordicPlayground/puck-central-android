package no.nordicsemi.actuators;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.droidparts.util.L;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.UUID;

import no.nordicsemi.R;
import no.nordicsemi.models.Action;
import no.nordicsemi.models.Rule;
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

    @Override
    public String getDescription() {
        return "Turn on or off devices using IR";
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public AlertDialog getActuatorDialog(Context ctx, final Action action, final Rule rule, final ActuatorDialogFinishListener listener) {
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.dialog_actuator_single_textinput, null);
        final EditText editText1 = (EditText) view.findViewById(R.id.etDialogActuatorEditText1);
        editText1.setHint(CODE);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
                .setView(view)
                .setTitle(getDescription())
                .setPositiveButton(ctx.getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String arguments = Action.jsonStringBuilder(
                                ARGUMENT_HEADER, "[4500, 4500]",
                                ARGUMENT_ONE, "[560, 1680]",
                                ARGUMENT_ZERO, "[560, 560]",
                                ARGUMENT_PTRAIL, "560",
                                ARGUMENT_PREDATA, "E0E0",
                                ARGUMENT_CODE, editText1.getText());

                        action.setArguments(arguments);
                        rule.setAction(action);
                        listener.onActuatorDialogFinish(action, rule);
                    }
                })
                .setNegativeButton(ctx.getString(R.string.reject), null);
        return builder.create();
    }

    @Override
    public void actuateOnPuck(BluetoothGatt gatt, JSONObject arguments) throws JSONException {
        BluetoothGattService service = gatt.getService(UUIDUtils.stringToUUID("bftj ir         "));
        BluetoothGattCharacteristic characteristic;
        if(arguments.has(ARGUMENT_HEADER)) {
            characteristic = service.getCharacteristic(UUIDUtils.stringToUUID("bftj ir header  "));
            writeInt16Array(gatt, characteristic, (JSONArray) arguments.get(ARGUMENT_HEADER));
        }
        if(arguments.has(ARGUMENT_ONE)) {
            characteristic = service.getCharacteristic(UUIDUtils.stringToUUID("bftj ir one     "));
            writeInt16Array(gatt, characteristic, (JSONArray) arguments.get(ARGUMENT_ONE));
        }
        if(arguments.has(ARGUMENT_ZERO)) {
            characteristic = service.getCharacteristic(UUIDUtils.stringToUUID("bftj ir zero    "));
            writeInt16Array(gatt, characteristic, (JSONArray) arguments.get(ARGUMENT_ZERO));
        }
        if(arguments.has(ARGUMENT_PTRAIL)) {
            characteristic = service.getCharacteristic(UUIDUtils.stringToUUID("bftj ir ptrail  "));
            byte[] array = NumberUtils.stringNumberToByteArray("" + arguments.get(ARGUMENT_PTRAIL), 10, 2);
            write(gatt, characteristic, array);
        }
        if(arguments.has(ARGUMENT_PREDATA)) {
            characteristic = service.getCharacteristic(UUIDUtils.stringToUUID("bftj ir predata "));
            byte[] array = NumberUtils.stringNumberToByteArray((String) arguments.get(ARGUMENT_PREDATA), 16, 2);
            write(gatt, characteristic, array);
        }
        if(arguments.has(ARGUMENT_CODE)) {
            characteristic = service.getCharacteristic(UUIDUtils.stringToUUID("bftj ir code    "));
            byte[] array = NumberUtils.stringNumberToByteArray((String) arguments.get(ARGUMENT_CODE), 16, 2);
            write(gatt, characteristic, array);

        }
    }
}
