package com.menjivar.android.ingresosgastos.data;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 *
 * Created by Amilcar Menjivar on 05/01/2015.
 */
public abstract class DataHolder<D extends Data> extends RecyclerView.ViewHolder {

    public DataHolder(View itemView) {
        super(itemView);
    }

    public abstract void fillInformation(D d);

    void test() {
    }


}
