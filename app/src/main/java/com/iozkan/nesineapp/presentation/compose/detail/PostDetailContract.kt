package com.iozkan.nesineapp.presentation.compose.detail

import androidx.compose.runtime.Immutable

/**
 * MVI state for the Compose detail/edit screen. [isSaved] becomes true after a
 * successful save; the screen reacts to it by navigating back.
 */
@Immutable
data class PostDetailState(
    val title: String = "",
    val body: String = "",
    val titleError: Boolean = false,
    val bodyError: Boolean = false,
    val isLoaded: Boolean = false,
    val isSaved: Boolean = false
)

sealed interface PostDetailEvent {
    data class TitleChanged(val value: String) : PostDetailEvent
    data class BodyChanged(val value: String) : PostDetailEvent
    data object Save : PostDetailEvent
}
