package com.iozkan.nesineapp.presentation.compose.detail

import androidx.lifecycle.SavedStateHandle
import com.iozkan.nesineapp.domain.usecase.GetPostUseCase
import com.iozkan.nesineapp.domain.usecase.UpdatePostUseCase
import com.iozkan.nesineapp.util.FakePostRepository
import com.iozkan.nesineapp.util.post
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Runs under Robolectric because the ViewModel reads its argument via type-safe
 * navigation (`SavedStateHandle.toRoute`), which needs a real android Bundle.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PostDetailViewModelTest {

    private val repo = FakePostRepository()

    private fun viewModel(postId: Int) = PostDetailViewModel(
        getPost = GetPostUseCase(repo),
        updatePost = UpdatePostUseCase(repo),
        savedStateHandle = SavedStateHandle(mapOf("postId" to postId))
    )

    @Test
    fun `init loads the post identified by the route argument`() {
        repo.seed(listOf(post(1), post(7, title = "lucky", body = "seven")))

        val vm = viewModel(postId = 7)

        assertEquals("lucky", vm.state.value.title)
        assertEquals("seven", vm.state.value.body)
        assertTrue(vm.state.value.isLoaded)
    }

    @Test
    fun `saving valid edits updates the repository and flags isSaved`() {
        repo.seed(listOf(post(7)))
        val vm = viewModel(postId = 7)

        vm.onEvent(PostDetailEvent.TitleChanged("new title"))
        vm.onEvent(PostDetailEvent.BodyChanged("new body"))
        vm.onEvent(PostDetailEvent.Save)

        assertTrue(vm.state.value.isSaved)
        assertEquals("new title", repo.getPostById(7)?.title)
        assertEquals("new body", repo.getPostById(7)?.body)
    }

    @Test
    fun `saving a blank title is rejected and does not touch the repository`() {
        repo.seed(listOf(post(7, title = "original")))
        val vm = viewModel(postId = 7)

        vm.onEvent(PostDetailEvent.TitleChanged("   "))
        vm.onEvent(PostDetailEvent.Save)

        assertTrue(vm.state.value.titleError)
        assertFalse(vm.state.value.isSaved)
        assertEquals("original", repo.getPostById(7)?.title)
    }

    @Test
    fun `editing a field clears its error`() {
        repo.seed(listOf(post(7)))
        val vm = viewModel(postId = 7)
        vm.onEvent(PostDetailEvent.TitleChanged(""))
        vm.onEvent(PostDetailEvent.Save) // raises titleError

        vm.onEvent(PostDetailEvent.TitleChanged("fixed"))

        assertFalse(vm.state.value.titleError)
    }
}
