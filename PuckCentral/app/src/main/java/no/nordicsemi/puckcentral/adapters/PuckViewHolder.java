package no.nordicsemi.puckcentral.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.droidparts.adapter.holder.ViewHolder;
import org.droidparts.annotation.inject.InjectView;

import no.nordicsemi.puckcentral.R;

public class PuckViewHolder extends ViewHolder {

    @InjectView(id = R.id.tvPuckName)
    public TextView mTvPuckName;

    @InjectView(id = R.id.llTriggerList)
    public LinearLayout mLlTriggerList;

    @InjectView(id = R.id.btnDeletePuck)
    public Button mBtnDeletePuck;

    @InjectView(id = R.id.btnAddRuleToPuck)
    public Button mBtnAddRuleToPuck;

    public PuckViewHolder(View view) {
        super(view);
    }
}

