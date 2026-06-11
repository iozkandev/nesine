package com.iozkan.nesineapp.data.repository

import com.iozkan.nesineapp.data.remote.PostApi
import com.iozkan.nesineapp.data.remote.dto.PostDto
import com.iozkan.nesineapp.domain.model.Post
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class PostRepositoryImplTest {

    /** Scriptable [PostApi]: returns [response] or throws [error] when set. */
    private class FakePostApi(
        var response: List<PostDto> = emptyList(),
        var error: Throwable? = null
    ) : PostApi {
        override suspend fun getPosts(): List<PostDto> = error?.let { throw it } ?: response
    }

    private fun dto(id: Int) = PostDto(id = id, userId = 1, title = "t$id", body = "b$id")

    @Test
    fun `refreshPosts on success maps DTOs to domain and populates the cache`() = runTest {
        val api = FakePostApi(response = listOf(dto(1), dto(2)))
        val repo = PostRepositoryImpl(api)

        val result = repo.refreshPosts()

        assertTrue(result.isSuccess)
        assertEquals(listOf(1, 2), repo.posts.value.map { it.id })
        assertEquals("t1", repo.posts.value.first().title)
    }

    @Test
    fun `refreshPosts on failure returns failure and leaves the cache untouched`() = runTest {
        val api = FakePostApi(error = IOException("offline"))
        val repo = PostRepositoryImpl(api)

        val result = repo.refreshPosts()

        assertTrue(result.isFailure)
        assertTrue(repo.posts.value.isEmpty())
    }

    @Test
    fun `getPostById returns the matching post or null`() = runTest {
        val api = FakePostApi(response = listOf(dto(1), dto(2)))
        val repo = PostRepositoryImpl(api).also { it.refreshPosts() }

        assertEquals(2, repo.getPostById(2)?.id)
        assertNull(repo.getPostById(99))
    }

    @Test
    fun `updatePost replaces only the matching post`() = runTest {
        val api = FakePostApi(response = listOf(dto(1), dto(2)))
        val repo = PostRepositoryImpl(api).also { it.refreshPosts() }

        repo.updatePost(Post(id = 1, userId = 1, title = "edited", body = "edited body"))

        assertEquals("edited", repo.getPostById(1)?.title)
        assertEquals("t2", repo.getPostById(2)?.title)
    }

    @Test
    fun `deletePost removes the post with the given id`() = runTest {
        val api = FakePostApi(response = listOf(dto(1), dto(2)))
        val repo = PostRepositoryImpl(api).also { it.refreshPosts() }

        repo.deletePost(1)

        assertEquals(listOf(2), repo.posts.value.map { it.id })
    }

    @Test
    fun `restorePost re-inserts at the given index`() = runTest {
        val api = FakePostApi(response = listOf(dto(1), dto(2), dto(3)))
        val repo = PostRepositoryImpl(api).also { it.refreshPosts() }
        val removed = repo.getPostById(2)!!
        repo.deletePost(2)

        repo.restorePost(removed, index = 1)

        assertEquals(listOf(1, 2, 3), repo.posts.value.map { it.id })
    }

    @Test
    fun `restorePost coerces an out-of-range index to the end`() = runTest {
        val api = FakePostApi(response = listOf(dto(1)))
        val repo = PostRepositoryImpl(api).also { it.refreshPosts() }

        repo.restorePost(Post(id = 5, userId = 1, title = "x", body = "y"), index = 99)

        assertEquals(listOf(1, 5), repo.posts.value.map { it.id })
    }

    @Test
    fun `restorePost is a no-op when the post is already present`() = runTest {
        val api = FakePostApi(response = listOf(dto(1)))
        val repo = PostRepositoryImpl(api).also { it.refreshPosts() }

        repo.restorePost(repo.getPostById(1)!!, index = 0)

        assertEquals(1, repo.posts.value.size)
        assertFalse(repo.posts.value.size > 1)
    }
}
