package dev.anonymous.social.media.ui.main.albums

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.anonymous.social.media.adapters.AlbumsAdapter
import dev.anonymous.social.media.databinding.FragmentAlbumsBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlbumsFragment : Fragment(), AlbumsAdapter.AlbumsListCallback {
    private lateinit var binding: FragmentAlbumsBinding
    private val viewModel: AlbumsViewModel by viewModels()
    private lateinit var albumsAdapter: AlbumsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setupAlbumsRecycler()
        setupFlowCollectors()
    }

    private fun setupAlbumsRecycler() {
        with(binding.recyclerAlbums) {
            setHasFixedSize(false)
            albumsAdapter = AlbumsAdapter(viewModel, lifecycleScope)
            albumsAdapter.registerAlbumsListCallback(this@AlbumsFragment)
            adapter = albumsAdapter
        }
    }

    private fun setupFlowCollectors() {
        lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    Log.e(TAG, "setupFlowCollectors: $errorMessage")
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    binding.progressAlbums.visibility = View.VISIBLE
                } else {
                    binding.progressAlbums.visibility = View.GONE
                }
            }
        }
    }

    override fun onItemClicked(albumId: Int) {
        val action: NavDirections =
            AlbumsFragmentDirections.actionNavigationAlbumsToAlbumPhotosActivity(albumId)
        findNavController().navigate(action)
    }

    override fun onStart() {
        super.onStart()

        // to solve multi fragment stack
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isEnabled) {
                        requireActivity().finish()
                    }
                }
            })
    }

    companion object {
        private const val TAG = "PostsFragment"
    }
}