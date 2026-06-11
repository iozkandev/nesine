package com.iozkan.nesineapp.presentation.compose.list

import androidx.compose.runtime.Immutable
import com.iozkan.nesineapp.domain.model.Post
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * MVI state for the Compose listing screen.
 *
 * Marked [@Immutable] and backed by [ImmutableList] so the Compose compiler can
 * prove stability — composables that take this state become skippable and avoid
 * needless recomposition when unrelated state changes.
 */
@Immutable
data class PostListState(
    val isLoading: Boolean = false,
    val posts: ImmutableList<Post> = persistentListOf()
)

/** Intents: the only way the UI can ask the ViewModel to do something. */
sealed interface PostListEvent {
    data object Refresh : PostListEvent
    data class Delete(val post: Post, val index: Int) : PostListEvent
    data object UndoDelete : PostListEvent
}

/**
 * One-shot side effects. Kept OUT of [PostListState] so they fire exactly once
 * and never re-trigger on recomposition or configuration change.
 */
sealed interface PostListEffect {
    data object ShowUndoSnackbar : PostListEffect
    data class ShowError(val message: String) : PostListEffect
}
