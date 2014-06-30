package no.nordicsemi.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.droidparts.adapter.cursor.EntityCursorAdapter;
import org.droidparts.bus.EventBus;
import org.droidparts.persist.sql.stmt.Select;

import java.util.ArrayList;

import no.nordicsemi.R;
import no.nordicsemi.db.RuleManager;
import no.nordicsemi.models.Action;
import no.nordicsemi.models.Rule;
import no.nordicsemi.triggers.Trigger;

public class RuleAdapter extends EntityCursorAdapter<Rule> {

    private final Context mCtx;

    public RuleAdapter(Context ctx, Select<Rule> select) {
        super(ctx, new RuleManager(ctx), select);
        this.mCtx = ctx;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view =  getLayoutInflater().inflate(R.layout.rule_list_item, null);
        RuleViewHolder ruleViewHolder = new RuleViewHolder(view);
        view.setTag(ruleViewHolder);
        return view;
    }

    @Override
    public void bindView(final Context context, View view, final Rule rule) {
        entityManager.fillForeignKeys(rule);

        RuleViewHolder holder = (RuleViewHolder) view.getTag();

        holder.mTvPuckName.setText(rule.getPuck().getName());
        holder.mTvTrigger.setText(rule.getTrigger());

        ArrayList<Action> actions = rule.getActions();
        LinearLayout actuatorList = (LinearLayout) view.findViewById(R.id.lvActuatorList);
        actuatorList.removeAllViews();

        for (Action action : actions) {
            View listItem = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, null);
            ((TextView) listItem.findViewById(android.R.id.text1)).setText(action.getActuator()
                       .getDescription());
            ((TextView) listItem.findViewById(android.R.id.text2)).setText(action.getArguments());
            actuatorList.addView(listItem);
        }

        view.findViewById(R.id.ibAddActuatorToExistingRule)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.postEvent(Trigger.TRIGGER_ADD_ACTUATOR_FOR_EXISTING_RULE,
                                rule);
                    }
                });

        view.findViewById(R.id.ibDeleteExistingRule)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.postEvent(Trigger.TRIGGER_REMOVE_RULE, rule);
                    }
                });
    }

    public boolean delete(Rule rule) {
        boolean success = entityManager.delete(rule.id);
        if (success) {
            requeryData();
        }
        return success;
    }

    public boolean createOrUpdate(Rule rule) {
        boolean success = entityManager.createOrUpdate(rule);
        if (success) {
            requeryData();
        }
        return success;

    }
}
