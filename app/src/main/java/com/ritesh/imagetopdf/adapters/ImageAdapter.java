package com.ritesh.imagetopdf.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.ritesh.imagetopdf.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder>{

    private final Context context;
    private List<Uri> images;
    private final ImageClickListener clickListener;
    private final SparseBooleanArray selectedItems;

    public ImageAdapter(Context context, final ImageClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
        selectedItems = new SparseBooleanArray();
    }

    public List<Uri> getImagesList() {
        return images;
    }

    public void addItemsToList(List<Uri>images) {
        int start = 0;
        this.images = images;
        notifyItemRangeInserted(start, images.size());
    }

    public void toggleSelection(int position) {
        if (selectedItems.get(position,false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    public void selectAllItems() {
        if (getSelectedItemCount() == getItemCount()) {
            return;
        }
        for(int pos = 0; pos < getItemCount(); ++pos) {
            if(!selectedItems.get(pos, false)) {
                selectedItems.put(pos,true);
                notifyItemChanged(pos);
            }
        }
    }

    public void removeSelectedItems() {
        ArrayList<Integer> items = getSelectedItems();
        selectedItems.clear();
        for (Integer pos : items) {
            notifyItemChanged(pos);
        }
    }

    public void deleteItems() {
        ArrayList<Integer> items = getSelectedItems();
        Collections.sort(items);
        Collections.reverse(items);
        selectedItems.clear();
        for(int pos : items) {
            images.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    private ArrayList<Integer> getSelectedItems() {
        ArrayList<Integer> items = new ArrayList<>(getSelectedItemCount());
        for(int i = 0; i < selectedItems.size(); ++i) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view,clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(context).load(images.get(position)).into(holder.photo);

        if (selectedItems.get(position)) {
            holder.check.setVisibility(View.VISIBLE);
            holder.checkedView.setVisibility(View.VISIBLE);
        } else {
            holder.check.setVisibility(View.GONE);
            holder.checkedView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (images == null) {
            return 0;
        }
        return images.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        final ShapeableImageView photo;
        final ShapeableImageView check;
        final View checkedView;
        final ImageClickListener clickListener;
        public MyViewHolder(@NonNull View itemView, ImageClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;
            photo = itemView.findViewById(R.id.photo);
            check = itemView.findViewById(R.id.checked);
            checkedView = itemView.findViewById(R.id.checkedView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onImageClick(getAbsoluteAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onImageLongClick(getAbsoluteAdapterPosition());
            return true;
        }
    }

}
