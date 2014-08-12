package no.nordicsemi.puckcentral.db;

import android.content.Context;

import org.droidparts.persist.sql.EntityManager;
import org.droidparts.persist.sql.stmt.Is;
import org.droidparts.persist.sql.stmt.Select;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.puckcentral.models.Puck;
import no.nordicsemi.puckcentral.models.Rule;

public class RuleManager extends EntityManager<Rule> {

    public RuleManager(Context ctx) {
        super(Rule.class, ctx);
    }

    public ArrayList<Rule> getRulesForTrigger(String trigger) {
        return readAll(select().where(DB.Column.TRIGGER, Is.EQUAL, trigger));
    }

    public List<Rule> getRulesForTriggers(String... triggers) {
        List<Rule> rules = new ArrayList<>();
        for (String trigger : triggers) {
            rules.addAll(getRulesForTrigger(trigger));
        }
        return rules;
    }

    public Select<Rule> getRulesForPuck(Puck puck) {
        if (puck == null) {
            return null;
        } else {
            return select().where(DB.Column.PUCK, Is.EQUAL, puck.id);
        }
    }

    public ArrayList<Rule> getRulesForPuckAndTrigger(Puck puck, String triggerIdentifier) {
        if (puck == null || triggerIdentifier == null) {
            return new ArrayList<>();
        } else {
            return readAll(select()
                    .where(DB.Column.PUCK, Is.EQUAL, puck.id)
                    .where(DB.Column.TRIGGER, Is.EQUAL, triggerIdentifier));
        }
    }

    public void deteRulesWithPuckId(long id) {
        List<Rule> toDelete = readAll(select().where(DB.Column.PUCK, Is.EQUAL, id));
        for (Rule rule : toDelete) {
            super.delete(rule.id);
        }
    }

    public void createOrExtendExisting(Rule rule) {
        List<Rule> rules = getRulesForPuckAndTrigger(rule.getPuck(), rule.getTrigger());
        if (rules.size() > 0) {
            // If multiple rules with the same trigger exist, pick the first
            Rule toExtend = rules.get(0);
            fillForeignKeys(toExtend);
            toExtend.getActions().addAll(rule.getActions());
            update(toExtend);
        } else {
            super.create(rule);
        }
    }
}
