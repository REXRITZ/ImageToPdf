package com.ritesh.imagetopdf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.ritesh.imagetopdf.R;
import com.ritesh.imagetopdf.db.PdfDetail;
import com.ritesh.imagetopdf.utils.Utils;
import com.ritesh.imagetopdf.viewmodel.PdfViewModel;

import java.io.File;
import java.util.ArrayList;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.MyViewHolder>{

    private final Context context;
    private ArrayList<PdfDetail> allPdfs;
    private final PdfItemClickListener clickListener;
    public PdfAdapter(Context context, PdfItemClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    public void updateList(ArrayList<PdfDetail> allPdfs) {
        final DiffCallback diffCallback = new DiffCallback(this.allPdfs, allPdfs);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.allPdfs = allPdfs;
        diffResult.dispatchUpdatesTo(this);
    }

    public void deleteFile(int position) {
        PdfViewModel.delete(allPdfs.get(position));
        allPdfs.remove(position);
        notifyItemRemoved(position);
    }

    public void renameFile(int position, String fileName) {
        File file = new File(allPdfs.get(position).getFilePath());
        allPdfs.get(position).setFileName(fileName);
        allPdfs.get(position).setFilePath(Utils.PATH + File.separator + fileName);
        File newFile = new File(Utils.PATH, fileName);
        file.renameTo(newFile);
        PdfViewModel.update(allPdfs.get(position));
        notifyItemChanged(position);
    }

    public PdfDetail getItem(int position) {
        return allPdfs.get(position);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_item, parent, false);
        return new MyViewHolder(view,clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PdfDetail pdf = allPdfs.get(position);

        if(pdf.isEncrypted()) {
            holder.lock.setVisibility(View.VISIBLE);
        }
        holder.fileName.setText(pdf.getFileName());
        holder.fileSize.setText(Utils.convertFileSize(pdf.getFileSize()));
        holder.date.setText(Utils.getFormattedDate(pdf.getDateCreated()));
    }

    @Override
    public int getItemCount() {
        if(allPdfs == null) {
            return 0;
        }
        return allPdfs.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView fileName;
        final TextView fileSize;
        final TextView date;
        final ShapeableImageView options;
        final ShapeableImageView lock;
        final PdfItemClickListener clickListener;


        public MyViewHolder(@NonNull View itemView, PdfItemClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;
            fileName = itemView.findViewById(R.id.file_name);
            fileSize = itemView.findViewById(R.id.file_size);
            date = itemView.findViewById(R.id.date);
            options = itemView.findViewById(R.id.more_options);
            lock = itemView.findViewById(R.id.lockImageView);
            itemView.setOnClickListener(this);
            options.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(view, getAbsoluteAdapterPosition());
        }
    }
}
