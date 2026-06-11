package com.iozkan.nesineapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Network representation of a post as returned by
 * https://jsonplaceholder.typicode.com/posts
 */
data class PostDto(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String
)
