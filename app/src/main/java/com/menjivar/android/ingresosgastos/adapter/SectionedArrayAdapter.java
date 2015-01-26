package com.menjivar.android.ingresosgastos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.menjivar.android.ingresosgastos.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to create lists separated with filters.
 * Created by Amilcar Menjivar on 12/01/2015.
 */
public class SectionedArrayAdapter extends ArrayAdapter<Entry> {

    private static final String tag = "SectionedArrayAdapter";

    private int layoutResourceId; // This must include the "separator" view, as well as the "entry" view.
    private int separatorResID;

    private List<Entry> entries;
    // TODO: might need to override the add method.


    private static final int STATE_UNKNOWN = 0;
    private static final int STATE_SECTIONED_CELL = 1;
    private static final int STATE_REGULAR_CELL = 2;

    private int[] rowStates;

    public SectionedArrayAdapter(Context context, int layoutResourceId, int separatorResID) {
        this(context, layoutResourceId, separatorResID, new ArrayList<Entry>());
    }

    private SectionedArrayAdapter(Context context, int layoutResourceId, int separatorResID, List<Entry> objects) {
        super(context, layoutResourceId, R.id.entry_title, objects);

        this.layoutResourceId = layoutResourceId;
        this.separatorResID = separatorResID;
        this.entries = objects;
        this.rowStates = new int[entries.size()];
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        rowStates = new int[entries.size()];
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView( position, convertView, parent );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        // I don't have a use for this, yet.
        AdditionalData additionalData = onCreateAdditionalData();

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(layoutResourceId, parent, false);

            // if (position == 2) additionalData.separator.setVisibility(View.GONE); ???

            row.setTag(additionalData);
        } else {
            additionalData = (AdditionalData)row.getTag();
        }

        // Find the views I need.
        View separator = row.findViewById( separatorResID );

        // Get the filter
        Entry entry = entries.get(position);
        String filter = entry.getFilter();

        boolean needSeparator = false;

        switch(rowStates[position]) {
            case STATE_SECTIONED_CELL:
                needSeparator = true;
                break;

            case STATE_REGULAR_CELL:
                needSeparator = false;
                break;

            default:
                if (position == 0) {
                    needSeparator = filter != null;
                } else {
                    Entry lastEntry = entries.get(position-1);
                    String lastFilter = lastEntry.getFilter();

                    if( filter == null ) {
                        needSeparator = lastFilter != null;
                    } else if (!filter.equals(lastFilter)) {
                        needSeparator = true;
                    }
                }
                rowStates[position] = (needSeparator) ? STATE_SECTIONED_CELL : STATE_REGULAR_CELL;
                break;
        }

        if( needSeparator ) {
            setSeparatorVisible(position, separator, filter);
        }

        entry.fillInformation( row );
        return row;
    }

    protected void setSeparatorVisible(int pos, View separatorView, String filter) {
        separatorView.setVisibility(View.VISIBLE);
    }

    protected AdditionalData onCreateAdditionalData() {
        return null;
    }

    class AdditionalData {
        // Anything can go here.
    }

}
