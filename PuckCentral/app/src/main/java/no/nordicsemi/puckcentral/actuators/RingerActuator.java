package no.nordicsemi.puckcentral.actuators;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;

import org.droidparts.activity.Activity;
import org.droidparts.annotation.inject.InjectSystemService;
import org.json.JSONException;
import org.json.JSONObject;

import no.nordicsemi.puckcentral.R;
import no.nordicsemi.puckcentral.models.Action;
import no.nordicsemi.puckcentral.models.Rule;

public class RingerActuator extends Actuator {

    public static final String MODE = "mode";
    public static final String[] RINGER_MODES = new String[] { "silent", "vibrate only", "volume on" };

    @InjectSystemService
    AudioManager mAudioManager;

    @Override
    public String describeActuator() {
        return "Phone Volume Actuator";
    }

    @Override
    public String describeArguments(JSONObject arguments) {
        try {
            return "Sets your phone to " + RINGER_MODES[arguments.getInt(MODE)];
        } catch (JSONException e) {
            e.printStackTrace();
            return "Invalid arguments for actuator";
        }
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
    public AlertDialog getActuatorDialog(Activity activity, final Action action, final Rule rule,
                                         final ActuatorDialogFinishListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(describeActuator())
                .setItems(RINGER_MODES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int ringerMode;
                        switch (which) {
                            case 0:
                                ringerMode = AudioManager.RINGER_MODE_SILENT;
                                break;

                            case 1:
                                ringerMode = AudioManager.RINGER_MODE_VIBRATE;
                                break;

                            case 2:
                                ringerMode = AudioManager.RINGER_MODE_NORMAL;
                                break;

                            default:
                                throw new IllegalStateException();
                        }

                        String arguments = Action.jsonStringBuilder(MODE, ringerMode);
                        action.setArguments(arguments);
                        rule.addAction(action);
                        listener.onActuatorDialogFinish(action, rule);
                    }
                })
                .setNegativeButton(activity.getString(R.string.abort), null);
        return builder.create();
    }
}
