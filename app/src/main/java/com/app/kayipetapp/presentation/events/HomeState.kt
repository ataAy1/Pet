package com.app.kayipetapp.presentation.events

import com.app.kayipetapp.domain.models.Event

data class HomeState(
    val isLoading: Boolean = false,
    val data: List<Event>? = null,
    val error: String = ""
)
