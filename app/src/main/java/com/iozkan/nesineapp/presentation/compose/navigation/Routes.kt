package com.iozkan.nesineapp.presentation.compose.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes (Navigation-Compose 2.8+). Each destination is a
 * @Serializable object/class, so arguments are passed and read without manual
 * string routes or bundle keys.
 */
@Serializable
data object ListDestination

@Serializable
data class DetailDestination(val postId: Int)
