package com.iozkan.nesineapp.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.iozkan.nesineapp.domain.model.Post
import com.iozkan.nesineapp.domain.usecase.GetPostUseCase
import com.iozkan.nesineapp.domain.usecase.UpdatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val getPost: GetPostUseCase,
    private val updatePost: UpdatePostUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // "postId" matches the <argument> name defined in nav_graph.xml (Safe Args).
    private val postId: Int = savedStateHandle["postId"] ?: -1

    private val _post = MutableStateFlow(getPost(postId))
    val post: StateFlow<Post?> = _post.asStateFlow()

    /**
     * Applies the user's edits to the single source of truth. Returns true when
     * the input is valid and the update was performed.
     */
    fun saveChanges(title: String, body: String): Boolean {
        val current = _post.value ?: return false
        if (title.isBlank() || body.isBlank()) return false

        val updated = current.copy(title = title.trim(), body = body.trim())
        updatePost(updated)
        _post.value = updated
        return true
    }
}
