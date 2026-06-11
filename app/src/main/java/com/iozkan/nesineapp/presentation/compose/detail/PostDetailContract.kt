package com.iozkan.nesineapp.presentation.compose.detail

import androidx.compose.runtime.Immutable

@Immutable
data class PostDetailState(
    val title: String = "",
    val body: String = "",
    val titleError: Boolean = false,
    val bodyError: Boolean = false,
    val isLoaded: Boolean = false
)

sealed interface PostDetailEvent {
    data class TitleChanged(val value: String) : PostDetailEvent
    data class BodyChanged(val value: String) : PostDetailEvent
    data object Save : PostDetailEvent
}

sealed interface PostDetailEffect {
    /** Editing finished successfully; pop back to the list. */
    data object NavigateBack : PostDetailEffect
}
