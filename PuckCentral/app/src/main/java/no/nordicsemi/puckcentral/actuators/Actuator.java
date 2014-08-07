package no.nordicsemi.puckcentral.actuators;

import android.app.AlertDialog;
import android.content.Context;

import org.droidparts.Injector;
import org.droidparts.activity.Activity;
import org.droidparts.annotation.inject.InjectDependency;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import no.nordicsemi.puckcentral.models.Action;
import no.nordicsemi.puckcentral.models.Rule;

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

    public void actuate(String arguments) throws JSONException {
        actuate(new JSONObject(arguments));
    }

    public static void actuate(int actuatorId, String arguments) throws JSONException {
        mActuators.get(actuatorId).actuate(arguments);
    }


    public abstract AlertDialog getActuatorDialog(Activity activity, final Action action, final Rule rule,
                                                  final ActuatorDialogFinishListener listener);

    public interface ActuatorDialogFinishListener {
        public void onActuatorDialogFinish(Action action, Rule rule);
    }

    private static HashMap<Integer, Actuator> createActuatorList() {
        HashMap<Integer, Actuator> actuators = new HashMap<>();
        Actuator actuator = new HttpActuator();
        actuators.put(actuator.getId(), actuator);
        actuator = new RingerActuator();
        actuators.put(actuator.getId(), actuator);
        actuator = new SpotifyActuator();
        actuators.put(actuator.getId(), actuator);
        actuator = new IRActuator();
        actuators.put(actuator.getId(), actuator);
        actuator = new DisplayActuator();
        actuators.put(actuator.getId(), actuator);
        actuator = new MusicVolumeActuator();
        actuators.put(actuator.getId(), actuator);
        return actuators;
    }

}
