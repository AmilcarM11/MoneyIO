package com.menjivar.android.ingresosgastos.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.menjivar.android.ingresosgastos.RegistryActivity;
import com.menjivar.android.ingresosgastos.data.DataAdapter;
import com.menjivar.android.ingresosgastos.data.DataHolder;
import com.menjivar.android.ingresosgastos.R;
import com.menjivar.android.ingresosgastos.data.Rubro;

/**
 *
 * Created by Amilcar Menjivar on 30/12/2014.
 */
public class RubroCardView extends ItemCard {

    private int rubroID = -1;

    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mMoneyView;
    private TextView mCountView;
    private ImageButton mButton;

    private String mName;
    private String mDescription;
    private double mMoney = 0;
    private int mCount = 0;
    private int mButtonAction = 0;

    private static final int[] ICONS = { R.drawable.ic_action_options, R.drawable.ic_action_remove };

    public RubroCardView(Context context) {
        this(context, null);
    }

    public RubroCardView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.itemCardStyle);

        // Read attributes
        if( attrs != null ) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RubroCardView, R.attr.itemCardStyle, 0);
            try {
                // Button
                mButtonAction = a.getInt(R.styleable.RubroCardView_buttonAction, 0);
            } finally {
                a.recycle();
            }
        }

        // Inflate Layout
        LayoutInflater.from(context).inflate(R.layout.view_rubro, this, true);

        // Save the views
        mTitleView = (TextView) this.findViewById(R.id.item_rubro_title);
        mDescriptionView = (TextView) this.findViewById(R.id.item_rubro_description);
        mMoneyView = (TextView) this.findViewById(R.id.item_rubro_monto);
        mCountView = (TextView) this.findViewById(R.id.item_rubro_cantidad);
        mButton = (ImageButton) this.findViewById(R.id.item_rubro_optionsBtn);

        // Set the correct icon for the button
        mButton.setImageResource(ICONS[mButtonAction]);
    }

    @Override
    public int getItemID() {
        return rubroID;
    }

    public int getID() {
        return rubroID;
    }

    public void setInformation(Rubro rubro) {
        rubroID = rubro.id;
        mName = rubro.name;
        mDescription = rubro.description;
        mMoney = rubro.money;
        mCount = rubro.count;
        updateView();
    }

    public void updateView() {
        mTitleView.setText(mName);
        mMoneyView.setText(moneyFormat(mMoney));
        mDescriptionView.setText(mDescription);
        mCountView.setText(mCount + " cuentas");
        if(mDescription == null || mDescription.equals("")) {
            mDescriptionView.setVisibility(View.GONE);
        }
        else {
            mDescriptionView.setVisibility(View.VISIBLE);
        }
        invalidate();
        requestLayout();
    }

    public static DataAdapter<Rubro> createDataAdapter() {
        return new DataAdapter<Rubro>() {

            @Override
            public DataHolder<Rubro> onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new MyHolder(new RubroCardView(viewGroup.getContext()));
            }
        };
    }

    private static class MyHolder extends DataHolder<Rubro> {

        private RubroCardView mView;

        public MyHolder(View itemView) {
            super(itemView);
            this.mView = (RubroCardView) itemView;
        }

        @Override
        public void fillInformation(Rubro rubro) {
            mView.setInformation(rubro);
        }
    }

}
