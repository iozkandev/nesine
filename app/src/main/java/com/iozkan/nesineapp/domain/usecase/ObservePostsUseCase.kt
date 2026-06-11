package com.iozkan.nesineapp.domain.usecase

import com.iozkan.nesineapp.domain.model.Post
import com.iozkan.nesineapp.domain.repository.PostRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Streams the current list of posts from the single source of truth.
 * `invoke` operator lets call sites use it like a function: `observePosts()`.
 */
class ObservePostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(): StateFlow<List<Post>> = repository.posts
}
