package dev.anonymous.social.media.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.anonymous.social.media.databinding.ItemAlbumBinding
import dev.anonymous.social.media.models.Album
import dev.anonymous.social.media.ui.main.albums.AlbumsViewModel
import kotlinx.coroutines.launch

class AlbumsAdapter(
    private val viewModel: AlbumsViewModel,
    lifecycleScope: LifecycleCoroutineScope
) : RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem == newItem
        }
    }

    private var differ = AsyncListDiffer(this, differCallback)

    init {
        lifecycleScope.launch {
            viewModel.albumList.collect { albums ->
                differ.submitList(albums)
            }
        }
    }

    private var albumsListCallback: AlbumsListCallback? = null

    fun registerAlbumsListCallback(albumsListCallback: AlbumsListCallback) {
        this.albumsListCallback = albumsListCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumsViewHolder =
        AlbumsViewHolder(
            ItemAlbumBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: AlbumsViewHolder, position: Int) {
        val album = differ.currentList[position]
        holder.bind(album, getRandomNumber(position))
        holder.setAlbumsListCallback(albumsListCallback)
    }

    /**
     * Returns an integer color if it has been generated for this position before
     * Generates an integer color if it has not been generated before and return it
     */
    private fun getRandomNumber(position: Int): Int {
        with(viewModel) {
            // 0 is default value in IntArray
            if (backgroundColors[position] == 0) {
                // 200 => darken color
                backgroundColors[position] = Color.argb(
                    255,
                    random.nextInt(200),
                    random.nextInt(200),
                    random.nextInt(200)
                )
            }
            return backgroundColors[position]
        }
    }

    class AlbumsViewHolder(private val binding: ItemAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var albumsListCallback: AlbumsListCallback? = null

        fun setAlbumsListCallback(albumsListCallback: AlbumsListCallback?) {
            this.albumsListCallback = albumsListCallback
        }

        fun bind(album: Album, argbColor: Int) {
            with(binding) {
                tvAlbumTitle.text = album.title

                tvAlbumId.setBackgroundColor(argbColor)
                tvAlbumId.text = album.id.toString()

                root.setOnClickListener {
                    albumsListCallback?.onItemClicked(album.id)
                }
            }
        }
    }

    interface AlbumsListCallback {
        fun onItemClicked(albumId: Int)
    }
}