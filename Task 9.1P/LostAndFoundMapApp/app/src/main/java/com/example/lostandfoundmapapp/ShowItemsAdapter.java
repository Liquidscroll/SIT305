package com.example.lostandfoundmapapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class ShowItemsAdapter extends RecyclerView.Adapter<ShowItemsAdapter.ItemViewHolder>
{

    public interface OnItemClickListener { void onItemClick(Item item); }
    private final List<Item> item_list;
    private final OnItemClickListener listener;
    public ShowItemsAdapter(List<Item> item_list, OnItemClickListener listener)
    {
        this.item_list = item_list;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ShowItemsAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rows_items, parent, false);
        return new ItemViewHolder(view, this.listener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowItemsAdapter.ItemViewHolder holder, int position) {
        holder.getItemTypeView().setText(item_list.get(position).post_type);
        holder.getItemDescView().setText(item_list.get(position).name);
    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }

    public Item getItemByPos(int position) { return item_list.get(position); }
    public void updateItems(List<Item> newItems)
    {
        this.item_list.clear();
        this.item_list.addAll(newItems);
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView ad_type, item_desc;
        public ItemViewHolder(View view, OnItemClickListener listener, ShowItemsAdapter adapter)
        {
            super(view);
            item_desc = view.findViewById(R.id.adapter_name_text);
            ad_type = view.findViewById(R.id.adapter_ad_type_text);

            view.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION && listener != null)
                {
                    listener.onItemClick(adapter.getItemByPos(pos));
                }
            });
        }

        public TextView getItemDescView() {
            return item_desc;
        }
        public TextView getItemTypeView() { return ad_type; }

    }
}
