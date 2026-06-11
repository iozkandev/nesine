package com.iozkan.nesineapp.presentation.detail

import androidx.lifecycle.SavedStateHandle
import com.iozkan.nesineapp.domain.usecase.GetPostUseCase
import com.iozkan.nesineapp.domain.usecase.UpdatePostUseCase
import com.iozkan.nesineapp.util.FakePostRepository
import com.iozkan.nesineapp.util.post
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PostDetailViewModelTest {

    private val repo = FakePostRepository()

    private fun viewModel(postId: Int) = PostDetailViewModel(
        getPost = GetPostUseCase(repo),
        updatePost = UpdatePostUseCase(repo),
        savedStateHandle = SavedStateHandle(mapOf("postId" to postId))
    )

    @Test
    fun `init exposes the post identified by the savedStateHandle argument`() {
        repo.seed(listOf(post(3, title = "hello", body = "world")))

        val vm = viewModel(postId = 3)

        assertEquals("hello", vm.post.value?.title)
        assertEquals("world", vm.post.value?.body)
    }

    @Test
    fun `saveChanges with valid input returns true and updates the repository`() {
        repo.seed(listOf(post(3)))
        val vm = viewModel(postId = 3)

        val saved = vm.saveChanges(title = "  edited  ", body = "  edited body  ")

        assertTrue(saved)
        assertEquals("edited", repo.getPostById(3)?.title)        // trimmed
        assertEquals("edited body", repo.getPostById(3)?.body)
    }

    @Test
    fun `saveChanges with blank input returns false and leaves the repository unchanged`() {
        repo.seed(listOf(post(3, title = "original")))
        val vm = viewModel(postId = 3)

        val saved = vm.saveChanges(title = "", body = "whatever")

        assertFalse(saved)
        assertEquals("original", repo.getPostById(3)?.title)
    }
}
