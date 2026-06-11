package com.iozkan.nesineapp.domain.usecase

import com.iozkan.nesineapp.domain.model.Post
import com.iozkan.nesineapp.domain.repository.PostRepository
import javax.inject.Inject

/** Returns a single cached post by id (used to pre-fill the detail screen). */
class GetPostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(id: Int): Post? = repository.getPostById(id)
}
