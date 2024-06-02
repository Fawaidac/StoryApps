package com.polije.storyapps.model

data class RegisterModel(
    val name: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val error: Boolean,
    val message: String,
)