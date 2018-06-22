package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.ToolContract.ToolEntry;

/**
 * {@link ToolCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of tool data as its data source. This adapter knows
 * how to create list items for each row of tool data in the {@link Cursor}.
 */

public class ToolCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ToolCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ToolCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the tool data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current tool can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of tool attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_QUANTITY);

        // Read the tool attributes from the Cursor for the current tool
        String toolName = cursor.getString(nameColumnIndex);
        String toolPrice = "Price: " + String.valueOf(cursor.getFloat(priceColumnIndex));
        String toolQuantity = "Quantity: " + String.valueOf(cursor.getInt(quantityColumnIndex));

        // Update the TextViews with the attributes for the current tool
        nameTextView.setText(toolName);
        priceTextView.setText(toolPrice);
        quantityTextView.setText(toolQuantity);

        Button saleButton = (Button) view.findViewById(R.id.sale_btn);

        int pos = cursor.getPosition();

        saleButton.setTag(pos);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();

                //Message to test if get the right position
                String val = "Message " + position;
                Toast.makeText(context, val, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
