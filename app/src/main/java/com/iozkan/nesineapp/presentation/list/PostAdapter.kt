package com.iozkan.nesineapp.presentation.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.iozkan.nesineapp.R
import com.iozkan.nesineapp.databinding.ItemPostBinding
import com.iozkan.nesineapp.domain.model.Post

/**
 * RecyclerView adapter backed by [ListAdapter] + [DiffUtil] so list changes
 * (update / swipe-to-delete) animate efficiently without manual notify* calls.
 */
class PostAdapter(
    private val onItemClick: (Post) -> Unit
) : ListAdapter<Post, PostAdapter.PostViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class PostViewHolder(
        private val binding: ItemPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(pos))
                }
            }
        }

        fun bind(post: Post, position: Int) {
            binding.textTitle.text = post.title
            binding.textBody.text = post.body

            // Random grayscale image keyed by the adapter position, as required.
            val imageUrl = "https://picsum.photos/300/300?random=$position&grayscale"
            binding.imageAvatar.load(imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem == newItem
        }
    }
}
