package com.iozkan.nesineapp.domain.usecase

import com.iozkan.nesineapp.domain.model.Post
import com.iozkan.nesineapp.domain.repository.PostRepository
import javax.inject.Inject

/** Re-inserts a swipe-deleted post at its previous position (undo). */
class RestorePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(post: Post, index: Int) = repository.restorePost(post, index)
}
