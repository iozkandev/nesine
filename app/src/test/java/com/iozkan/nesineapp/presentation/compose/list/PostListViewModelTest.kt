package com.iozkan.nesineapp.presentation.compose.list

import com.iozkan.nesineapp.domain.usecase.DeletePostUseCase
import com.iozkan.nesineapp.domain.usecase.ObservePostsUseCase
import com.iozkan.nesineapp.domain.usecase.RefreshPostsUseCase
import com.iozkan.nesineapp.domain.usecase.RestorePostUseCase
import com.iozkan.nesineapp.util.FakePostRepository
import com.iozkan.nesineapp.util.MainDispatcherRule
import com.iozkan.nesineapp.util.post
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = FakePostRepository()

    private fun viewModel() = PostListViewModel(
        observePosts = ObservePostsUseCase(repo),
        refreshPosts = RefreshPostsUseCase(repo),
        deletePost = DeletePostUseCase(repo),
        restorePost = RestorePostUseCase(repo)
    )

    @Test
    fun `when cache is empty on init it refreshes from the network`() = runTest {
        repo.refreshResult = Result.success(listOf(post(1), post(2)))

        val vm = viewModel()

        assertEquals(1, repo.refreshCount)
        assertEquals(listOf(1, 2), vm.state.value.posts.map { it.id })
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `when cache is already populated on init it does not refresh`() = runTest {
        repo.seed(listOf(post(1)))

        val vm = viewModel()

        assertEquals(0, repo.refreshCount)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `refresh failure surfaces an error message`() = runTest {
        repo.refreshResult = Result.failure(RuntimeException("boom"))

        val vm = viewModel()
        val message = vm.state.value.userMessage

        assertTrue(message is UserMessage.Error)
        assertEquals("boom", (message as UserMessage.Error).text)
    }

    @Test
    fun `delete removes the post and emits a PostDeleted message`() = runTest {
        repo.seed(listOf(post(1), post(2)))
        val vm = viewModel()

        vm.onEvent(PostListEvent.Delete(post(1), index = 0))

        assertEquals(listOf(2), vm.state.value.posts.map { it.id })
        assertTrue(vm.state.value.userMessage is UserMessage.PostDeleted)
    }

    @Test
    fun `undo restores the most recently deleted post at its index`() = runTest {
        repo.seed(listOf(post(1), post(2), post(3)))
        val vm = viewModel()
        vm.onEvent(PostListEvent.Delete(post(2), index = 1))

        vm.onEvent(PostListEvent.UndoDelete)

        assertEquals(listOf(1, 2, 3), vm.state.value.posts.map { it.id })
    }

    @Test
    fun `messageShown clears the message only when the id matches`() = runTest {
        repo.seed(listOf(post(1)))
        val vm = viewModel()
        vm.onEvent(PostListEvent.Delete(post(1), index = 0))
        val shownId = vm.state.value.userMessage!!.id

        vm.onEvent(PostListEvent.MessageShown(id = shownId + 1)) // stale ack
        assertEquals(shownId, vm.state.value.userMessage?.id)

        vm.onEvent(PostListEvent.MessageShown(id = shownId)) // current ack
        assertNull(vm.state.value.userMessage)
    }

    @Test
    fun `each message gets a unique id so repeats re-trigger`() = runTest {
        repo.seed(listOf(post(1), post(2)))
        val vm = viewModel()

        vm.onEvent(PostListEvent.Delete(post(1), index = 0))
        val firstId = vm.state.value.userMessage!!.id
        vm.onEvent(PostListEvent.Delete(post(2), index = 0))
        val secondId = vm.state.value.userMessage!!.id

        assertNotEquals(firstId, secondId)
    }
}
