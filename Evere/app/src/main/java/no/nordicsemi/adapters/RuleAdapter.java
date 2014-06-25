package no.nordicsemi.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.droidparts.adapter.cursor.EntityCursorAdapter;
import org.droidparts.persist.sql.stmt.Select;

import java.util.ArrayList;

import no.nordicsemi.R;
import no.nordicsemi.db.RuleManager;
import no.nordicsemi.models.Action;
import no.nordicsemi.models.Rule;

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
    public void bindView(Context context, View view, Rule rule) {
        entityManager.fillForeignKeys(rule);

        RuleViewHolder holder = (RuleViewHolder) view.getTag();

        holder.mTvPuckName.setText(rule.getPuck().getName());
        holder.mTvTrigger.setText(rule.getTrigger());

        ArrayList<Action> actions = rule.getActions();
        ArrayList<String> actuatorDescriptions = new ArrayList<>();
        for (Action action : actions) {
            actuatorDescriptions.add(action.getActuator().getDescription());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mCtx,
                android.R.layout.simple_list_item_1, actuatorDescriptions);
        holder.mLvActuatorList.setAdapter(adapter);
    }
}
