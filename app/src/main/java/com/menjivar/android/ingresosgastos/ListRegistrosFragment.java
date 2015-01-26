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
import com.menjivar.android.ingresosgastos.data.Registro;
import com.menjivar.android.ingresosgastos.views.ContextMenuRecyclerView;
import com.menjivar.android.ingresosgastos.views.CuentaCardView;
import com.menjivar.android.ingresosgastos.views.RegistroCardView;



/**
 * Fragment used for listing all the "Registros" in the DB.
 * Created by Amilcar Menjivar on 30/12/2014.
 */
public class ListRegistrosFragment extends ListFragment<Registro> implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ID_REGISTRO_ALL = 3;
    public static final int LOADER_ID_REGISTRO_FILTERED = 4;

    private DataAdapter<Registro> adapter;

    private CuentaCardView mHeaderView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_registros, container, false);

        // Recycler View
        ContextMenuRecyclerView recList = (ContextMenuRecyclerView) rootView.findViewById(R.id.cardListRegistros);
        recList.setHasFixedSize(true);
        registerForContextMenu(recList); // Context Menu

        // Set the layout manager
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        // Set adapter
        adapter = RegistroCardView.createDataAdapter();
        recList.setAdapter(adapter);

        // Header
        mHeaderView = (CuentaCardView) rootView.findViewById(R.id.headerCuenta);

        // Load stuff
        int loaderID = isFiltered() ? LOADER_ID_REGISTRO_FILTERED : LOADER_ID_REGISTRO_ALL;
        getMyActivity().startLoader(loaderID, this);

        return rootView;
    }

    private boolean isFiltered() {
        return getMyActivity().getHasSelectedCuenta();
    }

    private Cuenta getSelectedHeader() {
        return getMyActivity().getSelectedCuenta();
    }

    private void updateHeader() {
        Cuenta headerCuenta;
        if( isFiltered() && (headerCuenta = getSelectedHeader()) != null ) {
            mHeaderView.setVisibility(View.VISIBLE);
            mHeaderView.setInformation( headerCuenta );
        } else {
            mHeaderView.setVisibility(View.GONE);
        }
    }

    @Override
    public DataAdapter<Registro> getAdapter() {
        return adapter;
    }

    @Override
    public void onReload() {
        int loaderID = isFiltered() ? LOADER_ID_REGISTRO_FILTERED : LOADER_ID_REGISTRO_ALL;
        getMyActivity().restartLoader(loaderID, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if( id == LOADER_ID_REGISTRO_ALL) {
            return new MyCursorLoader(getMyActivity()) {
                @Override
                public DBHelper.Query getQuery() {
                    return DBHelper.Query.Registro;
                }
            };
        } else if ( id == LOADER_ID_REGISTRO_FILTERED) {
            return new MyCursorLoader(getMyActivity()) {
                @Override
                // TODO: Cuenta filtered query
                public DBHelper.Query getQuery() {
                    return DBHelper.Query.Registro;
                }
            };
        }
        return null; // TODO: throw an exception maybe?
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getAdapter().onLoadDone(DBHelper.iterableCursor(data, Registro.class));
        updateHeader();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
