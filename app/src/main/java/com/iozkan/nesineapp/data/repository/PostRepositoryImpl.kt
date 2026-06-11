package com.iozkan.nesineapp.data.repository

import com.iozkan.nesineapp.data.mapper.toDomain
import com.iozkan.nesineapp.data.remote.PostApi
import com.iozkan.nesineapp.domain.model.Post
import com.iozkan.nesineapp.domain.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for posts.
 *
 * Network responses are written into [_posts]; every screen observes the same
 * flow, so edits from the detail screen and swipe-to-delete on the list mutate
 * one shared cache and propagate everywhere automatically. The repository is a
 * [Singleton] so this cache survives across fragments and configuration changes.
 */
@Singleton
class PostRepositoryImpl @Inject constructor(
    private val api: PostApi
) : PostRepository {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    override val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    override suspend fun refreshPosts(): Result<List<Post>> = runCatching {
        val fetched = api.getPosts().toDomain()
        _posts.value = fetched
        fetched
    }

    override fun getPostById(id: Int): Post? = _posts.value.firstOrNull { it.id == id }

    override fun updatePost(post: Post) {
        _posts.update { current ->
            current.map { if (it.id == post.id) post else it }
        }
    }

    override fun deletePost(postId: Int) {
        _posts.update { current -> current.filterNot { it.id == postId } }
    }

    override fun restorePost(post: Post, index: Int) {
        _posts.update { current ->
            if (current.any { it.id == post.id }) {
                current // already present, nothing to restore
            } else {
                current.toMutableList().apply {
                    add(index.coerceIn(0, size), post)
                }
            }
        }
    }
}
