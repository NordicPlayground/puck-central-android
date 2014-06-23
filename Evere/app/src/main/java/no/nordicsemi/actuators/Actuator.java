package no.nordicsemi.actuators;

import android.content.Context;

import org.droidparts.Injector;
import org.droidparts.annotation.inject.InjectDependency;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Actuator {

    @InjectDependency
    Context mContext;

    public Actuator() {
        Injector.inject(mContext, this);
    }

    abstract void actuate(JSONObject arguments) throws JSONException;

    public void actuate(String arguments) throws JSONException {
        actuate(new JSONObject(arguments));
    }
}
