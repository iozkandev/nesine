package com.iozkan.nesineapp.presentation.list

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
import org.junit.Assert.assertNull
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
    fun `empty cache triggers a refresh on init`() = runTest {
        repo.refreshResult = Result.success(listOf(post(1), post(2)))

        val vm = viewModel()

        assertEquals(1, repo.refreshCount)
        assertEquals(listOf(1, 2), vm.uiState.value.posts.map { it.id })
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `populated cache skips the refresh on init`() = runTest {
        repo.seed(listOf(post(1)))

        val vm = viewModel()

        assertEquals(0, repo.refreshCount)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `refresh failure exposes an error message that can be cleared`() = runTest {
        repo.refreshResult = Result.failure(RuntimeException("network down"))

        val vm = viewModel()
        assertEquals("network down", vm.uiState.value.errorMessage)

        vm.onErrorShown()
        assertNull(vm.uiState.value.errorMessage)
    }

    @Test
    fun `delete then undo restores the list`() = runTest {
        repo.seed(listOf(post(1), post(2), post(3)))
        val vm = viewModel()

        vm.onDeletePost(post(2), position = 1)
        assertEquals(listOf(1, 3), vm.uiState.value.posts.map { it.id })

        vm.onUndoDelete()
        assertEquals(listOf(1, 2, 3), vm.uiState.value.posts.map { it.id })
    }
}
