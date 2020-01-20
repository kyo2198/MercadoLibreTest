package com.mercadolibre.test.Adapters;

import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.widget.TextView;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.mercadolibre.test.R;
import com.mercadolibre.test.Model.Item;
import com.mercadolibre.test.Utils.DownloadImageTask;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private ItemViewClickListener itemViewClickListener;

    private List<Item> items;

    public ItemsAdapter(List<Item> items, ItemViewClickListener itemViewClickListener) {
        this.items = items;

        this.itemViewClickListener = itemViewClickListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.item, parent, false);

        ItemViewHolder viewHolder = new ItemViewHolder(view, itemViewClickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int position) {
        Item item = items.get(position);

        ImageView thumbnail = itemViewHolder.thumbnail;
        new DownloadImageTask(thumbnail).execute(item.GetThumbnail());

        TextView description = itemViewHolder.description;
        description.setText(item.GetDescription());

        TextView price = itemViewHolder.price;
        price.setText("$ " + item.GetPrice());

        TextView shipping = itemViewHolder.shipping;
        if(item.GetShipping()== false)
            shipping.setVisibility(View.GONE);
        else
            shipping.setVisibility(View.VISIBLE);

        TextView condition = itemViewHolder.condition;
        if(item.GetCondition().isEmpty() || item.GetCondition().equals("null")) {
            condition.setVisibility(View.GONE);
        } else {
            condition.setText(item.GetCondition());
            condition.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemViewClickListener itemViewClickListener;

        public ImageView thumbnail;

        public TextView description;
        public TextView price;
        public TextView shipping;
        public TextView condition;

        public ItemViewHolder(View view, ItemViewClickListener itemViewClickListener) {
            super(view);

            thumbnail = view.findViewById(R.id.thumbnail);

            description = view.findViewById(R.id.description);
            price = view.findViewById(R.id.price);
            shipping = view.findViewById(R.id.shipping);
            condition = view.findViewById(R.id.condition);

            this.itemViewClickListener = itemViewClickListener;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            this.itemViewClickListener.OnClick(view, getAdapterPosition());
        }
    }

    public interface ItemViewClickListener {
        void OnClick(View view, int position);
    }
}