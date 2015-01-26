package com.menjivar.android.ingresosgastos.data;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 *
 * Created by Amilcar Menjivar on 05/01/2015.
 */
public abstract class DataAdapter<D extends Data> extends RecyclerView.Adapter<DataHolder<D>> {

    private ArrayList<D> dataList = new ArrayList<D>();

    @Override
    public abstract DataHolder<D> onCreateViewHolder(ViewGroup viewGroup, int i);

    @Override
    public void onBindViewHolder(DataHolder<D> dataHolder, int i) {
        dataHolder.fillInformation(dataList.get(i));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void add(D d) {
        insert(dataList.size(), d);
    }

    public void insert(int pos, D d) {
        dataList.add(pos, d);
        this.notifyItemInserted(pos);
    }

    public D getItem(int pos) {
        return dataList.get(pos);
    }

    public int getItemIndexByID(int id) {
        int size = dataList.size();
        for(int i=0; i<size;i++){
            D d = dataList.get(i);
            if(d != null && d.getID()== id)
                return i;
        }
        return -1;
    }

    public D getItemByID(int id) {
        int index = getItemIndexByID(id);
        return index == -1 ? null : getItem(index);
    }

    public void remove(int pos) {
        dataList.remove(pos);
        this.notifyItemRemoved(pos);
    }

    public void clear() {
        int size = dataList.size();
        dataList.clear();
        this.notifyItemRangeRemoved(0, size);
    }

    public void onLoadDone(Iterable<D> results) {
        this.clear();
        for(D d : results) {
            if( d != null )
                dataList.add(d);
        }
        this.notifyItemRangeInserted(0, dataList.size());
    }

}
