package no.nordicsemi.adapters;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.droidparts.adapter.holder.ViewHolder;
import org.droidparts.annotation.inject.InjectView;

import no.nordicsemi.R;

public class RuleViewHolder extends ViewHolder {

    @InjectView(id = R.id.tvPuckName)
    public TextView mTvPuckName;

    @InjectView(id = R.id.tvTrigger)
    public TextView mTvTrigger;

    @InjectView(id = R.id.lvActuatorList)
    public ListView mLvActuatorList;

    public RuleViewHolder(View view) {
        super(view);
    }
}
