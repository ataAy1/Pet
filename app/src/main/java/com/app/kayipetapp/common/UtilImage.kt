package com.app.kayipetapp.common

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.kayipetapp.presentation.events_add.EventImageSelectionAdapter
import java.util.Collections

object UtilImage {

    const val PICK_IMAGE_REQUEST = 1001

    fun openImagePicker(launcher: ActivityResultLauncher<Intent>) {
        val pickImageIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickImageIntent.type = "image/*"
        pickImageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        launcher.launch(pickImageIntent)
    }

    fun handleImagePickerResult(
        data: Intent?,
        selectedImageUris: MutableList<Uri>,
        adapter: EventImageSelectionAdapter
    ) {
        data?.clipData?.let { clipData ->
            for (i in 0 until clipData.itemCount) {
                val uri = clipData.getItemAt(i).uri
                selectedImageUris.add(uri)
            }
        } ?: run {
            data?.data?.let { uri ->
                selectedImageUris.add(uri)
            }
        }
        adapter.setContentList(selectedImageUris)
    }

    fun setupRecyclerView(
        recyclerView: RecyclerView,
        selectedImageUris: MutableList<Uri>,
        adapter: EventImageSelectionAdapter
    ) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
            ItemTouchHelper(createItemTouchHelperCallback(selectedImageUris, adapter)).attachToRecyclerView(this)
        }
    }

    private fun createItemTouchHelperCallback(
        selectedImageUris: MutableList<Uri>,
        adapter: EventImageSelectionAdapter
    ): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            ItemTouchHelper.UP or ItemTouchHelper.DOWN
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.absoluteAdapterPosition
                val toPosition = target.absoluteAdapterPosition
                Collections.swap(selectedImageUris, fromPosition, toPosition)
                adapter.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                selectedImageUris.removeAt(position)
                adapter.notifyItemRemoved(position)
                adapter.updateItemNumbers()
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                adapter.updateItemNumbers()
            }
        }
    }
}
