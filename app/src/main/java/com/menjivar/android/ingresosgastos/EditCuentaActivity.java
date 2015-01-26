package com.menjivar.android.ingresosgastos;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.menjivar.android.ingresosgastos.adapter.Entry;
import com.menjivar.android.ingresosgastos.adapter.SectionedArrayAdapter;
import com.menjivar.android.ingresosgastos.data.Cuenta;
import com.menjivar.android.ingresosgastos.data.DBHelper;
import com.menjivar.android.ingresosgastos.data.Rubro;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Amilcar Menjivar on 11/01/2015.
 */
public class EditCuentaActivity extends ActionBarActivity {

    private int entryID = -1;

    TextView mNameView;
    TextView mDescriptionView;
    Spinner mRubroView;
    Spinner mNatureView;

    SectionedArrayAdapter listRubroAdapter;
    SectionedArrayAdapter listNatureAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cuenta);

        // Find the views
        mNameView = (TextView) this.findViewById(R.id.entry_nameField);
        mDescriptionView = (TextView) this.findViewById(R.id.entry_descriptionField);
        mRubroView = (Spinner) this.findViewById(R.id.entry_rubroSpinner);
        mNatureView = (Spinner) this.findViewById(R.id.entry_natureSpinner);

        // Set the adapters to the spinners
        listRubroAdapter = new SectionedArrayAdapter(this, R.layout.sectioned_list_entry, R.id.entry_separator);
        listNatureAdapter = new SectionedArrayAdapter(this, R.layout.sectioned_list_entry, R.id.entry_separator);
        mRubroView.setAdapter(listRubroAdapter);
        mNatureView.setAdapter(listNatureAdapter);

        // Popuate the spinners
        populateSpinners();

        // When editing, load the information from DB and write it to the views.
        entryID = this.getIntent().getIntExtra("entryID", -1);
        if( entryID != -1 ) { // Find information
            Cuenta cuenta = DBHelper.getInstance().getCuenta( entryID );

            // Write information to the views.
            mNameView.setText( cuenta.name );
            mDescriptionView.setText( cuenta.description );
            int indexRubro = getEntryPosition(listRubroAdapter, cuenta.rubroID);
            if( indexRubro != -1 ) {
                mRubroView.setSelection(indexRubro, false);
            }
            int indexNature = getEntryPosition(listNatureAdapter, cuenta.nature);
            if( indexNature != -1 ) {
                mNatureView.setSelection(indexNature, false);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_accept) {
            String nameText = mNameView.getText().toString();
            String descriptionText = mDescriptionView.getText().toString();
            int rubro = ((Entry)mRubroView.getSelectedItem()).getID();
            int nature = ((Entry)mNatureView.getSelectedItem()).getID();

            if( entryID == -1 ) { // New entry
                DBHelper.getInstance().insertCuenta(nameText, descriptionText, nature, rubro);

            } else { // Editing entry.
                DBHelper.getInstance().updateCuenta(entryID, nameText, descriptionText, nature, rubro);
            }
            done();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void done() {
        setResult(RESULT_OK);
        finish();
    }

    private int getEntryPosition(SectionedArrayAdapter adapter, int itemID) {
        for(int i=0; i<adapter.getCount(); i++) {
            Entry item = adapter.getItem(i);
            if( item != null && item.getID() == itemID )
                return i;
        }
        return -1;
    }

    private void populateSpinners() {
        // Load Rubros
        for(Rubro rubro : DBHelper.iterableCursor(
                DBHelper.getInstance().query(DBHelper.Query.RubroList), Rubro.class)) {
            listRubroAdapter.add( new RubroEntry(rubro.id, rubro.name, rubro.description) );
        }

        // Load Natures
        // TODO: this must be done with a database.
        for(Cuenta.Nature nature : Cuenta.Nature.values()) {
            listNatureAdapter.add( new NatureEntry(nature.ordinal(), nature.toString()));
        }

    }

    private class RubroEntry extends Entry {

        public RubroEntry(int id, String rubroName, String rubroDescription) {
            super(null, id);
            addData("title", rubroName);
            addData("description", rubroDescription);
        }

        @Override
        public Map<String, Integer> onFigureOutWhereToPlaceStuff() {
            HashMap<String,Integer> indxMap = new HashMap<String,Integer>();
            indxMap.put("title", R.id.entry_title);
            indxMap.put("description", R.id.entry_description);
            return indxMap;
        }

        @Override
        public void onSetInformation(String key, View view, Object value) {
            switch(key) {
                 case "title":
                case "description":
                    ((TextView)view).setText((String) value);
                    break;
                default:
                    Log.d("ENTRY", "Missing behavious for key: " + key);
            }
        }
    }

    private class NatureEntry extends Entry {

        public NatureEntry(int id, String nature) {
            super(null, id);
            addData("title", nature);
            addData("description", null);
        }

        @Override
        public Map<String, Integer> onFigureOutWhereToPlaceStuff() {
            HashMap<String,Integer> indxMap = new HashMap<String,Integer>();
            indxMap.put("title", R.id.entry_title);
            indxMap.put("description", R.id.entry_description);
            return indxMap;
        }

        @Override
        public void onSetInformation(String key, View view, Object value) {
            switch(key) {
                case "title":
                    ((TextView)view).setText((String) value);
                    break;
                case "description":
                    view.setVisibility(View.GONE);
                    break;
                default:
                    Log.d("ENTRY", "Missing behaviour for key: " + key);
            }
        }
    }
}
