package com.ritesh.imagetopdf.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ritesh.imagetopdf.data.PdfEntity
import com.ritesh.imagetopdf.databinding.PdfItemBinding

class PdfAdapter(
    private val onItemClick: (String) -> Unit,
    private val onOptionsClick: (Long) -> Unit
) : ListAdapter<PdfEntity,PdfAdapter.PdfViewHolder>(PdfItemComparator){

    inner class PdfViewHolder(private val binding: PdfItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(pdfEntity: PdfEntity) {
            binding.run {
                if(pdfEntity.isEncrypted) {
                    lockImageView.visibility = View.VISIBLE
                }
                fileName.text = pdfEntity.fileName
                fileSize.text = pdfEntity.fileSize
                date.text = pdfEntity.dateCreated

                root.setOnClickListener {
                    onItemClick(pdfEntity.filePath)
                }
                moreOptions.setOnClickListener {
                    onOptionsClick(pdfEntity.id!!)
                }
            }
        }
    }

    object PdfItemComparator: DiffUtil.ItemCallback<PdfEntity>() {
        override fun areItemsTheSame(oldItem: PdfEntity, newItem: PdfEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PdfEntity, newItem: PdfEntity): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val binding = PdfItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PdfViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}