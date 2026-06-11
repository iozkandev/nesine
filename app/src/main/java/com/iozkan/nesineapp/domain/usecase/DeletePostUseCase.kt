package com.iozkan.nesineapp.domain.usecase

import com.iozkan.nesineapp.domain.repository.PostRepository
import javax.inject.Inject

/** Removes a post from the single source of truth (swipe-to-delete). */
class DeletePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(postId: Int) = repository.deletePost(postId)
}
