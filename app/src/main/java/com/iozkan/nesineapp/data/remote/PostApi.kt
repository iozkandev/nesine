package com.iozkan.nesineapp.data.remote

import com.iozkan.nesineapp.data.remote.dto.PostDto
import retrofit2.http.GET

/** Retrofit definition for the JSONPlaceholder posts endpoint. */
interface PostApi {

    @GET("posts")
    suspend fun getPosts(): List<PostDto>

    companion object {
        const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    }
}
