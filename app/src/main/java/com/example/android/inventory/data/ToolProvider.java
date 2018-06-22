package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.inventory.data.ToolContract.ToolEntry;

/**
 * {@link ContentProvider} for Inventory app.
 */
public class ToolProvider extends ContentProvider {

    /** URI matcher code for the content URI for the tools table */
    private static final int TOOLS = 100;

    /** URI matcher code for the content URI for a single tool in the tools table */
    private static final int TOOL_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(ToolContract.CONTENT_AUTHORITY, ToolContract.PATH_TOOLS, TOOLS);

        sUriMatcher.addURI(ToolContract.CONTENT_AUTHORITY, ToolContract.PATH_TOOLS + "/#", TOOL_ID);
    }

    /** Database helper that will provide us access to the database */
    private ToolDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new ToolDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TOOLS:
                cursor = database.query(ToolEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TOOL_ID:
                // For the TOOL_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.inventory/tools/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ToolContract.ToolEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the tools table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ToolEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TOOLS:
                return insertTool(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertTool(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(ToolEntry.COLUMN_TOOL_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Tool requires a name");
        }

        // Check that the price is valid
        Float price = values.getAsFloat(ToolEntry.COLUMN_TOOL_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Tool requires valid price");
        }

        // Check that the quantity is valid
        Integer quantity = values.getAsInteger(ToolEntry.COLUMN_TOOL_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Tool requires valid quantity");
        }

        // Check that the supplier name is not null
        String supplierName = values.getAsString(ToolEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Tool requires a valid supplier name");
        }

        // Check that the supplier phone number is not null
        String supplierNumber = values.getAsString(ToolEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierNumber == null) {
            throw new IllegalArgumentException("Tool requires a valid supplier phone number");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new tools with the given values
        long id = database.insert(ToolEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TOOLS:
                return updateTool(uri, contentValues, selection, selectionArgs);
            case TOOL_ID:
                // For the TOOL_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ToolEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateTool(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update tools in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more tools).
     * Return the number of rows that were successfully updated.
     */
    private int updateTool(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Check that the name is not null
        String name = values.getAsString(ToolEntry.COLUMN_TOOL_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Tool requires a name");
        }

        // Check that the price is valid
        Float price = values.getAsFloat(ToolEntry.COLUMN_TOOL_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Tool requires valid price");
        }

        // Check that the quantity is valid
        Integer quantity = values.getAsInteger(ToolEntry.COLUMN_TOOL_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Tool requires valid quantity");
        }

        // Check that the supplier name is not null
        String supplierName = values.getAsString(ToolEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Tool requires a valid supplier name");
        }

        // Check that the supplier phone number is not null
        String supplierNumber = values.getAsString(ToolEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierNumber == null) {
            throw new IllegalArgumentException("Tool requires a valid supplier phone number");
        }

        if(values.size() == 0) {
            return 0;
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ToolEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TOOLS:
                // Delete all rows that match the selection and selection args
                // For case TOOLS:
                rowsDeleted = database.delete(ToolEntry.TABLE_NAME, selection, selectionArgs);

                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                // Return the number of rows deleted
                return rowsDeleted;

            case TOOL_ID:
                // For case TOOL_ID:
                // Delete a single row given by the ID in the URI
                rowsDeleted = database.delete(ToolEntry.TABLE_NAME, selection, selectionArgs);

                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                // Return the number of rows deleted
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TOOLS:
                return ToolEntry.CONTENT_LIST_TYPE;
            case TOOL_ID:
                return ToolEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}