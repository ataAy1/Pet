package com.app.kayipetapp.domain.models

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
import java.util.UUID

data class Event(
    var eventID: String = UUID.randomUUID().toString(),
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var userName: String = "",
    var adress: String = "",
    var eventType: String = "",
    var dateTime: DateTime? = null,
    var images: HashMap<String, String>? = null,
    var description: String? = null
) : Serializable
