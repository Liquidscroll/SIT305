package com.example.lostandfoundapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShowItemsActivity extends AppCompatActivity implements ShowItemsAdapter.OnItemClickListener {
    private ItemDatabase dbHelper;
    private RecyclerView rv;
    private ShowItemsAdapter itemAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.show_items_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check if we've returned from an item being deleted in the details fragment.
        // If so, reload the data.
        getSupportFragmentManager().setFragmentResultListener("ITEM_DEL", this, (result, bundle) -> {
            if (result.equals("ITEM_DEL") && bundle.isEmpty()) {
                loadData();
            }
        });

        dbHelper = new ItemDatabase(this);
        setupRecyclerView();
        loadData();
    }

    // Configure recycler view and add divider decoration.
    private void setupRecyclerView() {
        rv = findViewById(R.id.item_list_rv);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dvItemDec = new DividerItemDecoration(rv.getContext(), LinearLayoutManager.VERTICAL);
        rv.addItemDecoration(dvItemDec);
    }
    // Load data from the database and update UI accordingly
    private void loadData() {
        List<Item> items = getItemsFromDB();
        updateUI(items);
    }
    // Get items from the database
    private List<Item> getItemsFromDB()
    {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("adverts", null, null, null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    items.add(new Item(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("post_type")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("phone_number")),
                            cursor.getString(cursor.getColumnIndexOrThrow("description")),
                            cursor.getString(cursor.getColumnIndexOrThrow("date")),
                            cursor.getString(cursor.getColumnIndexOrThrow("location"))
                    ));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
            db.close();
        }
        return items;
    }
    // Update the visibility of 'no items' text and refresh the RecyclerView
    private void updateUI(List<Item> items) {

        TextView notFound = findViewById(R.id.no_items_notification);
        notFound.setVisibility(items.isEmpty() ? TextView.VISIBLE : TextView.GONE);

        if (itemAdapter == null) {
            itemAdapter = new ShowItemsAdapter(items, this);
            rv.setAdapter(itemAdapter);
        } else {
            itemAdapter.updateItems(items);
        }
    }
    @Override public void onItemClick(Item item)
    {
        ItemDetailFragment newFragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("CURR_ITEM", item);
        newFragment.setArguments(args);

        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.show_items_layout, newFragment);
        trans.addToBackStack(null);
        trans.commit();
    }

}
