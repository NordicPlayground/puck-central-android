package no.nordicsemi.puckcentral.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import org.droidparts.adapter.cursor.EntityCursorAdapter;
import org.droidparts.bus.EventBus;
import org.droidparts.persist.sql.stmt.Select;

import no.nordicsemi.puckcentral.R;
import no.nordicsemi.puckcentral.db.RuleManager;
import no.nordicsemi.puckcentral.models.Action;
import no.nordicsemi.puckcentral.models.Rule;
import no.nordicsemi.puckcentral.triggers.Trigger;

public class RuleAdapter extends EntityCursorAdapter<Rule> {

    public RuleAdapter(Context ctx, Select<Rule> select) {
        super(ctx, new RuleManager(ctx), select);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view =  getLayoutInflater().inflate(R.layout.trigger_list_item, null);
        TriggerViewHolder holder = new TriggerViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(Context context, View view, final Rule rule) {
        entityManager.fillForeignKeys(rule);

        TriggerViewHolder holder = (TriggerViewHolder) view.getTag();
        holder.mTvTriggerName.setText(rule.getTrigger());

        StringBuilder sb = new StringBuilder();
        for (Action action : rule.getActions()) {
            sb.append("- ");
            sb.append(action.describeArguments());
            sb.append("\n");
        }

        sb.setLength(Math.max(sb.length() -1, 0));

        holder.mTvTriggerActions.setText(sb.toString());

        holder.mBtnRemoveRuleFromPuck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.postEvent(Trigger.TRIGGER_REMOVE_RULE, rule);
            }
        });
    }
}
