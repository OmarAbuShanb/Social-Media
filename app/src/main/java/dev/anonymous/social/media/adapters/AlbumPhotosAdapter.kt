package dev.anonymous.social.media.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.anonymous.social.media.databinding.ItemAlbumPhotoBinding
import dev.anonymous.social.media.models.AlbumPhoto
import javax.inject.Inject

class AlbumPhotosAdapter @Inject constructor(
    private val glide: RequestManager,
    private val drawableTransitionOptions: DrawableTransitionOptions
) : RecyclerView.Adapter<AlbumPhotosAdapter.AlbumPhotosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumPhotosViewHolder =
        AlbumPhotosViewHolder(
            ItemAlbumPhotoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: AlbumPhotosViewHolder, position: Int) =
        holder.bind(differ.currentList[position])

    inner class AlbumPhotosViewHolder(private val binding: ItemAlbumPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(albumPhotos: AlbumPhoto) {
            with(binding) {
                tvTitleAlbumPhoto.text = albumPhotos.title
                glide.load(albumPhotos.url)
                    .transition(drawableTransitionOptions)
                    .into(ivAlbumPhoto)
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<AlbumPhoto>() {
        override fun areItemsTheSame(oldItem: AlbumPhoto, newItem: AlbumPhoto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AlbumPhoto, newItem: AlbumPhoto): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}