package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
* API Contract for the Inventory app.
*/
public final class ToolContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ToolContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.inventory/tool/ is a valid path for
     * looking at tool data. content://com.example.android.inventory/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_TOOLS = "tools";

    /**
     * Inner class that defines constant values for the tool database table.
     * Each entry in the table represents a single tool.
     */
    public static final class ToolEntry implements BaseColumns {

        /** The content URI to access the tool data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TOOLS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of tools.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOOLS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single tool.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOOLS;

        /** Name of database table for tools */
        public final static String TABLE_NAME = "tools";

        /**
         * Unique ID number for the tool (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the tool.
         *
         * Type: TEXT
         */
        public final static String COLUMN_TOOL_NAME = "name";

        /**
         * Price of the tool.
         *
         * Type: REAL
         */
        public final static String COLUMN_TOOL_PRICE = "price";

        /**
         * Quantity of the tool.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_TOOL_QUANTITY = "quantity";

        /**
         * Name of the supplier.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplier";

        /**
         * Number of the supplier.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "phone_number";
    }
}
