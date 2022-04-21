package com.ritesh.imagetopdf.adapters;

import androidx.recyclerview.widget.DiffUtil;

import com.ritesh.imagetopdf.db.PdfDetail;

import java.util.ArrayList;

public class DiffCallback extends DiffUtil.Callback {

    private final ArrayList<PdfDetail> oldList, newList;

    public DiffCallback(ArrayList<PdfDetail> oldList, ArrayList<PdfDetail> newList) {
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
        return oldList.get(oldItemPosition).id == newList.get(newItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

}
