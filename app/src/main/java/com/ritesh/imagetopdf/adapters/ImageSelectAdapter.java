package com.ritesh.imagetopdf.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.ritesh.imagetopdf.R;
import com.ritesh.imagetopdf.model.ImageItem;

import java.util.ArrayList;
import java.util.List;

public class ImageSelectAdapter extends RecyclerView.Adapter<ImageSelectAdapter.MyViewHolder>{

    private final Context context;
    private List<ImageItem> images;
    private List<Uri> selectedUris;
    private final ImageClickListener clickListener;
    public ImageSelectAdapter(Context context, ImageClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
        images = new ArrayList<>();
        selectedUris = new ArrayList<>();
    }

    public void updateList(List<ImageItem> images) {
        final ImageSelectDiffCallback diffCallback = new ImageSelectDiffCallback(this.images, images);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.images = images;
        diffResult.dispatchUpdatesTo(this);
    }

    public void toggleSelection(int position) {
        images.get(position).isSelected = !images.get(position).isSelected;
        if (images.get(position).isSelected) {
            selectedUris.add(images.get(position).imageUri);
        } else {
            selectedUris.remove(images.get(position).imageUri);
        }
        notifyItemChanged(position);
    }

    public void toggleSelection(List<Uri> images) {
        selectedUris.addAll(images);
    }

    public List<Uri> getSelectedImages() {
        return selectedUris;
    }

    public int getSelectedImagesCount() {
        return selectedUris.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view,clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(context).load(images.get(position).imageUri).into(holder.image);
        if (images.get(position).isSelected) {
            holder.checkedView.setVisibility(View.VISIBLE);
            holder.checkedIcon.setVisibility(View.VISIBLE);
        } else {
            holder.checkedView.setVisibility(View.GONE);
            holder.checkedIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return images == null ? 0 : images.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        ShapeableImageView image, checkedIcon;
        View checkedView;
        ImageClickListener clickListener;
        public MyViewHolder(@NonNull View itemView, ImageClickListener clickListener) {
            super(itemView);
            image = itemView.findViewById(R.id.photo);
            checkedIcon = itemView.findViewById(R.id.checked);
            checkedView = itemView.findViewById(R.id.checkedView);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onImageClick(getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onImageLongClick(getLayoutPosition());
            return true;
        }
    }
}
