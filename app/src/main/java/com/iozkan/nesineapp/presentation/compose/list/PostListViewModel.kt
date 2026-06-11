package com.iozkan.nesineapp.presentation.compose.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iozkan.nesineapp.domain.model.Post
import com.iozkan.nesineapp.domain.usecase.DeletePostUseCase
import com.iozkan.nesineapp.domain.usecase.ObservePostsUseCase
import com.iozkan.nesineapp.domain.usecase.RefreshPostsUseCase
import com.iozkan.nesineapp.domain.usecase.RestorePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

/**
 * MVI ViewModel for the Compose listing screen. It orchestrates the same domain
 * use cases as the Views-side MVVM `PostListViewModel`.
 *
 * - State    : single [StateFlow] of [PostListState].
 * - Intents  : a single [onEvent] sink taking [PostListEvent].
 * - Messages : transient user messages live in [PostListState.userMessage] and
 *              are cleared via [PostListEvent.MessageShown].
 */
@HiltViewModel
class PostListViewModel @Inject constructor(
    private val observePosts: ObservePostsUseCase,
    private val refreshPosts: RefreshPostsUseCase,
    private val deletePost: DeletePostUseCase,
    private val restorePost: RestorePostUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PostListState(isLoading = true))
    val state: StateFlow<PostListState> = _state.asStateFlow()

    private var lastDeleted: Pair<Post, Int>? = null
    private val messageIds = AtomicLong(0L)

    init {
        observeCache()
        if (observePosts().value.isEmpty()) refresh() else _state.update { it.copy(isLoading = false) }
    }

    fun onEvent(event: PostListEvent) {
        when (event) {
            PostListEvent.Refresh -> refresh()
            is PostListEvent.Delete -> onDelete(event.post, event.index)
            PostListEvent.UndoDelete -> onUndo()
            is PostListEvent.MessageShown -> onMessageShown(event.id)
        }
    }

    /** Reflects the shared single source of truth (updates from the detail screen, deletes). */
    private fun observeCache() {
        viewModelScope.launch {
            observePosts().collect { posts ->
                _state.update { it.copy(posts = posts.toImmutableList()) }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            refreshPosts()
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            userMessage = UserMessage.Error(
                                id = messageIds.incrementAndGet(),
                                text = error.message ?: "Something went wrong"
                            )
                        )
                    }
                }
                .onSuccess { _state.update { it.copy(isLoading = false) } }
        }
    }

    private fun onDelete(post: Post, index: Int) {
        lastDeleted = post to index
        deletePost(post.id)
        _state.update {
            it.copy(userMessage = UserMessage.PostDeleted(id = messageIds.incrementAndGet()))
        }
    }

    private fun onUndo() {
        lastDeleted?.let { (post, index) -> restorePost(post, index) }
        lastDeleted = null
    }

    /** Clears the message only if it is still the one the UI reported showing. */
    private fun onMessageShown(id: Long) {
        _state.update { if (it.userMessage?.id == id) it.copy(userMessage = null) else it }
    }
}
