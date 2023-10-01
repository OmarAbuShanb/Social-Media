package dev.anonymous.social.media.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.anonymous.social.media.databinding.ItemPostCommentBinding
import dev.anonymous.social.media.models.PostComment

class PostCommentsAdapter : RecyclerView.Adapter<PostCommentsAdapter.PostCommentsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCommentsViewHolder =
        PostCommentsViewHolder(
            ItemPostCommentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: PostCommentsViewHolder, position: Int) =
        holder.bind(differ.currentList[position])

    class PostCommentsViewHolder(private val binding: ItemPostCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(postComments: PostComment) {
            with(binding) {
                tvUserId.text = postComments.id.toString()
                tvUserEmail.text = postComments.email
                tvCommentBody.text = postComments.body
            }
        }
    }


    private val differCallback = object : DiffUtil.ItemCallback<PostComment>() {
        override fun areItemsTheSame(oldItem: PostComment, newItem: PostComment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PostComment, newItem: PostComment): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}