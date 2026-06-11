package com.iozkan.nesineapp.presentation.compose.list

import androidx.compose.runtime.Immutable
import com.iozkan.nesineapp.domain.model.Post
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * MVI state for the Compose listing screen.
 *
 * Marked [@Immutable] and backed by [ImmutableList] so the Compose compiler can
 * prove stability — composables that take this state become skippable.
 */
@Immutable
data class PostListState(
    val isLoading: Boolean = false,
    val posts: ImmutableList<Post> = persistentListOf(),
    val userMessage: UserMessage? = null
)

/**
 * A transient message for the user. The [id] is unique per occurrence so that
 * two identical messages in a row still re-trigger the snackbar (and so the
 * consumption ack can target the exact message it was shown for).
 */
@Immutable
sealed interface UserMessage {
    val id: Long

    /** A post was swipe-deleted; shown with an Undo action. */
    data class PostDeleted(override val id: Long) : UserMessage

    /** A refresh failed; shown as a plain error snackbar. */
    data class Error(override val id: Long, val text: String) : UserMessage
}

/** Intents: the only way the UI can ask the ViewModel to do something. */
sealed interface PostListEvent {
    data object Refresh : PostListEvent
    data class Delete(val post: Post, val index: Int) : PostListEvent
    data object UndoDelete : PostListEvent

    /** The UI finished showing [PostListState.userMessage] with this id. */
    data class MessageShown(val id: Long) : PostListEvent
}
