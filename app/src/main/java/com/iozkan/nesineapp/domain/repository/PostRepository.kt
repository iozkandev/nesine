package com.iozkan.nesineapp.domain.repository

import com.iozkan.nesineapp.domain.model.Post
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository contract owned by the domain layer. The implementation lives in the
 * data layer (dependency inversion).
 *
 * The repository is the single source of truth: it exposes an observable
 * [StateFlow] of posts so every screen (list & detail) reacts to the same data.
 * Because JSONPlaceholder is a mock backend that does not actually persist
 * mutations, update/delete operate on this in-memory cache, which is exactly
 * what makes edits on the detail screen reflect instantly on the list.
 */
interface PostRepository {

    /** Observable cache of posts. Collectors are notified on every mutation. */
    val posts: StateFlow<List<Post>>

    /** Fetches posts from the network and replaces the cache. */
    suspend fun refreshPosts(): Result<List<Post>>

    /** Returns a single cached post by id, or null if not present. */
    fun getPostById(id: Int): Post?

    /** Updates a post in the cache (used by the detail/update screen). */
    fun updatePost(post: Post)

    /** Removes a post from the cache (used by swipe-to-delete). */
    fun deletePost(postId: Int)

    /** Re-inserts a previously deleted post at [index] (used by undo). */
    fun restorePost(post: Post, index: Int)
}
