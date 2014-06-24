package no.nordicsemi.db;

import android.content.Context;

import org.droidparts.persist.sql.EntityManager;
import org.droidparts.persist.sql.stmt.Is;

import java.util.ArrayList;

import no.nordicsemi.models.Rule;
import no.nordicsemi.triggers.Trigger;

public class RuleManager extends EntityManager<Rule> {

    public RuleManager(Context ctx) {
        super(Rule.class, ctx);
    }

    public ArrayList<Rule> getRulesForTrigger(Trigger trigger) {
        return readAll(select().where("trigger", Is.EQUAL, this.getClass()));
    }
}
