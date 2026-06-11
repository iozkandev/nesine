package com.iozkan.nesineapp.domain.model

/**
 * Clean domain model used across the presentation layer. It is intentionally
 * decoupled from the network DTO so the UI never depends on transport details.
 */
data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)
