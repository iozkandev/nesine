package com.iozkan.nesineapp.presentation.chooser

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.iozkan.nesineapp.R
import com.iozkan.nesineapp.databinding.FragmentChooserBinding

/**
 * Landing screen that lets the interviewer pick which UI toolkit to explore.
 * Both buttons lead into flows that sit on the very same domain/data layers —
 * "Views" runs the MVVM + Fragments stack, "Compose" runs the MVI + Compose stack.
 */
class ChooserFragment : Fragment(R.layout.fragment_chooser) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentChooserBinding.bind(view)

        binding.buttonViews.setOnClickListener {
            findNavController().navigate(R.id.action_chooser_to_views)
        }
        binding.buttonCompose.setOnClickListener {
            findNavController().navigate(R.id.action_chooser_to_compose)
        }
    }
}
