package no.nordicsemi.actuators;

import android.content.Context;

import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.net.http.HTTPException;
import org.droidparts.net.http.RESTClient;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpActuator extends Actuator {

    @InjectDependency
    RESTClient mRestClient;

    public HttpActuator(Context context) {
        super(context);
    }

    @Override
    void actuate(JSONObject arguments) throws JSONException {
        try {
            mRestClient.post((String) arguments.get("url"), "", (String) arguments.get("data"));
        } catch (HTTPException e) {
            e.printStackTrace();
        }
    }
}
