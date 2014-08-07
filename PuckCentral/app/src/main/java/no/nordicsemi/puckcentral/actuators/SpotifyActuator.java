package no.nordicsemi.puckcentral.actuators;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.droidparts.activity.Activity;
import org.droidparts.annotation.inject.InjectDependency;
import org.json.JSONException;
import org.json.JSONObject;

import no.nordicsemi.puckcentral.R;
import no.nordicsemi.puckcentral.models.Action;
import no.nordicsemi.puckcentral.models.Rule;

public class SpotifyActuator extends Actuator {

    public static final String SPOTIFY_URI = "spotify_uri";

    @InjectDependency
    Context mCtx;

    @Override
    public String getDescription() {
        return "Play song from spotify";
    }

    @Override
    public int getId() {
        return 42;
    }

    @Override
    void actuate(JSONObject arguments) throws JSONException {
        String uri = arguments.getString(SPOTIFY_URI);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mCtx.startActivity(intent);
    }

    @Override
    public AlertDialog getActuatorDialog(Activity activity, final Action action, final Rule rule,
                                         final ActuatorDialogFinishListener listener) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.dialog_actuator_single_textinput, null);
        final EditText editText1 = (EditText) view.findViewById(R.id.etDialogActuatorEditText1);
        editText1.setHint(SPOTIFY_URI);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setView(view)
                .setTitle(getDescription())
                .setPositiveButton(activity.getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String arguments = Action.jsonStringBuilder(SPOTIFY_URI,
                                editText1.getText().toString());

                        action.setArguments(arguments);
                        rule.addAction(action);
                        listener.onActuatorDialogFinish(action, rule);
                    }
                })
                .setNegativeButton(activity.getString(R.string.reject), null);
        return builder.create();
    }
}
