package dev.anonymous.social.media.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.anonymous.social.media.databinding.ItemPostBinding
import dev.anonymous.social.media.models.Post

class PostsAdapter : RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {
    private var postsListCallback: PostsListCallback? = null

    fun registerPostsListCallback(postsListCallback: PostsListCallback) {
        this.postsListCallback = postsListCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder =
        PostsViewHolder(
            ItemPostBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val post = differ.currentList[position]
        holder.bind(post)
        holder.setPostsListCallback(postsListCallback)
    }

    class PostsViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var postsListCallback: PostsListCallback? = null

        fun setPostsListCallback(postsListCallback: PostsListCallback?) {
            this.postsListCallback = postsListCallback
        }

        fun bind(post: Post) {
            with(binding) {
                tvPostTitle.text = post.title
                tvPostBody.text = post.body

                root.setOnClickListener {
                    postsListCallback?.onItemClicked(post.id)
                }
            }
        }
    }

    interface PostsListCallback {
        fun onItemClicked(postId: Int)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}