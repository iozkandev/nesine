package com.iozkan.nesineapp.domain.usecase

import com.iozkan.nesineapp.domain.model.Post
import com.iozkan.nesineapp.domain.repository.PostRepository
import javax.inject.Inject

/** Persists an edited post back into the single source of truth. */
class UpdatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(post: Post) = repository.updatePost(post)
}
