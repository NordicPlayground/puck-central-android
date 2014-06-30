package no.nordicsemi.models;

import org.droidparts.annotation.sql.Column;
import org.droidparts.annotation.sql.Table;
import org.droidparts.model.Entity;

import java.util.ArrayList;

import no.nordicsemi.db.DB;

@Table
public class Rule extends Entity {

    @Column(name = DB.Column.PUCK, eager = true)
    private Puck mPuck;

    @Column(name = DB.Column.TRIGGER)
    private String mTrigger;

    @Column(name = DB.Column.ACTIONS, eager = true)
    private ArrayList<Action> mActions;

    public void setPuck(Puck puck) { this.mPuck = puck; }

    public Puck getPuck() { return mPuck; }

    public String getTrigger() {
        return mTrigger;
    }

    public void setTrigger(String trigger) {
        this.mTrigger = trigger;
    }

    public ArrayList<Action> getActions() {
        return mActions;
    }

    public void addAction(Action action) {
        if (mActions == null) {
            mActions = new ArrayList<>();
        }
        mActions.add(action);
    }
}
