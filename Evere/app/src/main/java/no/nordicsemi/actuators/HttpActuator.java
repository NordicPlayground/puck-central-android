package no.nordicsemi.actuators;

import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.concurrent.task.SimpleAsyncTask;
import org.droidparts.net.http.RESTClient;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpActuator extends Actuator {

    @InjectDependency
    RESTClient mRestClient;

    @Override
    void actuate(final JSONObject arguments) throws JSONException {
        new SimpleAsyncTask<Void>(mContext, null) {
            @Override
            protected Void onExecute() throws Exception {
                mRestClient.post((String) arguments.get("url"), "application/x-www-form-urlencoded", (String) arguments.get("data"));
                return null;
            }
        }.execute();
    }
}
