package com.iozkan.nesineapp.presentation.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.iozkan.nesineapp.presentation.compose.navigation.PostsNavHost
import com.iozkan.nesineapp.presentation.compose.theme.NesineComposeTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Bridge between the Views world (Navigation Component) and the Compose world.
 *
 * It is a normal fragment destination whose entire content is a [ComposeView]
 * hosting a Compose-native [PostsNavHost]. This is the classic interop pattern
 * for incrementally adopting Compose: Compose grows inside a fragment shell
 * until enough of the app is migrated to drop fragments entirely.
 */
@AndroidEntryPoint
class ComposeHostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        // Dispose the composition with the fragment's view lifecycle.
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            NesineComposeTheme {
                PostsNavHost()
            }
        }
    }
}
