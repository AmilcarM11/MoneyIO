package com.menjivar.android.ingresosgastos;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.menjivar.android.ingresosgastos.data.Cuenta;
import com.menjivar.android.ingresosgastos.data.DBHelper;
import com.menjivar.android.ingresosgastos.data.DataAdapter;
import com.menjivar.android.ingresosgastos.data.MyCursorLoader;
import com.menjivar.android.ingresosgastos.data.Rubro;
import com.menjivar.android.ingresosgastos.views.ContextMenuRecyclerView;
import com.menjivar.android.ingresosgastos.views.CuentaCardView;
import com.menjivar.android.ingresosgastos.views.RubroCardView;

/**
 * Fragment used for listing all the "Cuentas" in the DB.
 * Created by Amilcar Menjivar on 30/12/2014.
 */
public class ListCuentasFragment extends ListFragment<Cuenta> implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ID_CUENTA_ALL = 1;
    public static final int LOADER_ID_CUENTA_FILTERED = 2;

    private DataAdapter<Cuenta> adapter;

    private RubroCardView mHeaderView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_cuentas, container, false);

        // Recycler View
        ContextMenuRecyclerView recList = (ContextMenuRecyclerView) rootView.findViewById(R.id.cardListCuentas);
        recList.setHasFixedSize(true);
        registerForContextMenu(recList); // Context Menu

        // Set the layout manager
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        // Set adapter
        adapter = CuentaCardView.createDataAdapter();
        recList.setAdapter(adapter);

        // Header
        mHeaderView = (RubroCardView) rootView.findViewById(R.id.headerRubro);

        // Load stuff
        int loaderID = isFiltered() ? LOADER_ID_CUENTA_FILTERED : LOADER_ID_CUENTA_ALL;
        getMyActivity().startLoader(loaderID, this);

        return rootView;
    }

    private boolean isFiltered() {
        return getMyActivity().getHasSelectedRubro();
    }

    private Rubro getSelectedHeader() {
        return getMyActivity().getSelectedRubro();
    }

    private void updateHeader() {
        Rubro selectedRubro;
        if( isFiltered() && (selectedRubro = getSelectedHeader()) != null ) {
            mHeaderView.setVisibility(View.VISIBLE);
            mHeaderView.setInformation( selectedRubro );
        } else {
            mHeaderView.setVisibility(View.GONE);
        }
    }

    @Override
    public DataAdapter<Cuenta> getAdapter() {
        return adapter;
    }

    @Override
    public void onReload() {
        // Check if a rubro was selected.
        int loaderID = isFiltered() ? LOADER_ID_CUENTA_FILTERED : LOADER_ID_CUENTA_ALL;
        getMyActivity().restartLoader(loaderID, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if( id == LOADER_ID_CUENTA_ALL) {
            return new MyCursorLoader(getMyActivity()) {
                @Override
                public DBHelper.Query getQuery() {
                    return DBHelper.Query.Cuenta;
                }
            };
        } else if ( id == LOADER_ID_CUENTA_FILTERED) {
            return new MyCursorLoader(getMyActivity()) {
                @Override
                // TODO: Cuenta filtered query
                public DBHelper.Query getQuery() {
                    return DBHelper.Query.Cuenta;
                }
            };
        }
        return null; // TODO: throw an exception maybe?
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getAdapter().onLoadDone(DBHelper.iterableCursor(data, Cuenta.class));
        updateHeader();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //getAdapter().clear();
        //  loader.reset();
    }
}
