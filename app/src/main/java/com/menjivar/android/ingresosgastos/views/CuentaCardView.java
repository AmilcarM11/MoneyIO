package com.menjivar.android.ingresosgastos.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.menjivar.android.ingresosgastos.data.Cuenta;
import com.menjivar.android.ingresosgastos.data.DataAdapter;
import com.menjivar.android.ingresosgastos.data.DataHolder;
import com.menjivar.android.ingresosgastos.R;

/**
 *
 * Created by Amilcar Menjivar on 30/12/2014.
 */
public class CuentaCardView extends ItemCard {

    private int cuentaID = -1;

    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mMoneyView;
    private TextView mNatureView;
    private TextView mRubroView;
    private ImageButton mButton;

    private String mName;
    private String mDescription;
    private double mMoney = 0;
    private String mNature;
    private String mRubro;

    private int mButtonAction = BUTTON_ACTION_OPTIONS;

    public static final int BUTTON_ACTION_OPTIONS = 0;
    public static final int BUTTON_ACTION_REMOVE = 1;

    private static final int[] ICONS = { R.drawable.ic_action_options, R.drawable.ic_action_remove };

    public CuentaCardView(Context context) {
        this(context, null);
    }

    public CuentaCardView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.itemCardStyle);

        // Read attributes
        if( attrs != null ) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CuentaCardView, R.attr.itemCardStyle, 0);
            try {
                // Button
                mButtonAction = a.getInt(R.styleable.CuentaCardView_buttonAction, BUTTON_ACTION_OPTIONS);
            } finally {
                a.recycle();
            }
        }

        // Inflate Layout
        LayoutInflater.from(context).inflate(R.layout.view_cuenta, this, true);

        // Save the views
        mTitleView = (TextView) this.findViewById(R.id.item_cuenta_title);
        mDescriptionView = (TextView) this.findViewById(R.id.item_cuenta_description);
        mMoneyView = (TextView) this.findViewById(R.id.item_cuenta_monto);
        mNatureView = (TextView) this.findViewById(R.id.item_cuenta_nature);
        mRubroView = (TextView) this.findViewById(R.id.item_cuenta_rubro);
        mButton = (ImageButton) this.findViewById(R.id.item_cuenta_optionsBtn);

        // Set the correct icon for the button
        mButton.setImageResource(ICONS[mButtonAction]);
    }

    @Override
    public int getItemID() {
        return cuentaID;
    }

    public int getID() {
        return cuentaID;
    }

    public void setInformation(Cuenta cuenta) {
        cuentaID = cuenta.id;
        mName = cuenta.name;
        mDescription = cuenta.description;
        mMoney = cuenta.money;
        mNature = cuenta.nature().toString();
        mRubro = cuenta.rubro;
        updateView();
    }

    public void updateView() {
        mTitleView.setText(mName);
        mDescriptionView.setText(mDescription);
        if(mDescription == null || mDescription.equals(""))
            mDescriptionView.setVisibility(View.GONE);
        else
            mDescriptionView.setVisibility(View.VISIBLE);
        mMoneyView.setText( moneyFormat(mMoney));
        mNatureView.setText( mNature );
        mRubroView.setText( mRubro ); // TODO: when a rubro is selected, hide this view.
        //mRubroView.setVisibility( View.GONE );
        invalidate();
        requestLayout();
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public double getMoney() {
        return mMoney;
    }

    public String getNature() {
        return mNature;
    }

    public String getRubro() {
        return mRubro;
    }

    public static DataAdapter<Cuenta> createDataAdapter() {
        return new DataAdapter<Cuenta>() {
            @Override
            public DataHolder<Cuenta> onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new MyHolder(new CuentaCardView(viewGroup.getContext()));
            }
        };
    }

    private static class MyHolder extends DataHolder<Cuenta> {

        private CuentaCardView mView;

        public MyHolder(View itemView) {
            super(itemView);
            this.mView = (CuentaCardView) itemView;
        }

        @Override
        public void fillInformation(Cuenta cuenta) {
            mView.setInformation(cuenta);
        }
    }

}
