package com.menjivar.android.ingresosgastos.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.menjivar.android.ingresosgastos.R;

import java.text.DecimalFormat;

/**
 * Used to set the layout parameters on all the implementing views.
 * Created by Amilcar Menjivar on 06/01/2015.
 */
public abstract class ItemCard extends CardView {

    public static final int TYPE_RUBRO = 0;
    public static final int TYPE_CUENTA = 1;
    public static final int TYPE_REGISTRO = 2;

    protected ItemCard(Context context) {
        this(context, null);
    }

    protected ItemCard(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.itemCardStyle);
    }

    public ItemCard(Context context, AttributeSet attrs, int defAttrs) {
        super(context, attrs, defAttrs);

        // Set width and height.
        MarginLayoutParams layoutParams = new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // Set margins.
        layoutParams.setMargins( (int)dip(8), (int)dip(2), (int)dip(8), (int)dip(2) ); // left, top, right, bottom
        // Apply params.
        this.setLayoutParams(layoutParams);
    }

    public static ItemCard getCardView(View view) {
        View curr = view;
        int failsafe = 7; // Used to prevent infinite loops.
        while( curr != null && failsafe-- > 0 ) {
            if(curr instanceof ItemCard)
                return (ItemCard) curr;
            curr = (View) curr.getParent();
        }
        return null;
    }

    // 0=RubroCardView, 1=CuentaCardView, 2=RegistroCardView
    public static int getItemCardType(View view) {
        if( view instanceof RubroCardView )
            return TYPE_RUBRO;
        if( view instanceof CuentaCardView )
            return TYPE_CUENTA;
        if( view instanceof RegistroCardView )
            return TYPE_REGISTRO;
        return -1;
    }

    // returns the ID of the item represented by this ItemCard
    public abstract int getItemID();


    // TODO: move to util interface
    protected float dip(float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, getResources().getDisplayMetrics());
    }

    public static String moneyFormat(double money) {
        DecimalFormat format = new DecimalFormat("###,###,##0.00");
        return "$ " + format.format(money);
    }
}
