package dev.anonymous.social.media.ui.other

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.anonymous.social.media.R
import dev.anonymous.social.media.adapters.AlbumPhotosAdapter
import dev.anonymous.social.media.databinding.ActivityAlbumPhotosBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlbumPhotosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlbumPhotosBinding

    @Inject
    lateinit var albumPhotosAdapter: AlbumPhotosAdapter

    private val args: AlbumPhotosActivityArgs by navArgs()

    @Inject
    lateinit var itemViewModelAssistedFactory: AlbumPhotosViewModel.AssistedFactory

    private val viewModel: AlbumPhotosViewModel by viewModels {
        AlbumPhotosViewModel.provideFactory(itemViewModelAssistedFactory, args.albumId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.includedToolbar.mainToolbar.title = getString(R.string.album_photos)

        setupAlbumPhotosRecycler()
        setupFlowCollectors()
    }

    private fun setupAlbumPhotosRecycler() {
        with(binding.recyclerAlbumPhotos) {
            setHasFixedSize(false)
            adapter = albumPhotosAdapter
        }
    }

    private fun setupFlowCollectors() {
        lifecycleScope.launch {
            viewModel.albumPhotosList.collect { albumPhotos ->
                albumPhotosAdapter.differ.submitList(albumPhotos)
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    Log.e(TAG, "setupFlowCollectors: $errorMessage")
                    Toast.makeText(baseContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    binding.progressAlbumPhotos.visibility = View.VISIBLE
                } else {
                    binding.progressAlbumPhotos.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        private const val TAG = "AlbumPhotosActivity"
    }
}