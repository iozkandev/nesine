package com.iozkan.nesineapp.util

import com.iozkan.nesineapp.domain.model.Post
import com.iozkan.nesineapp.domain.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory test double for [PostRepository] that lets tests seed the cache and
 * script the result of [refreshPosts], while keeping real update/delete/restore
 * semantics so ViewModel logic can be exercised end to end.
 */
class FakePostRepository : PostRepository {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    override val posts: StateFlow<List<Post>> = _posts

    /** Result returned by [refreshPosts]; on success it also seeds the cache. */
    var refreshResult: Result<List<Post>> = Result.success(emptyList())
    var refreshCount: Int = 0
        private set

    fun seed(posts: List<Post>) {
        _posts.value = posts
    }

    override suspend fun refreshPosts(): Result<List<Post>> {
        refreshCount++
        refreshResult.onSuccess { _posts.value = it }
        return refreshResult
    }

    override fun getPostById(id: Int): Post? = _posts.value.firstOrNull { it.id == id }

    override fun updatePost(post: Post) {
        _posts.update { current -> current.map { if (it.id == post.id) post else it } }
    }

    override fun deletePost(postId: Int) {
        _posts.update { current -> current.filterNot { it.id == postId } }
    }

    override fun restorePost(post: Post, index: Int) {
        _posts.update { current ->
            if (current.any { it.id == post.id }) current
            else current.toMutableList().apply { add(index.coerceIn(0, size), post) }
        }
    }
}

/** Small factory to keep test bodies terse. */
fun post(id: Int, title: String = "Title $id", body: String = "Body $id"): Post =
    Post(id = id, userId = 1, title = title, body = body)
