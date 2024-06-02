package com.polije.storyapps.model

data class LoginModel(
    val email: String,
    val password: String
)

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult?
)

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)