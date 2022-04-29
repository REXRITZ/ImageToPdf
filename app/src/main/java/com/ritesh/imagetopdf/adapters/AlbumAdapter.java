package com.ritesh.imagetopdf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ritesh.imagetopdf.R;
import com.ritesh.imagetopdf.model.Album;

import java.util.List;
import java.util.Locale;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder>{

    private final Context context;
    private List<Album> albumList;
    private final AlbumClickListener clickListener;
    private final int dimen;
    public AlbumAdapter(Context context, List<Album> albumList, AlbumClickListener clickListener) {
        this.context = context;
        this.albumList = albumList;
        this.clickListener = clickListener;
        dimen = (int) context.getResources().getDimension(R.dimen.dimen_16);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_list_item, parent, false);
        return new MyViewHolder(view,clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Album album = albumList.get(position);

        if (position <= 1) {
            holder.albumThumbnail.setPadding(dimen,dimen,dimen,dimen);
            Glide.with(context)
                    .load(position == 0 ? ContextCompat.getDrawable(context,R.drawable.ic_baseline_photo_camera_24) : ContextCompat.getDrawable(context,R.drawable.ic_baseline_manage_search_24))
                    .into(holder.albumThumbnail);
            holder.arrow.setVisibility(View.VISIBLE);
        } else {
            Glide.with(context).load(album.albumThumbnail).into(holder.albumThumbnail);
            holder.itemCount.setText(String.format(Locale.getDefault(),"%d", album.getItemCount()));
        }
        holder.albumName.setText(album.albumName);
    }

    @Override
    public int getItemCount() {
        return albumList == null ? 0 : albumList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView albumThumbnail, arrow;
        TextView albumName, itemCount;
        AlbumClickListener clickListener;

        public MyViewHolder(@NonNull View itemView, AlbumClickListener clickListener) {
            super(itemView);
            albumThumbnail = itemView.findViewById(R.id.albumThumbnail);
            albumName = itemView.findViewById(R.id.albumName);
            itemCount = itemView.findViewById(R.id.itemCount);
            arrow = itemView.findViewById(R.id.arrow);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onAlbumClicked(getLayoutPosition());
        }
    }
}