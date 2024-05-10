package com.example.lostandfoundapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.concurrent.TimeUnit;
public class ItemDetailFragment extends Fragment
{
    private Item item = null;
    private ItemDatabase dbHelper;
    private TextView item_title, item_phone, item_desc, item_date, item_loc;
    private Button remove_btn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_item_details, container, false);


        initViews(view);
        loadItemDetails();

        remove_btn.setOnClickListener(v -> {
            if(item != null) { removeItem(item.id); }
        });
        return view;
    }

    private void initViews(View view)
    {
        item_title = view.findViewById(R.id.frag_post_type);
        item_phone = view.findViewById(R.id.frag_phone);
        item_desc = view.findViewById(R.id.frag_desc);
        item_date = view.findViewById(R.id.frag_date);
        item_loc = view.findViewById(R.id.frag_loc);
        remove_btn = view.findViewById(R.id.frag_remove_btn);
    }

    private void loadItemDetails()
    {
        Bundle args = getArguments();
        if(args != null)
        {
            item = getArguments().getParcelable("CURR_ITEM");
        }
        if(item != null)
        {
            item_title.setText(String.format("%s %s", item.post_type, item.name));

            long timeDiffInMillis = System.currentTimeMillis() - item.date.getTime();
            long timeDiffInDays = TimeUnit.MILLISECONDS.toDays(timeDiffInMillis);
            if(timeDiffInDays != 0) {
                item_date.setText(String.format("%s days ago", timeDiffInDays));
            }
            else
            {
                item_date.setText(R.string.zero_days_text);
            }

            item_loc.setText(String.format("At %s", item.location));
            item_phone.setText(String.format("Contact: %s", item.phone_number));
            item_desc.setText(item.description);


        }
    }

    private void removeItem(int itemID)
    {
        dbHelper = new ItemDatabase(getContext());
        dbHelper.deleteItem(itemID);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.setFragmentResult("ITEM_DEL", Bundle.EMPTY);
        fm.popBackStackImmediate();

    }
}
