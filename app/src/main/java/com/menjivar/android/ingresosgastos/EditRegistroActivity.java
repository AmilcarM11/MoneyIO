package com.menjivar.android.ingresosgastos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.menjivar.android.ingresosgastos.adapter.Entry;
import com.menjivar.android.ingresosgastos.adapter.SectionedArrayAdapter;
import com.menjivar.android.ingresosgastos.data.Cuenta;
import com.menjivar.android.ingresosgastos.data.DBHelper;
import com.menjivar.android.ingresosgastos.data.Registro;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Amilcar Menjivar on 11/01/2015.
 */
public class EditRegistroActivity extends ActionBarActivity {

    private int entryID = -1;

    TextView mMoneyView;
    TextView mDescriptionView;
    Spinner mCuentaSpinner;
    DatePicker mDatePicker;

    SectionedArrayAdapter listCuentaAdapter;

    private long result = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_registro);

        // Find the views
        mMoneyView = (TextView) this.findViewById(R.id.entry_moneyField);
        mDescriptionView = (TextView) this.findViewById(R.id.entry_descriptionField);
        mCuentaSpinner = (Spinner) this.findViewById(R.id.entry_cuentaSpinner);
        mDatePicker = (DatePicker) this.findViewById(R.id.entry_datePicker);

        // Set the adapters to the spinners
        listCuentaAdapter = new SectionedArrayAdapter(this, R.layout.sectioned_list_entry, R.id.entry_separator);
        mCuentaSpinner.setAdapter(listCuentaAdapter);

        // Popuate the spinners
        populateSpinners();

        // When editing, load the information from DB and write it to the views.
        entryID = this.getIntent().getIntExtra("entryID", -1);
        if( entryID != -1 ) { // Find information
            Registro registro = DBHelper.getInstance().getRegistro( entryID );

            // Write information to the views.
            mMoneyView.setText( ""+  registro.money ); // TODO: money format.
            mDescriptionView.setText( registro.description );
            int indexCuenta = getEntryPosition(listCuentaAdapter, registro.cuentaID );
            if( indexCuenta != -1 ) {
                mCuentaSpinner.setSelection(indexCuenta, false);
            }
            // TODO: date
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
            double money = Double.parseDouble(mMoneyView.getText().toString().trim());
            String descriptionText = mDescriptionView.getText().toString();
            int cuenta = ((Entry) mCuentaSpinner.getSelectedItem()).getID();

            String date = String.format("%02d/%02d/%s", mDatePicker.getDayOfMonth(), mDatePicker.getMonth()+1, mDatePicker.getYear());

            if( entryID == -1 ) { // New entry
                result = DBHelper.getInstance().insertRegistro(money, descriptionText, cuenta, date);
                // todo: respond with this?
                Toast.makeText(this, "Inserted cuenta on row:"+result, Toast.LENGTH_SHORT).show();

            } else { // Editing entry.
                DBHelper.getInstance().updateRegistro(entryID, money, descriptionText, cuenta, date);
            }
            done();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void done() {
        if( result == -1 ) {
            setResult(RESULT_CANCELED, null);
        } else {
            Intent dataBack = new Intent();
            dataBack.putExtra("ROW_ID", result);
            setResult(RESULT_OK, dataBack);
        }
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
        // Load Cuentas
        for(Cuenta cuenta : DBHelper.iterableCursor(
                DBHelper.getInstance().query(DBHelper.Query.CuentaList), Cuenta.class)) {
            listCuentaAdapter.add(new CuentaEntry(cuenta.id, cuenta.name, cuenta.description, cuenta.rubro));
        }

    }

    private class CuentaEntry extends Entry {

        public CuentaEntry(int id, String cuentaName, String cuentaDescription, String rubroName) {
            super(rubroName, id);
            addData("filter", getFilter());
            addData("title", cuentaName);
            addData("description", cuentaDescription);
        }

        @Override
        public Map<String, Integer> onFigureOutWhereToPlaceStuff() {
            HashMap<String,Integer> indxMap = new HashMap<String,Integer>();
            indxMap.put("filter", R.id.entry_separator);
            indxMap.put("title", R.id.entry_title);
            indxMap.put("description", R.id.entry_description);
            return indxMap;
        }

        @Override
        public void onSetInformation(String key, View view, Object value) {
            switch(key) {
                case "filter":
                    ((TextView)view).setText(((String) value).toUpperCase());
                    break;
                case "title":
                case "description":
                    ((TextView)view).setText((String) value);
                    break;
                default:
                    Log.d("ENTRY", "Missing behavious for key: " + key);
            }
        }
    }
}
