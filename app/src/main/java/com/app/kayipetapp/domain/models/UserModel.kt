package com.app.kayipetapp.domain.models

data class UserModel(
    val userId: String = "",
    val email: String = "",
    val password: String? = null,
    val userNick: String? = null
) {
    constructor() : this("", "", null, null)
}