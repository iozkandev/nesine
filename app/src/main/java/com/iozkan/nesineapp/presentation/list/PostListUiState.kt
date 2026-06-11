package com.iozkan.nesineapp.presentation.list

import com.iozkan.nesineapp.domain.model.Post

/**
 * Immutable UI state for the listing screen, exposed via StateFlow. A single
 * state object keeps the View dumb: it just renders whatever it receives.
 */
data class PostListUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val errorMessage: String? = null
)
