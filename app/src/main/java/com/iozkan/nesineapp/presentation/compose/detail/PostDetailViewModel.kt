package com.iozkan.nesineapp.presentation.compose.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.iozkan.nesineapp.domain.usecase.GetPostUseCase
import com.iozkan.nesineapp.domain.usecase.UpdatePostUseCase
import com.iozkan.nesineapp.presentation.compose.navigation.DetailDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val getPost: GetPostUseCase,
    private val updatePost: UpdatePostUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Type-safe argument extraction from the PostDetailRoute(postId = ...).
    private val postId: Int = savedStateHandle.toRoute<DetailDestination>().postId

    private val _state = MutableStateFlow(PostDetailState())
    val state: StateFlow<PostDetailState> = _state.asStateFlow()

    private val _effects = Channel<PostDetailEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        getPost(postId)?.let { post ->
            _state.update {
                it.copy(title = post.title, body = post.body, isLoaded = true)
            }
        }
    }

    fun onEvent(event: PostDetailEvent) {
        when (event) {
            is PostDetailEvent.TitleChanged ->
                _state.update { it.copy(title = event.value, titleError = false) }

            is PostDetailEvent.BodyChanged ->
                _state.update { it.copy(body = event.value, bodyError = false) }

            PostDetailEvent.Save -> save()
        }
    }

    private fun save() {
        val current = _state.value
        val titleBlank = current.title.isBlank()
        val bodyBlank = current.body.isBlank()
        if (titleBlank || bodyBlank) {
            _state.update { it.copy(titleError = titleBlank, bodyError = bodyBlank) }
            return
        }

        val original = getPost(postId) ?: return
        updatePost(original.copy(title = current.title.trim(), body = current.body.trim()))
        viewModelScope.launch { _effects.send(PostDetailEffect.NavigateBack) }
    }
}
