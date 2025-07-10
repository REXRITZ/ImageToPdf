package com.ritesh.imagetopdf.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ritesh.imagetopdf.databinding.ImageItemBinding

class ImageSelectAdapter(
    private val onClick: (Uri, Int) -> Unit,
    private val onDelete: (Int) -> Unit,
) : ListAdapter<Uri, ImageSelectAdapter.ImageViewHolder>(ImageComparator) {

    inner class ImageViewHolder(private val binding: ImageItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(imageUri: Uri) {
            Glide.with(binding.root.context)
                .load(imageUri)
                .into(binding.photo)

            binding.root.setOnClickListener {
                onClick(imageUri, adapterPosition)
            }
            binding.deleteImage.setOnClickListener {
                onDelete(adapterPosition)
            }
        }
    }

    override fun submitList(list: List<Uri>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    object ImageComparator: DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}