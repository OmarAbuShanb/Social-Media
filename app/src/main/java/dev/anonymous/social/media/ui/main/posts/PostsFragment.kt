package dev.anonymous.social.media.ui.main.posts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.anonymous.social.media.adapters.PostsAdapter
import dev.anonymous.social.media.databinding.FragmentPostsBinding
import dev.anonymous.social.media.utils.navigateSafe
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostsFragment : Fragment(), PostsAdapter.PostsListCallback {
    private lateinit var binding: FragmentPostsBinding
    private val viewModel: PostsViewModel by viewModels()
    private lateinit var postsAdapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setupPostsRecycler()
        setupFlowCollectors()
    }

    private fun setupPostsRecycler() {
        with(binding.recyclerPosts) {
            setHasFixedSize(false)
            postsAdapter = PostsAdapter()
            postsAdapter.registerPostsListCallback(this@PostsFragment)
            adapter = postsAdapter
        }
    }

    private fun setupFlowCollectors() {
        lifecycleScope.launch {
            viewModel.postList.collect { posts ->
                postsAdapter.differ.submitList(posts)
            }
        }

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
                    binding.progressPosts.visibility = View.VISIBLE
                } else {
                    binding.progressPosts.visibility = View.GONE
                }
            }
        }
    }

    override fun onItemClicked(postId: Int) {
        val action: NavDirections = PostsFragmentDirections
            .actionNavigationPostsToNavigationPostCommentsBottomSheet(postId)

        findNavController().navigateSafe(action)
        // if you need to dismiss
        // findNavController().popBackStack()
    }

    companion object {
        private const val TAG = "PostsFragment"
    }
}