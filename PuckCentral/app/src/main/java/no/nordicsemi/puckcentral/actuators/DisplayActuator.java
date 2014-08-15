package no.nordicsemi.puckcentral.actuators;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.droidparts.activity.Activity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import no.nordicsemi.puckcentral.R;
import no.nordicsemi.puckcentral.bluetooth.gatt.GattOperationBundle;
import no.nordicsemi.puckcentral.bluetooth.gatt.operations.GattCharacteristicWriteOperation;
import no.nordicsemi.puckcentral.bluetooth.gatt.operations.GattDisconnectOperation;
import no.nordicsemi.puckcentral.db.PuckManager;
import no.nordicsemi.puckcentral.models.Action;
import no.nordicsemi.puckcentral.models.Puck;
import no.nordicsemi.puckcentral.models.Rule;
import no.nordicsemi.puckcentral.utils.LZCompression;
import no.nordicsemi.puckcentral.utils.UUIDUtils;

public class DisplayActuator extends PuckActuator {

    public static final UUID SERVICE_DISPLAY_UUID = UUIDUtils.stringToUUID("bftj display    ");
    public static final UUID CHARACTERISTIC_COMMAND_UUID = UUIDUtils.stringToUUID("bftj display com");
    public static final UUID CHARACTERISTIC_DATA_UUID = UUIDUtils.stringToUUID("bftj display dat");
    private static final String ARGUMENT_TEXT = "Display Text (large)";
    private static final byte COMMAND_BEGIN_IMAGE_UPPER = 4;
    private static final byte COMMAND_BEGIN_IMAGE_LOWER = 5;
    private static final byte COMMAND_END_IMAGE_UPPER = 2;
    private static final byte COMMAND_END_IMAGE_LOWER = 3;

    @Override
    public String describeActuator() {
        return "Display an image on a Display Puck";
    }

    @Override
    public String describeArguments(JSONObject arguments) {
        try {
            return "Displays " + arguments.getString(ARGUMENT_TEXT) + " on a Display Puck";
        } catch (JSONException e) {
            e.printStackTrace();
            return "Invalid arguments for actuator";
        }
    }

    @Override
    public int getId() {
        return 2342;
    }

    @Override
    public AlertDialog getActuatorDialog(Activity activity, final Action action, final Rule rule, final ActuatorDialogFinishListener listener) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.dialog_actuator_single_textinput, null);
        final EditText editText1 = (EditText) view.findViewById(R.id.etDialogActuatorEditText1);
        editText1.setHint(activity.getString(R.string.text_to_display));

        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setView(view)
                .setTitle(describeActuator())
                .setPositiveButton(activity.getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Puck puck = new PuckManager(mContext).getAll().get(0);
                        String arguments = Action.jsonStringBuilder(
                                DisplayActuator.ARGUMENT_UUID, puck.getProximityUUID(),
                                DisplayActuator.ARGUMENT_MAJOR, puck.getMajor(),
                                DisplayActuator.ARGUMENT_MINOR, puck.getMinor(),
                                DisplayActuator.ARGUMENT_TEXT, editText1.getText().toString());

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
        Bitmap bitmap = render(arguments.getString(ARGUMENT_TEXT));
        GattOperationBundle bundle = new GattOperationBundle();
        writeImage(device, bundle, bitmap, ImageSection.UPPER);
        writeImage(device, bundle, bitmap, ImageSection.LOWER);
        mGattManager.queue(bundle);
    }

    private enum ImageSection {
        LOWER, UPPER
    }

    private void writeImage(BluetoothDevice device, GattOperationBundle bundle, Bitmap bitmap, ImageSection section) {

        byte beginCommand = section == ImageSection.UPPER
                          ? COMMAND_BEGIN_IMAGE_UPPER
                          : COMMAND_BEGIN_IMAGE_LOWER;
        byte endCommand = section == ImageSection.UPPER
                        ? COMMAND_END_IMAGE_UPPER
                        : COMMAND_END_IMAGE_LOWER;

        bundle.addOperation(new GattCharacteristicWriteOperation(
            device, SERVICE_DISPLAY_UUID, CHARACTERISTIC_COMMAND_UUID, new byte[]{beginCommand}));
        byte[] ePaperFormat = bitmapToEPaperFormat(bitmap, section);
        byte[] unpaddedPayload = LZCompression.compress(ePaperFormat);
        byte[] payload = padPayload(unpaddedPayload);
        for(int i = 0; i < payload.length; i += 20) {
            byte[] value = new byte[20];
            System.arraycopy(payload, i, value, 0, Math.min(20, payload.length - i));
            bundle.addOperation(new GattCharacteristicWriteOperation(
                device, SERVICE_DISPLAY_UUID, CHARACTERISTIC_DATA_UUID, value));
        }
        bundle.addOperation(new GattCharacteristicWriteOperation(
            device, SERVICE_DISPLAY_UUID, CHARACTERISTIC_COMMAND_UUID, new byte[]{endCommand}));
        bundle.addOperation(new GattDisconnectOperation(device));
    }

    private byte[] padPayload(byte[] unpaddedPayload) {
        int paddingLength = unpaddedPayload.length % 20 == 0
                ? 0
                : 20 - unpaddedPayload.length % 20;
        byte[] payload = new byte[unpaddedPayload.length + paddingLength];
        for(int i = 1; i < unpaddedPayload.length - 1; i++) {
            if(unpaddedPayload[i] == unpaddedPayload[0] && unpaddedPayload[i + 1] != 0) {
                i++;
                System.arraycopy(unpaddedPayload, 0, payload, 0, i);
                for(int j = 0; j < paddingLength; j++) {
                    payload[i + j] = (byte) 0x80;
                }
                System.arraycopy(unpaddedPayload, i, payload, i + paddingLength, unpaddedPayload.length - i);
                break;
            }
        }
        return payload;
    }

    private byte[] bitmapToEPaperFormat(Bitmap bitmap, ImageSection section) {
        int byteCounter = 0;
        int startY = section == ImageSection.LOWER
                   ? bitmap.getHeight() / 2
                   : 0;
        byte[] value = new byte[bitmap.getWidth() / 8 * bitmap.getHeight() / 2];
        for(int y = startY; y < startY + bitmap.getHeight() / 2; y++) {
            for(int x = 0; x < bitmap.getWidth() / 8; x++) {
                byte b = 0;
                for(int i = 0; i < 8; i++) {
                    int pixel = bitmap.getPixel(x * 8 + i, y);
                    int grey = (Color.red(pixel) + Color.blue(pixel) + Color.green(pixel)) / 3;
                    b |= (grey > 0 ? 0 : 1) << i;
                }
                value[byteCounter++] = b;
            }
        }
        return value;
    }

    private Bitmap render(String string) {
        Bitmap bitmap = Bitmap.createBitmap(264, 176, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setTextSize(40);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.SERIF);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(string, 264 / 2, 176 / 2 + 13, paint);
        return bitmap;
    }
}
