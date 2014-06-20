package no.nordicsemi.actuators;

import android.content.Context;

import org.droidparts.Injector;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Actuator {

    public Actuator(Context context) {
        Injector.inject(context, this);
    }

    abstract void actuate(JSONObject arguments) throws JSONException;

    public void actuate(String arguments) throws JSONException {
        actuate(new JSONObject(arguments));
    }
}
