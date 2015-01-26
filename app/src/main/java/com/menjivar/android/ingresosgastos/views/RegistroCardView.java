package com.menjivar.android.ingresosgastos.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.menjivar.android.ingresosgastos.data.DataAdapter;
import com.menjivar.android.ingresosgastos.data.DataHolder;
import com.menjivar.android.ingresosgastos.R;
import com.menjivar.android.ingresosgastos.data.Registro;

/**
 *
 * Created by Amilcar Menjivar on 30/12/2014.
 */
public class RegistroCardView extends ItemCard {

    private int registroID = -1;

    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mMoneyView;
    private TextView mDateView;

    private String mCuenta;
    private String mDescription;
    private double mMoney = 0;
    private String mDate;

    private static final int[] ICONS = { R.drawable.ic_action_options, R.drawable.ic_action_remove };

    public RegistroCardView(Context context) {
        this(context, null);
    }

    public RegistroCardView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.itemCardStyle);

        // Read attributes
        if( attrs != null ) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RegistroCardView, R.attr.itemCardStyle, 0);
            try {
                // Nothing for now.
            } finally {
                a.recycle();
            }
        }

        // Inflate Layout
        LayoutInflater.from(context).inflate(R.layout.view_registro, this, true);

        // Save the views
        mTitleView = (TextView) this.findViewById(R.id.item_registro_title);
        mDescriptionView = (TextView) this.findViewById(R.id.item_registro_description);
        mMoneyView = (TextView) this.findViewById(R.id.item_registro_monto);
        mDateView = (TextView) this.findViewById(R.id.item_registro_date);
        ImageButton button = (ImageButton) this.findViewById(R.id.item_registro_optionsBtn);

        // Set the correct icon for the button
        button.setImageResource(ICONS[0]);
    }

    @Override
    public int getItemID() {
        return registroID;
    }

    public int getID() {
        return registroID;
    }

    public void setInformation(Registro registro) {
        registroID = registro.id;
        mCuenta = registro.cuenta;
        mDescription = registro.description;
        mMoney = registro.money;
        mDate = registro.date;
        updateView();
    }

    public void updateView() {
        mTitleView.setText(mCuenta); // TODO: when a cuenta is selected, hide this.
        //mTitleView.setVisibility(View.GONE);
        mDescriptionView.setText(mDescription);
        if(mDescription == null || mDescription.equals(""))
            mDescriptionView.setVisibility(View.GONE);
        else
            mDescriptionView.setVisibility(View.VISIBLE);
        mMoneyView.setText(moneyFormat(mMoney));
        mDateView.setText( mDate );
        invalidate();
        requestLayout();
    }

    public String getName() {
        return mCuenta;
    }

    public String getDescription() {
        return mDescription;
    }

    public double getMoney() {
        return mMoney;
    }

    public static DataAdapter<Registro> createDataAdapter() {
        return new DataAdapter<Registro>() {
            @Override
            public DataHolder<Registro> onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new MyHolder(new RegistroCardView(viewGroup.getContext()));
            }
        };
    }

    private static class MyHolder extends DataHolder<Registro> {

        private RegistroCardView mView;

        public MyHolder(View itemView) {
            super(itemView);
            this.mView = (RegistroCardView) itemView;
        }

        @Override
        public void fillInformation(Registro registro) {
            mView.setInformation(registro);
        }
    }

}
