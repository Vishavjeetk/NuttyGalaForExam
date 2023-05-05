package com.example.nuttygala.models

data class UserModel(
    val email: String? = null,
    val name: String? = null,
    val itemsInCart: List<String>? = null
)
