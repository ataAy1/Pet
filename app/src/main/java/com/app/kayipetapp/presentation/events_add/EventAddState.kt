package com.app.kayipetapp.presentation.events_add

import com.app.kayipetapp.domain.models.Event

data class EventAddState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = ""
)
