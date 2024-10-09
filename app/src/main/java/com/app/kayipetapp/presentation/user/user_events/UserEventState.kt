package com.app.kayipetapp.presentation.user.user_events

import com.app.kayipetapp.domain.models.Event

data class UserEventState(
    val isLoading: Boolean = false,
    val data: List<Event>? = null,
    val error: String = "",
    val success: String = ""
)
