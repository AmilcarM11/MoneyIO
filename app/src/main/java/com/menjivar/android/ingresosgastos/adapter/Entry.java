package com.menjivar.android.ingresosgastos.adapter;


import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Amilcar Menjivar on 12/01/2015.
 */
public abstract class Entry {


    private final int id;
    private final String filter;

    private HashMap<String,Object> dataMap = new HashMap<String,Object>();

    public Entry(String filter, int id) {
        this.filter = filter;
        this.id = id;
        this.dataMap = new HashMap<String, Object>();
    }

    public String getFilter() {
        return filter;
    }

    public int getID() {
        return id;
    }

    public Entry addData(String key, Object value) {
        this.dataMap.put(key, value);
        return this;
    }

    // here i must create the map, and return it.
    // the filter text must also be included here.
    public abstract Map<String,Integer> onFigureOutWhereToPlaceStuff();

    // fill the view with the dataMap.
    public abstract void onSetInformation(String key, View view, Object value);

    // This is only used to fill-in the information on the entries.
    // The titles are handled elsewhere.
    public final void fillInformation(View entryView) {
        Map<String,Integer> keyMap = onFigureOutWhereToPlaceStuff();
        if( keyMap != null ) {
            for(String key : keyMap.keySet() ) {
                // For this to work, keyMap must only contain resIDs that correspond to views.
                // Also, every key in keyMap must also be available in dataMap

                View view = entryView.findViewById(keyMap.get(key));
                if( view == null || !dataMap.containsKey(key) ) {
                    String reason = "Missing information, key: %s, view: %s, containsData: %s";
                    reason = String.format(reason, key, view == null ? "null" : "not null", dataMap.containsKey(key));
                    throw new IllegalStateException("[Entry] " + reason);
                    // Every key must have a valid resID and value.
                } else {
                    onSetInformation(key, view, dataMap.get(key) );
                }
            }
        }
    }

}
