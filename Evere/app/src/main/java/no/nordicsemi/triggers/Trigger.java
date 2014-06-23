package no.nordicsemi.triggers;


import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.persist.sql.stmt.Is;
import org.droidparts.util.L;
import org.json.JSONException;

import java.util.ArrayList;

import no.nordicsemi.actuators.Actuator;
import no.nordicsemi.db.RuleManager;
import no.nordicsemi.models.Action;
import no.nordicsemi.models.Rule;

public abstract class Trigger {

    @InjectDependency
    RuleManager mRuleManager;

    public void trigger() {
        for(Rule rule : mRuleManager.getRulesForTrigger(this)) {
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
