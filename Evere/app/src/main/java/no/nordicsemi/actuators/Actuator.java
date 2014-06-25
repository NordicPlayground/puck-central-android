package no.nordicsemi.actuators;

import android.content.Context;

import org.droidparts.Injector;
import org.droidparts.annotation.inject.InjectDependency;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Actuator {

    @InjectDependency
    Context mContext;

    private static HashMap<Integer, Actuator> mActuators;

    static {
        mActuators = createActuatorList();
    }

    public abstract String getDescription();

    public abstract int getId();

    public static ArrayList<Actuator> getActuators() {
        return new ArrayList<>(mActuators.values());
    }

    public static Actuator getActuatorForId(int id) {
        return mActuators.get(id);
    }

    public Actuator() {
        Injector.inject(Injector.getApplicationContext(), this);
    }

    abstract void actuate(JSONObject arguments) throws JSONException;

    public static void actuate(int actuatorId, String arguments) throws JSONException {
        mActuators.get(actuatorId).actuate(arguments);
    }

    public void actuate(String arguments) throws JSONException {
        actuate(new JSONObject(arguments));
    }

    private static HashMap<Integer, Actuator> createActuatorList() {
        HashMap<Integer, Actuator> actuators = new HashMap<>();
        Actuator actuator = new HttpActuator();
        actuators.put(actuator.getId(), actuator);
        actuator = new RingerActuator();
        actuators.put(actuator.getId(), actuator);
        return actuators;
    }

}
