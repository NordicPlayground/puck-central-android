package no.nordicsemi.actuators;

import android.media.AudioManager;

import org.droidparts.annotation.inject.InjectSystemService;
import org.json.JSONException;
import org.json.JSONObject;

public class RingerActuator extends Actuator {

    @InjectSystemService
    AudioManager mAudioManager;

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void actuate(JSONObject arguments) throws JSONException {
        mAudioManager.setRingerMode((Integer) arguments.get("mode"));
    }
}
