package no.nordicsemi.puckcentral.models;

import org.droidparts.annotation.sql.Column;
import org.droidparts.annotation.sql.Table;
import org.droidparts.model.Entity;
import org.json.JSONException;
import org.json.JSONObject;

import no.nordicsemi.puckcentral.actuators.Actuator;

@Table
public class Action extends Entity {

    @Column
    private Integer actuatorId;

    @Column
    private String arguments;

    public Action() {}

    public Action(int actuatorId, String arguments) {
        this.actuatorId = actuatorId;
        this.arguments = arguments;
    }

    public Integer getActuatorId() {
        return actuatorId;
    }

    public Actuator getActuator() {
        return Actuator.getActuatorForId(actuatorId);
    }

    public void setActuatorId(Integer actuatorId) {
        this.actuatorId = actuatorId;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public String describeArguments() {
        return getActuator().describeArguments(arguments);
    }

    public String describeActuator() {
        return getActuator().describeActuator();
    }

    public static String jsonStringBuilder(Object... keyVals) {
        if (keyVals.length % 2 == 1) {
            throw new IllegalArgumentException("Arguments must be a multiple of two.");
        }

        JSONObject jsonObject = new JSONObject();
        for (int i=0; i<keyVals.length; i += 2) {
            try {
                jsonObject.put(String.valueOf(keyVals[i]), JSONObject.wrap(keyVals[i+1]));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonObject.toString();
    }
}
