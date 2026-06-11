package com.iozkan.nesineapp.presentation.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iozkan.nesineapp.presentation.compose.detail.PostDetailRoute
import com.iozkan.nesineapp.presentation.compose.list.PostListRoute

/**
 * Self-contained Navigation-Compose graph for the Compose flow, using type-safe
 * routes. This whole NavHost lives inside a single fragment destination of the
 * app's Navigation Component graph — a clean boundary that lets the Views and
 * Compose worlds coexist during an incremental migration.
 */
@Composable
fun PostsNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ListDestination
    ) {
        composable<ListDestination> {
            PostListRoute(
                onPostClick = { postId -> navController.navigate(DetailDestination(postId)) }
            )
        }
        composable<DetailDestination> {
            PostDetailRoute(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
