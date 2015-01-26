package com.menjivar.android.ingresosgastos;

import android.support.v4.app.Fragment;

import com.menjivar.android.ingresosgastos.data.Data;
import com.menjivar.android.ingresosgastos.data.DataAdapter;

/**
 *
 * Created by Amilcar Menjivar on 06/01/2015.
 */
public abstract class ListFragment<T extends Data> extends Fragment {

    public RegistryActivity getMyActivity() {
        return (RegistryActivity) getActivity();
    }


    // Returns the adapter used to hold all the information of this fragment.
    // Must not be called before onCreateView
    public abstract DataAdapter<T> getAdapter();

    // Used to refresh the fragment.
    public abstract void onReload();

}
