package no.nordicsemi.actuators;

import android.content.Context;


import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.concurrent.task.AsyncTaskResultListener;
import org.droidparts.concurrent.task.SimpleAsyncTask;
import org.droidparts.net.http.HTTPException;
import org.droidparts.net.http.RESTClient;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpActuator extends Actuator {

    private final Context mContext;
    @InjectDependency
    RESTClient mRestClient;

    public HttpActuator(Context context) {
        super(context);
        mContext = context;
    }

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
