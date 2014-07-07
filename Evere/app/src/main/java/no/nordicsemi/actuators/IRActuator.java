package no.nordicsemi.actuators;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import no.nordicsemi.R;
import no.nordicsemi.models.Action;
import no.nordicsemi.models.Puck;
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
    public static final UUID SERVICE_UUID = UUIDUtils.stringToUUID("bftj ir         ");
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
                        Puck p = rule.getPuck();

                        String arguments = Action.jsonStringBuilder(
                                IRActuator.ARGUMENT_UUID, p.getProximityUUID(),
                                IRActuator.ARGUMENT_MAJOR, p.getMajor(),
                                IRActuator.ARGUMENT_MINOR, p.getMinor(),
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
                .setNegativeButton(ctx.getString(R.string.reject), null);
        return builder.create();
    }

    @Override
    public void actuateOnPuck(BluetoothGatt gatt, JSONObject arguments) throws JSONException {
        BluetoothGattService service = gatt.getService(SERVICE_UUID);
        BluetoothGattCharacteristic characteristic;
        if(arguments.has(ARGUMENT_HEADER)) {
            characteristic = service.getCharacteristic(CHARACTERISTIC_HEADER_UUID);
            writeInt16Array(gatt, characteristic, arguments.getJSONArray(ARGUMENT_HEADER));
        }
        if(arguments.has(ARGUMENT_ONE)) {
            characteristic = service.getCharacteristic(CHARACTERISTIC_ONE_UUID);
            writeInt16Array(gatt, characteristic, arguments.getJSONArray(ARGUMENT_ONE));
        }
        if(arguments.has(ARGUMENT_ZERO)) {
            characteristic = service.getCharacteristic(CHARACTERISTIC_ZERO_UUID);
            writeInt16Array(gatt, characteristic, arguments.getJSONArray(ARGUMENT_ZERO));
        }
        if(arguments.has(ARGUMENT_PTRAIL)) {
            characteristic = service.getCharacteristic(CHARACTERISTIC_PTRAIL_UUID);
            byte[] array = NumberUtils.stringNumberToByteArray("" + arguments.get(ARGUMENT_PTRAIL), 10, 2);
            write(gatt, characteristic, array);
        }
        if(arguments.has(ARGUMENT_PREDATA)) {
            characteristic = service.getCharacteristic(CHARACTERISTIC_PREDATA_UUID);
            byte[] array = NumberUtils.stringNumberToByteArray(arguments.getString(ARGUMENT_PREDATA), 16, 2);
            write(gatt, characteristic, array);
        }
        if(arguments.has(ARGUMENT_CODE)) {
            characteristic = service.getCharacteristic(CHARACTERISTIC_CODE_UUID);
            byte[] array = NumberUtils.stringNumberToByteArray(arguments.getString(ARGUMENT_CODE), 16, 2);
            write(gatt, characteristic, array);

        }
    }
}
