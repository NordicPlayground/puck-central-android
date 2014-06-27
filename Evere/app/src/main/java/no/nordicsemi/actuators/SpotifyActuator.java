package no.nordicsemi.actuators;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.droidparts.annotation.inject.InjectDependency;
import org.json.JSONException;
import org.json.JSONObject;

import no.nordicsemi.R;
import no.nordicsemi.models.Action;
import no.nordicsemi.models.Rule;

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
        String uri = (String) arguments.get(SPOTIFY_URI);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mCtx.startActivity(intent);
    }

    @Override
    public AlertDialog getActuatorDialog(Context ctx, final Action action, final Rule rule,
                                         final ActuatorDialogFinishListener listener) {
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.dialog_actuator_single_textinput, null);
        final EditText editText1 = (EditText) view.findViewById(R.id.etDialogActuatorEditText1);
        editText1.setHint(SPOTIFY_URI);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
                .setView(view)
                .setTitle(getDescription())
                .setPositiveButton(ctx.getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String arguments = Action.jsonStringBuilder(SPOTIFY_URI,
                                editText1.getText());

                        action.setArguments(arguments);
                        rule.setAction(action);
                        listener.onActuatorDialogFinish(action, rule);
                    }
                })
                .setNegativeButton(ctx.getString(R.string.reject), null);
        return builder.create();
    }
}
