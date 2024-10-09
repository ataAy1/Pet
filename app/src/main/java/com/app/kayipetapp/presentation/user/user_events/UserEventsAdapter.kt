package com.app.kayipetapp.presentation.user.user_events

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.kayipetapp.R
import com.app.kayipetapp.databinding.EventItemBinding
import com.app.kayipetapp.domain.models.Event
import com.bumptech.glide.Glide
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class UserEventsAdapter(private val context: Context) :
    RecyclerView.Adapter<UserEventsAdapter.EventViewHolder>() {

    private var events = mutableListOf<Event>()
    private val imageGalleryList = mutableListOf<CarouselItem>()
    private var deleteListener: EventDeleteListener? = null

    fun setEvents(newEvents: List<Event>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }

    fun setEventDeleteListener(listener: EventDeleteListener) {
        deleteListener = listener
    }

    interface EventDeleteListener {
        fun onDeleteEvent(eventID: String)
    }

    inner class EventViewHolder(private val binding: EventItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.apply {
                Log.d("UserEventsAdapter", "Binding event: $event")
                textviewDescription.text = event.description ?: context.getString(R.string.no_description)

                setupDeleteEventButton(event)
                setupImageGallery(event.images)
            }
        }

        private fun setupImageGallery(images: Map<String, String>?) {
            imageGalleryList.clear()
            images?.let { imgs ->
                for (i in imgs.keys) {
                    imageGalleryList.add(CarouselItem(imageUrl = imgs[i] ?: ""))
                }
            }
            binding.imageGallery.setData(imageGalleryList)
        }

        private fun setupDeleteEventButton(event: Event) {
            binding.deleteEvent.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle(R.string.delete_confirmation_title)
                    .setMessage(R.string.delete_confirmation_message)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        deleteListener?.onDeleteEvent(event.eventID)
                    }
                    .setNegativeButton(R.string.no, null)
                    .show()
            }
        }
    }

    fun removeEvent(eventID: String) {
        val position = events.indexOfFirst { it.eventID == eventID }
        if (position != -1) {
            events.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = EventItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }
}
