package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.inventory.data.ToolContract.ToolEntry;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>  {

    /** Identifier for the tool data loader */
    private static final int EXISTING_TOOL_LOADER = 0;

    /** Content URI for the existing tool (null if it's a new tool) */
    private Uri mCurrentToolUri;

    /** EditText field to enter the tool's name */
    private TextView mNameEditText;

    /** EditText field to enter the tool's price */
    private TextView mPriceEditText;

    /** EditText field to enter the tool's quantity */
    private TextView mQuantityEditText;

    /** EditText field to enter the supplier's name */
    private TextView mSupplierEditText;

    /** EditText field to enter the supplier's phone number */
    private TextView mSupplierNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        mCurrentToolUri = intent.getData();

        // Otherwise this is an existing tool, so change app bar to say "Edit Tool"
        setTitle(getString(R.string.detail_activity_title_tool));

        // Initialize a loader to read the tool data from the database
        // and display the current values in the editor
        getLoaderManager().initLoader(EXISTING_TOOL_LOADER, null, this);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (TextView) findViewById(R.id.detail_tool_filed_name);
        mPriceEditText = (TextView) findViewById(R.id.detail_tool_filed_price);
        mQuantityEditText = (TextView) findViewById(R.id.detail_tool_filed_quantity);
        mSupplierEditText = (TextView) findViewById(R.id.detail_tool_filed_supplier_name);
        mSupplierNumberEditText = (TextView) findViewById(R.id.detail_tool_filed_supplier_number);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Modify" menu option
            case R.id.action_modify:
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(DetailActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific tool that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ToolEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.tool/tool/2"
                // if the tool with ID 2 was clicked on.
                //Uri currentToolUri = ContentUris.withAppendedId(ToolEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(mCurrentToolUri);

                // Launch the {@link EditorActivity} to display the data for the current tool.
                startActivity(intent);
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // Continue with handling back button press
        super.onBackPressed();
        return;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all tool attributes, define a projection that contains
        // all columns from the tool table
        String[] projection = {
                ToolEntry._ID,
                ToolEntry.COLUMN_TOOL_NAME,
                ToolEntry.COLUMN_TOOL_PRICE,
                ToolEntry.COLUMN_TOOL_QUANTITY,
                ToolEntry.COLUMN_SUPPLIER_NAME,
                ToolEntry.COLUMN_SUPPLIER_PHONE_NUMBER };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentToolUri,         // Query the content URI for the current tool
                projection,              // Columns to include in the resulting Cursor
                null,           // No selection clause
                null,       // No selection arguments
                null);         // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of tool attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_SUPPLIER_NAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplieNumber = cursor.getString(supplierNumberColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(String.valueOf(quantity));
            mSupplierEditText.setText(supplierName);
            mSupplierNumberEditText.setText(supplieNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierNumberEditText.setText("");
    }
}
