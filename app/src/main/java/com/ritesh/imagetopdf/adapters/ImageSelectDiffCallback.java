package com.ritesh.imagetopdf.adapters;

import androidx.recyclerview.widget.DiffUtil;

import com.ritesh.imagetopdf.model.ImageItem;

import java.util.List;

public class ImageSelectDiffCallback extends DiffUtil.Callback {
    List<ImageItem> oldList, newList;

    public ImageSelectDiffCallback(List<ImageItem> oldList, List<ImageItem> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        if (oldList == null) {
            return 0;
        }
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        if (newList == null) {
            return 0;
        }
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).imageUri == newList.get(newItemPosition).imageUri
                && oldList.get(oldItemPosition).isSelected == newList.get(newItemPosition).isSelected;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
