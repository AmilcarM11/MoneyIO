package com.menjivar.android.ingresosgastos;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.menjivar.android.ingresosgastos.data.DBHelper;
import com.menjivar.android.ingresosgastos.data.DataAdapter;
import com.menjivar.android.ingresosgastos.data.MyCursorLoader;
import com.menjivar.android.ingresosgastos.data.Rubro;
import com.menjivar.android.ingresosgastos.views.ContextMenuRecyclerView;
import com.menjivar.android.ingresosgastos.views.RubroCardView;


/**
 * Fragment used for listing all the "Rubros" in the DB.
 * Created by Amilcar Menjivar on 30/12/2014.
 */
public class ListRubrosFragment extends ListFragment<Rubro> implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ID_RUBRO_ALL = 0;

    private DataAdapter<Rubro> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_rubros, container, false);

        // Recycler View
        ContextMenuRecyclerView recList = (ContextMenuRecyclerView) rootView.findViewById(R.id.cardListRubros);
        recList.setHasFixedSize(true);
        registerForContextMenu(recList); // Context Menu

        // Set the layout manager
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        // Set adapter
        adapter = RubroCardView.createDataAdapter();
        recList.setAdapter(adapter);

        // Load stuff
        getMyActivity().startLoader(LOADER_ID_RUBRO_ALL, this);

        return rootView;
    }

    @Override
    public DataAdapter<Rubro> getAdapter() {
        return adapter;
    }

    @Override
    public void onReload() {
        getMyActivity().restartLoader(LOADER_ID_RUBRO_ALL, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(getMyActivity()) {
            @Override
            public DBHelper.Query getQuery() {
                return DBHelper.Query.Rubro;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getAdapter().onLoadDone(DBHelper.iterableCursor(data, Rubro.class));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //getAdapter().clear();
    }
}
