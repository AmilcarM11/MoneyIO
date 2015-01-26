package com.menjivar.android.ingresosgastos;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.menjivar.android.ingresosgastos.data.Cuenta;
import com.menjivar.android.ingresosgastos.data.DBHelper;
import com.menjivar.android.ingresosgastos.data.DataAdapter;
import com.menjivar.android.ingresosgastos.data.Rubro;
import com.menjivar.android.ingresosgastos.views.ContextMenuRecyclerView;
import com.menjivar.android.ingresosgastos.views.ItemCard;


public class RegistryActivity extends ActionBarActivity {

    /*
    * TODO List
    * X Loading Database
    * X Click-handling for selecting an entry and deleting it.
    * X Create/Edit entries
    * - Navigation Drawer
    * - Saving/Restoring State
    * */

    private ViewPager mPager;
    private RegistryPagerAdapter mPageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

        getSupportActionBar().setTitle("Registros");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mPager = (ViewPager) findViewById(R.id.viewPager);
        mPageAdapter = new RegistryPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPageAdapter);
        mPager.setOnPageChangeListener(mPageAdapter);

        // Init the Database
        dbHelper = new DBHelper(this);
    }

    // Action bar buttons

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.registry_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int page = mPager.getCurrentItem();
        switch (item.getItemId()) {
            case R.id.action_add:
                openEditActivity(page, -1);
                return true;

            case R.id.action_refresh:
                refreshPage( mPager.getCurrentItem() );
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Context Menu

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_entry_actions, menu);
        ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) menuInfo;

        // Set title
        int page = mPager.getCurrentItem();
        String title = null;
        ListFragment fragment = (ListFragment) mPageAdapter.getItem(page);
        if( page == 0 ) {
            title = "Rubro: ";
        } else if( page == 1 ) {
            title = "Cuenta: ";
        } else if( page == 2 ) {
            title = "Registro: ";
        }
        if (title != null) {
            title += fragment.getAdapter().getItem(info.position).getText();
            menu.setHeaderTitle(title);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        ItemCard cardView = ItemCard.getCardView(info.view);
        if( cardView == null ) {
            throw new IllegalStateException("Context Menu opened for a null view.");
        }

        int entryType = ItemCard.getItemCardType(cardView);
        int entryID = cardView.getItemID();
        switch (item.getItemId()) {
            case R.id.action_edit:
                openEditActivity(entryType, entryID);
                return true;
            case R.id.action_delete:
                deleteEntry(entryType, entryID);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    // Handle Clicks

    private static final int REQUEST_EDIT_RUBRO = 0;
    private static final int REQUEST_EDIT_CUENTA = 1;
    private static final int REQUEST_EDIT_REGISTRO = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_EDIT_RUBRO:
            case REQUEST_EDIT_CUENTA:
            case REQUEST_EDIT_REGISTRO:
                if (resultCode == RESULT_OK) {
                    // Refresh the correct loader.
                    int page = requestCode;
                    refreshPage(page);
                    if(data != null ) {
                        long rowID = data.getLongExtra("ROW_ID", -1);
                        if( rowID != -1 ) {

                        }
                    }
                }
                break;
        }
    }

    // Use this to open another activities.
    private void openEditActivity(int requestCode, int entryID) {
        Class[] classes = new Class[] { EditRubroActivity.class, EditCuentaActivity.class, EditRegistroActivity.class };
        Intent intent = new Intent(this, classes[requestCode]);
        intent.putExtra("entryID", entryID);
        startActivityForResult(intent, requestCode);
    }

    // Handle clicks

    public void onClick_SelectItem(View view) {
        ItemCard cardView = ItemCard.getCardView(view);
        if( cardView == null )
            return;
        int entryID = cardView.getItemID();
        int entryType = ItemCard.getItemCardType(cardView);

        if( entryType == ItemCard.TYPE_RUBRO ) { // Rubro
            selectedRubroID = entryID;
            mPager.setCurrentItem( ItemCard.TYPE_CUENTA, true); // Set page to "Cuentas"
            refreshPage( ItemCard.TYPE_CUENTA );
        } else if( entryType == ItemCard.TYPE_CUENTA ) { // Cuenta
            selectedCuentaID = entryID;
            mPager.setCurrentItem(ItemCard.TYPE_REGISTRO, true); // Set page to "Registro"
            refreshPage( ItemCard.TYPE_REGISTRO );
        }
    }

    public void onClick_Options(View view) {
        ItemCard cardView = ItemCard.getCardView(view);
        if( cardView != null ) {
            openContextMenu( cardView );
        } else {
            Toast.makeText(this, "Can't open menu", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteEntry(int type, int id) {
        // Delete from Database
        switch( type ) {
            case ItemCard.TYPE_RUBRO:
                dbHelper.deleteRubro(id);
                break;
            case ItemCard.TYPE_CUENTA:
                dbHelper.deleteCuenta(id);
                break;
            case ItemCard.TYPE_REGISTRO:
                dbHelper.deleteRegistro(id);
                break;
        }
        // Remove from display
        ListFragment fragment = (ListFragment) mPageAdapter.getItem(mPager.getCurrentItem());
        fragment.getAdapter().remove(fragment.getAdapter().getItemIndexByID(id));
    }

    private void refreshPage(int page) {
        ListFragment fragment = (ListFragment) mPageAdapter.getItem(page);
        fragment.onReload();
    }

    // Selected Rubro/Cuenta

    private int selectedRubroID = -1;
    private int selectedCuentaID = -1;

    public boolean getHasSelectedRubro() {
        return selectedRubroID != -1;
    }

    public boolean getHasSelectedCuenta() {
        return selectedCuentaID != -1;
    }

    @SuppressWarnings("unchecked")
    public Rubro getSelectedRubro() {
        DataAdapter<Rubro> adapter = ((ListFragment<Rubro>) mPageAdapter.getItem(0)).getAdapter();
        return adapter.getItemByID(selectedRubroID);
    }

    @SuppressWarnings("unchecked")
    public Cuenta getSelectedCuenta() {
        DataAdapter<Cuenta> adapter = ((ListFragment<Cuenta>) mPageAdapter.getItem(1)).getAdapter();
        return adapter.getItemByID( selectedCuentaID );
    }

    // DataBase

    DBHelper dbHelper;

    public DBHelper getDataBaseHelper() {
        return dbHelper;
    }


    // Loader

    // TODO: move all the callbacks to each respective fragment.
    // Only information to be stored here is the selectedRubro and selectedCuenta.
    // Besides that, might want to handle context on each fragment.

    // Used to reload stuff.
    public void startLoader(int loaderID, LoaderManager.LoaderCallbacks<Cursor> callbacks) {
        getSupportLoaderManager().initLoader(loaderID, null, callbacks);
    }

    public void restartLoader(int loaderID, LoaderManager.LoaderCallbacks<Cursor> callbacks) {
        getSupportLoaderManager().restartLoader(loaderID, null, callbacks);
    }

}
