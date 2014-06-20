package no.nordicsemi.actuators;

import android.content.Context;
import android.media.AudioManager;

import org.droidparts.annotation.inject.InjectSystemService;
import org.json.JSONException;
import org.json.JSONObject;

public class RingerActuator extends Actuator {

    @InjectSystemService
    AudioManager mAudioManager;

    public RingerActuator(Context context) {
        super(context);
    }

    @Override
    public void actuate(JSONObject arguments) throws JSONException {
        mAudioManager.setRingerMode((Integer) arguments.get("mode"));
    }
}
