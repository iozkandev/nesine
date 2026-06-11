package com.iozkan.nesineapp.presentation.compose.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.iozkan.nesineapp.R
import com.iozkan.nesineapp.domain.model.Post
import com.iozkan.nesineapp.presentation.compose.theme.NesineComposeTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * Stateful entry point: owns the ViewModel, bridges StateFlow -> Compose state
 * with [collectAsStateWithLifecycle], and collects one-shot effects.
 */
@Composable
fun PostListRoute(
    onPostClick: (Int) -> Unit,
    viewModel: PostListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val undoLabel = stringResource(R.string.undo)
    val deletedLabel = stringResource(R.string.post_deleted)

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                PostListEffect.ShowUndoSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = deletedLabel,
                        actionLabel = undoLabel,
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(PostListEvent.UndoDelete)
                    }
                }

                is PostListEffect.ShowError ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    PostListScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onPostClick = onPostClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    state: PostListState,
    snackbarHostState: SnackbarHostState,
    onEvent: (PostListEvent) -> Unit,
    onPostClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onEvent(PostListEvent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.posts.isEmpty() && !state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.empty_posts),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    itemsIndexed(
                        items = state.posts,
                        key = { _, post -> post.id }
                    ) { index, post ->
                        SwipeablePostItem(
                            index = index,
                            post = post,
                            onClick = { onPostClick(post.id) },
                            onDelete = { onEvent(PostListEvent.Delete(post, index)) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeablePostItem(
    index: Int,
    post: Post,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DismissBackground() }
    ) {
        PostItem(index = index, post = post, onClick = onClick)
    }
}

@Composable
private fun PostItem(
    index: Int,
    post: Post,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Random grayscale image keyed by adapter position, as required.
        AsyncImage(
            model = "https://picsum.photos/300/300?random=$index&grayscale",
            contentDescription = stringResource(R.string.post_image_desc),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_placeholder),
            error = painterResource(R.drawable.ic_placeholder),
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            Text(
                text = post.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun DismissBackground() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.onErrorContainer)
        Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.onErrorContainer)
    }
}

// ---------------------------------------------------------------------------
// Previews — enabled by stateless composables + stable, immutable state.
// ---------------------------------------------------------------------------

private class PostListStateProvider : PreviewParameterProvider<PostListState> {
    override val values = sequenceOf(
        PostListState(
            isLoading = false,
            posts = sampledPosts()
        ),
        PostListState(isLoading = false, posts = persistentListOf())
    )
}

private fun sampledPosts(): ImmutableList<Post> = (1..4).map {
    Post(id = it, userId = 1, title = "Sample title $it", body = "Sample body text for post $it")
}.toImmutableList()

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PostListScreenPreview(
    @PreviewParameter(PostListStateProvider::class) state: PostListState
) {
    NesineComposeTheme {
        PostListScreen(
            state = state,
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
            onPostClick = {}
        )
    }
}
