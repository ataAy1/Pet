package com.app.kayipetapp.presentation.events_add

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.kayipetapp.databinding.ImageSelectionItemBinding
import com.bumptech.glide.Glide


class EventImageSelectionAdapter() :
    RecyclerView.Adapter<EventImageSelectionAdapter.ImageViewHolder>() {

    var list = mutableListOf<Uri>()


    fun setContentList(photoLists: MutableList<Uri>) {
        this.list = photoLists

        notifyDataSetChanged()
    }

    inner class ImageViewHolder(private val binding: ImageSelectionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photoUri: Uri, position: Int) {
            Glide.with(itemView)
                .load(photoUri)
                .centerCrop()
                .into(binding.i1)

            binding.t1Number.text = (position + 1).toString()
        }
    }




    fun updateItemNumbers() {
        for (position in list.indices) {
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ImageSelectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val photoUri = list[position]
        holder.bind(photoUri, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}