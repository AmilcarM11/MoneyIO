package com.menjivar.android.ingresosgastos;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.menjivar.android.ingresosgastos.data.DBHelper;
import com.menjivar.android.ingresosgastos.data.Rubro;


public class EditRubroActivity extends ActionBarActivity {

    private int entryID = -1;

    TextView mNameView;
    TextView mDescriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rubro);

        // Find the views
        mNameView = (TextView) this.findViewById(R.id.entry_nameField);
        mDescriptionView = (TextView) this.findViewById(R.id.entry_descriptionField);

        // Find information when editing.
        entryID = this.getIntent().getIntExtra("entryID", -1);
        if( entryID != -1 ) { // Find information
            // Get from Database
            Rubro rubro = DBHelper.getInstance().getRubro( entryID );
            if( rubro != null ) {

                // Write information to the views.
                mNameView.setText( rubro.name );
                mDescriptionView.setText( rubro.description );
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

            if( entryID == -1 ) { // New entry
                DBHelper.getInstance().insertRubro(nameText, descriptionText);
            } else { // Editing entry.
                DBHelper.getInstance().updateRubro(entryID, nameText, descriptionText);
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
}
