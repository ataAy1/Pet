package com.app.kayipetapp.domain.models

import java.io.Serializable

data class DateTime(
    var year: Int? = null,
    var month: Int? = null,
    var day: Int? = null,
    var hour: String? = null,
    var minute: String? = null,
    var dayOfWeek: String? = null
) : Serializable