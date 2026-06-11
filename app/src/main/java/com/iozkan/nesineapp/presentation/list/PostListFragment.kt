package com.iozkan.nesineapp.presentation.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.iozkan.nesineapp.R
import com.iozkan.nesineapp.databinding.FragmentPostListBinding
import com.iozkan.nesineapp.domain.model.Post
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostListFragment : Fragment(R.layout.fragment_post_list) {

    // ViewBinding scoped to the view lifecycle to avoid leaking the binding.
    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostListViewModel by viewModels()

    private val adapter = PostAdapter(onItemClick = ::openDetail)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostListBinding.bind(view)

        setupRecyclerView()
        setupSwipeRefresh()
        observeState()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PostListFragment.adapter
            // Divider between each item.
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }
        attachSwipeToDelete()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadPosts() }
    }

    /** Swipe-to-dismiss (delete) on each row. */
    private fun attachSwipeToDelete() {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return
                val deleted = adapter.currentList[position]
                viewModel.onDeletePost(deleted, position)
                showUndoSnackbar()
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerView)
    }

    private fun showUndoSnackbar() {
        Snackbar.make(binding.root, R.string.post_deleted, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { viewModel.onUndoDelete() }
            .show()
    }

    private fun openDetail(post: Post) {
        val action = PostListFragmentDirections.actionListToDetail(post.id)
        findNavController().navigate(action)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.swipeRefresh.isRefreshing = state.isLoading
                    adapter.submitList(state.posts)

                    binding.emptyView.visibility =
                        if (!state.isLoading && state.posts.isEmpty()) View.VISIBLE
                        else View.GONE

                    state.errorMessage?.let { message ->
                        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                        viewModel.onErrorShown()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        // Detach the adapter to prevent the RecyclerView from leaking the view.
        binding.recyclerView.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
