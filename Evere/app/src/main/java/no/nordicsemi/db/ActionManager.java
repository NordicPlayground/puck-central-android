package no.nordicsemi.db;

import android.content.Context;

import org.droidparts.persist.sql.EntityManager;

import no.nordicsemi.models.Action;

public class ActionManager extends EntityManager<Action> {

    public ActionManager(Context ctx) {
        super(Action.class, ctx);
    }

}
