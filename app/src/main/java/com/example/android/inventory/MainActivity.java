package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventory.data.ToolContract.ToolEntry;

/**
 * Displays list of tools that were entered and stored in the app.
 */
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the tool data loader
     */
    private static final int TOOL_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    ToolCursorAdapter mCursorAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the tool data
        ListView toolListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        toolListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of tool data in the Cursor.
        // There is no tool data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new ToolCursorAdapter(this, null);
        toolListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        toolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link DetailActivity}
                 Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                // Form the content URI that represents the specific tool that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ToolEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.tool/tool/2"
                // if the tool with ID 2 was clicked on.
                Uri currentToolUri = ContentUris.withAppendedId(ToolEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentToolUri);

                // Launch the {@link DetailActivity} to display the data for the current tool.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(TOOL_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded tool data into the database. For debugging purposes only.
     */
    private void insertTool() {
        // Create a ContentValues object where column names are the keys,
        // and hammer's tool attributes are the values.
        ContentValues values = new ContentValues();
        values.put(ToolEntry.COLUMN_TOOL_NAME, "Ax");
        values.put(ToolEntry.COLUMN_TOOL_PRICE, 19.84f);
        values.put(ToolEntry.COLUMN_TOOL_QUANTITY, 6);
        values.put(ToolEntry.COLUMN_SUPPLIER_NAME, "Supplier_A");
        values.put(ToolEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "3313467...");

        // Insert a new row for Ax into the provider using the ContentResolver.
        // Use the {@link ToolEntry#CONTENT_URI} to indicate that we want to insert
        // into the Tools database table.
        // Receive the new content URI that will allow us to access Ax's data in the future.
        Uri newUri = getContentResolver().insert(ToolEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertTool();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllTools();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ToolEntry._ID,
                ToolEntry.COLUMN_TOOL_NAME,
                ToolEntry.COLUMN_TOOL_PRICE,
                ToolEntry.COLUMN_TOOL_QUANTITY,
                ToolEntry.COLUMN_SUPPLIER_NAME,
                ToolEntry.COLUMN_SUPPLIER_PHONE_NUMBER };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ToolEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link ToolCursorAdapter} with this new cursor containing updated tool data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    /**
     * Helper method to delete all tool in the database.
     */
    private void deleteAllTools() {
        int rowsDeleted = getContentResolver().delete(ToolEntry.CONTENT_URI, null, null);
    }
}


