package no.nordicsemi.models;

import org.droidparts.annotation.sql.Column;
import org.droidparts.annotation.sql.Table;
import org.droidparts.model.Entity;

import java.util.ArrayList;

import no.nordicsemi.triggers.Trigger;

@Table
public class Rule extends Entity {

    @Column
    private Class trigger;

    @Column
    private ArrayList<Action> actions;

    public Class getTrigger() {
        return trigger;
    }

    public void setTrigger(Class trigger) {
        this.trigger = trigger;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public void setActions(ArrayList<Action> actions) {
        this.actions = actions;
    }
}
