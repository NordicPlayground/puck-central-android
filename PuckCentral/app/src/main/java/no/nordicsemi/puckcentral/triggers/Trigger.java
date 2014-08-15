package no.nordicsemi.puckcentral.triggers;


import org.droidparts.Injector;
import org.droidparts.util.L;
import org.json.JSONException;

import java.util.ArrayList;

import no.nordicsemi.puckcentral.actuators.Actuator;
import no.nordicsemi.puckcentral.db.RuleManager;
import no.nordicsemi.puckcentral.models.Action;
import no.nordicsemi.puckcentral.models.Puck;
import no.nordicsemi.puckcentral.models.Rule;

public abstract class Trigger {

    public static final String TRIGGER_CLOSEST_PUCK_CHANGED = "trigger_closest_puck_changed";
    public static final String TRIGGER_CONNECTION_STATE_CHANGED = "trigger_connection_state_changed";
    public static final String TRIGGER_ENTER_ZONE = "trigger_enter_zone";
    public static final String TRIGGER_LEAVE_ZONE = "trigger_leave_zone";
    public static final String TRIGGER_ZONE_DISCOVERED = "trigger_zone_discovered";
    public static final String TRIGGER_ADD_ACTUATOR_FOR_EXISTING_RULE = "trigger_add_actuator_for_existing_rule";
    public static final String TRIGGER_REMOVE_RULE = "trigger_remove_rule";

    public static final String TRIGGER_ROTATE_CUBE_UP = "trigger_rotate_cube_up";
    public static final String TRIGGER_ROTATE_CUBE_DOWN = "trigger_rotate_cube_down";
    public static final String TRIGGER_ROTATE_CUBE_LEFT = "trigger_rotate_cube_left";
    public static final String TRIGGER_ROTATE_CUBE_RIGHT = "trigger_rotate_cube_right";
    public static final String TRIGGER_ROTATE_CUBE_FRONT = "trigger_rotate_cube_front";
    public static final String TRIGGER_ROTATE_CUBE_BACK = "trigger_rotate_cube_back";
    public static final String TRIGGER_ADD_RULE_FOR_EXISTING_PUCK = "trigger_add_rule_for_existing_puck";

    public static void trigger(Puck puck, String trigger) {
        L.i("Triggering event for puck " + puck + " and trigger " + trigger);

        ArrayList<Rule> rules = Injector.getDependency(Injector.getApplicationContext(),
                RuleManager.class).getRulesForPuckAndTrigger(puck, trigger);
        L.i("With matching rules " + rules);

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
