package com.iozkan.nesineapp.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.iozkan.nesineapp.databinding.FragmentPostDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Detail / update screen presented as a Material bottom sheet. The user can edit
 * the title and body; saving writes through to the shared repository so the
 * listing screen reflects the change immediately (single source of truth).
 */
@AndroidEntryPoint
class PostDetailFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    // Safe Args supplies "postId" to the ViewModel via SavedStateHandle.
    private val viewModel: PostDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindInitialContent()
        binding.buttonSave.setOnClickListener { onSaveClicked() }
        binding.buttonCancel.setOnClickListener { dismiss() }
    }

    /** Pre-fills the editable fields once, from the current cached post. */
    private fun bindInitialContent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.post.collect { post ->
                    if (post == null) {
                        dismiss()
                        return@collect
                    }
                    // Only set text when the field is empty so we don't clobber
                    // the user's in-progress edits on recollection.
                    if (binding.inputTitle.text.isNullOrEmpty()) {
                        binding.inputTitle.setText(post.title)
                    }
                    if (binding.inputBody.text.isNullOrEmpty()) {
                        binding.inputBody.setText(post.body)
                    }
                }
            }
        }
    }

    private fun onSaveClicked() {
        val title = binding.inputTitle.text?.toString().orEmpty()
        val body = binding.inputBody.text?.toString().orEmpty()

        binding.inputTitleLayout.error =
            if (title.isBlank()) getString(com.iozkan.nesineapp.R.string.error_required) else null
        binding.inputBodyLayout.error =
            if (body.isBlank()) getString(com.iozkan.nesineapp.R.string.error_required) else null

        if (viewModel.saveChanges(title, body)) {
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
