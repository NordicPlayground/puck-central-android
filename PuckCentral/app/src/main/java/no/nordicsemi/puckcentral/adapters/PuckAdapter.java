package no.nordicsemi.puckcentral.adapters;

import android.app.AlertDialog;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import org.droidparts.adapter.cursor.EntityCursorAdapter;
import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.bus.EventBus;
import org.droidparts.persist.sql.stmt.Select;
import org.droidparts.util.L;

import java.util.HashMap;
import java.util.Map;

import no.nordicsemi.puckcentral.R;
import no.nordicsemi.puckcentral.bluetooth.gatt.GattManager;
import no.nordicsemi.puckcentral.db.PuckManager;
import no.nordicsemi.puckcentral.db.RuleManager;
import no.nordicsemi.puckcentral.models.Puck;
import no.nordicsemi.puckcentral.triggers.Trigger;

public class PuckAdapter extends EntityCursorAdapter<Puck> {

    @InjectDependency
    private RuleManager mRuleManager;

    @InjectDependency
    private PuckManager mPuckManager;

    private Map<String, Integer> connectionStates;

    private Puck mClosestPuck;

    public PuckAdapter(Context ctx, Select<Puck> select) {
        super(ctx, new PuckManager(ctx), select);
        mClosestPuck = null;
        connectionStates = new HashMap<>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view =  getLayoutInflater().inflate(R.layout.puck_list_item, null);
        PuckViewHolder puckViewHolder = new PuckViewHolder(view);
        view.setTag(puckViewHolder);
        return view;
    }

    @Override
    public void bindView(final Context context, View view, final Puck puck) {
        entityManager.fillForeignKeys(puck);

        final PuckViewHolder holder = (PuckViewHolder) view.getTag();

        StringBuilder puckName = new StringBuilder(puck.getName());
        puckName.setCharAt(0, Character.toUpperCase(puckName.charAt(0)));
        holder.mTvPuckName.setText(puckName.toString() +
                (puck.equals(mClosestPuck) ?
                        " (Immediate)"
                        : ""));

        int color = context.getResources().getColor(android.R.color.black);
        if (connectionStates.containsKey(puck.getAddress())) {
            switch (connectionStates.get(puck.getAddress())) {
                case BluetoothProfile.STATE_CONNECTING:
                    color = context.getResources().getColor(android.R.color.holo_orange_light);
                    break;

                case BluetoothProfile.STATE_CONNECTED:
                    color = context.getResources().getColor(android.R.color.holo_green_light);
                    break;
            }
        }

        holder.mTvPuckName.setTextColor(color);

        RuleAdapter ruleAdapter = new RuleAdapter(context, mRuleManager.getRulesForPuck(puck));
        holder.mLlTriggerList.removeAllViews();
        for (int i=0; i< ruleAdapter.getCount(); i++) {
            View item = ruleAdapter.getView(i, null, null);
            holder.mLlTriggerList.addView(item);
        }

        holder.mBtnDeletePuck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.remove_puck, puck.getName()))
                        .setPositiveButton(context.getString(R.string.remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mPuckManager.delete(puck.id)) {
                                    requeryData();
                                }
                            }
                        })
                        .setNegativeButton(context.getString(R.string.abort), null);
                builder.create().show();
            }
        });

        holder.mBtnAddRuleToPuck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.postEvent(Trigger.TRIGGER_ADD_RULE_FOR_EXISTING_PUCK, puck);
            }
        });
    }

    public void connectionStateChanged(GattManager.ConnectionStateChangedBundle bundle) {
        if (!connectionStates.containsKey(bundle.mAddress)) {
            connectionStates.put(bundle.mAddress, BluetoothProfile.STATE_DISCONNECTED);
        }

        if (connectionStates.get(bundle.mAddress) == bundle.mNewState) {
            return;
        }

        connectionStates.put(bundle.mAddress, bundle.mNewState);
        requeryData();
    }

    public void closestPuckChanged(Puck closestPuck) {
        if (closestPuck == null && mClosestPuck == null ||
                closestPuck != null && closestPuck.equals(mClosestPuck)) {
            return;
        }

        mClosestPuck = closestPuck;
        requeryData();
    }
}
