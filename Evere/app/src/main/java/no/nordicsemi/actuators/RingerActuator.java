package no.nordicsemi.actuators;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;

import org.droidparts.annotation.inject.InjectSystemService;
import org.json.JSONException;
import org.json.JSONObject;

import no.nordicsemi.R;
import no.nordicsemi.models.Action;
import no.nordicsemi.models.Rule;

public class RingerActuator extends Actuator {

    public static final String MODE = "mode";
    public static final String[] RINGER_MODES = new String[] { "Silent", "Vibrate", "Volume" };

    @InjectSystemService
    AudioManager mAudioManager;

    @Override
    public String getDescription() {
        return "Change phone state";
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void actuate(JSONObject arguments) throws JSONException {
        if (arguments.has(MODE)) {
            mAudioManager.setRingerMode(arguments.getInt(MODE));
        } else {
            throw new IllegalArgumentException("Arguments do not contain MODE");
        }
    }

    @Override
    public AlertDialog getActuatorDialog(Context ctx, final Action action, final Rule rule,
                                         final ActuatorDialogFinishListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
                .setTitle(getDescription())
                .setItems(RINGER_MODES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String arguments;
                        switch (which) {
                            case 0:
                                arguments = Action.jsonStringBuilder(MODE,
                                        AudioManager.RINGER_MODE_SILENT);
                                break;

                            case 1:
                                arguments = Action.jsonStringBuilder(MODE,
                                        AudioManager.RINGER_MODE_VIBRATE);
                                break;

                            case 2:
                                arguments = Action.jsonStringBuilder(MODE,
                                        AudioManager.RINGER_MODE_NORMAL);
                                break;

                            default:
                                throw new IllegalStateException();
                        }

                        action.setArguments(arguments);
                        rule.addAction(action);
                        listener.onActuatorDialogFinish(action, rule);
                    }
                })
                .setNegativeButton(ctx.getString(R.string.abort), null);
        return builder.create();
    }
}
