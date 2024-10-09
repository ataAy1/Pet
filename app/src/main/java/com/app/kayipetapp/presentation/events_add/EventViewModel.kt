package com.app.kayipetapp.presentation.events_add

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kayipetapp.domain.models.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class EventViewModel : ViewModel() {
    private val mutableSelectedEvent = MutableLiveData<Event>()
    val selectedEvent: LiveData<Event> get() = mutableSelectedEvent

    fun selectEvent(event: Event) {
        mutableSelectedEvent.value = event
    }


}