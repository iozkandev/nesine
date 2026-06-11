package com.iozkan.nesineapp.presentation.compose.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iozkan.nesineapp.R
import com.iozkan.nesineapp.presentation.compose.theme.NesineComposeTheme

@Composable
fun PostDetailRoute(
    onBack: () -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Leave the screen once the save succeeds.
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onBack()
    }

    PostDetailScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    state: PostDetailState,
    onEvent: (PostDetailEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cancel)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.End
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = { onEvent(PostDetailEvent.TitleChanged(it)) },
                label = { Text(stringResource(R.string.hint_title)) },
                isError = state.titleError,
                supportingText = errorTextOrNull(state.titleError),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.body,
                onValueChange = { onEvent(PostDetailEvent.BodyChanged(it)) },
                label = { Text(stringResource(R.string.hint_body)) },
                isError = state.bodyError,
                supportingText = errorTextOrNull(state.bodyError),
                minLines = 4,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            Button(
                onClick = { onEvent(PostDetailEvent.Save) },
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

private fun errorTextOrNull(isError: Boolean): (@Composable () -> Unit)? =
    if (isError) {
        { Text(stringResource(R.string.error_required)) }
    } else {
        null
    }

@Preview(showBackground = true)
@Composable
private fun PostDetailScreenPreview() {
    NesineComposeTheme {
        PostDetailScreen(
            state = PostDetailState(
                title = "sunt aut facere",
                body = "quia et suscipit suscipit recusandae",
                isLoaded = true
            ),
            onEvent = {},
            onBack = {}
        )
    }
}
