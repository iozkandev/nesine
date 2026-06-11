package com.iozkan.nesineapp.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iozkan.nesineapp.domain.model.Post
import com.iozkan.nesineapp.domain.usecase.DeletePostUseCase
import com.iozkan.nesineapp.domain.usecase.ObservePostsUseCase
import com.iozkan.nesineapp.domain.usecase.RefreshPostsUseCase
import com.iozkan.nesineapp.domain.usecase.RestorePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val observePosts: ObservePostsUseCase,
    private val refreshPosts: RefreshPostsUseCase,
    private val deletePost: DeletePostUseCase,
    private val restorePost: RestorePostUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostListUiState(isLoading = true))
    val uiState: StateFlow<PostListUiState> = _uiState.asStateFlow()

    // Remembers the most recently swiped post + its row index so it can be undone.
    private var lastDeleted: Pair<Post, Int>? = null

    init {
        observeCache()
        // Only hit the network if the shared cache is empty (e.g. first launch).
        if (observePosts().value.isEmpty()) {
            loadPosts()
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /** Keeps the UI in sync with the single source of truth (updates, deletes). */
    private fun observeCache() {
        viewModelScope.launch {
            observePosts().collect { posts ->
                _uiState.update { it.copy(posts = posts) }
            }
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            refreshPosts()
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Something went wrong"
                        )
                    }
                }
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun onDeletePost(post: Post, position: Int) {
        lastDeleted = post to position
        deletePost(post.id)
    }

    fun onUndoDelete() {
        lastDeleted?.let { (post, position) -> restorePost(post, position) }
        lastDeleted = null
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
