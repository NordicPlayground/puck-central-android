package no.nordicsemi.triggers;


import org.droidparts.Injector;
import org.droidparts.util.L;
import org.json.JSONException;

import java.util.ArrayList;

import no.nordicsemi.actuators.Actuator;
import no.nordicsemi.db.RuleManager;
import no.nordicsemi.models.Action;
import no.nordicsemi.models.Puck;
import no.nordicsemi.models.Rule;

public abstract class Trigger {

    public static final String TRIGGER_PROXIMITY_IMMEDIATE = "trigger_proximity_immediate";
    public static final String TRIGGER_PROXIMITY_NEAR = "trigger_proximity_near";
    public static final String TRIGGER_PROXIMITY_FAR = "trigger_proximity_far";

    public static void trigger(Puck puck, String trigger) {
        L.d("Triggering event for puck " + puck + " and trigger " + trigger);

        ArrayList<Rule> rules = Injector.getDependency(Injector.getApplicationContext(),
                RuleManager.class).getRulesForPuckAndTrigger(puck, trigger);
        L.d("With matching rules " + rules);

        for(Rule rule : rules) {
            for (Action action : rule.getActions()) {
                try {
                    Actuator.actuate(action.getActuatorId(), action.getArguments());
                } catch (JSONException e) {
                    L.e(e);
                }
            }
        }
    }
}
