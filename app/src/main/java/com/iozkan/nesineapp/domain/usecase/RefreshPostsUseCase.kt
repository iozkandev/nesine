package com.iozkan.nesineapp.domain.usecase

import com.iozkan.nesineapp.domain.model.Post
import com.iozkan.nesineapp.domain.repository.PostRepository
import javax.inject.Inject

/** Triggers a network refresh of the post cache. */
class RefreshPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(): Result<List<Post>> = repository.refreshPosts()
}
