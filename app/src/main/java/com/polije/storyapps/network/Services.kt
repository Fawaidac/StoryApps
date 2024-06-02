package com.polije.storyapps.network

import com.polije.storyapps.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Service {

    @POST("login")
    fun login(@Body request: LoginModel): Call<LoginResponse>

    @POST("register")
    fun register(@Body request: RegisterModel): Call<RegisterResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
    ): Call<StoryResponse>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
    ): Call<GetStoriesResponse>

    @GET("stories")
    fun getAllStoriesLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): Call<GetStoriesResponse>

    @GET("stories/{id}")
    fun getStoryDetail(
        @Header("Authorization") token: String,
        @Path("id") storyId: String
    ): Call<StoryDetailResponse>

}