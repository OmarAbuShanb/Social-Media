package dev.anonymous.social.media.ui.dialogs

import android.animation.ValueAnimator
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.anonymous.social.media.adapters.PostCommentsAdapter
import dev.anonymous.social.media.databinding.DialogPostCommentsBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PostCommentsBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: DialogPostCommentsBinding
    private lateinit var postCommentsAdapter: PostCommentsAdapter

    private val args: PostCommentsBottomSheetArgs by navArgs()

    @Inject
    lateinit var itemViewModelAssistedFactory: PostCommentsViewModel.AssistedFactory

    private val viewModel: PostCommentsViewModel by viewModels {
        PostCommentsViewModel.provideFactory(itemViewModelAssistedFactory, args.postId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPostCommentsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setupPostCommentsRecycler()
        setupFlowCollectors()
    }

    private fun setupPostCommentsRecycler() {
        with(binding.recyclerPostComments) {
            setHasFixedSize(false)
            postCommentsAdapter = PostCommentsAdapter()
            adapter = postCommentsAdapter
        }
    }

    private fun setupFlowCollectors() {
        lifecycleScope.launch {
            viewModel.postCommentsList.collect { postComments ->
                postCommentsAdapter.differ.submitList(postComments)
                expandedSheetHeightWithAnimation()
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    Log.e(TAG, "setupFlowCollectors: $errorMessage")
                    findNavController().popBackStack()
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    binding.progressPostComments.visibility = View.VISIBLE
                } else {
                    binding.progressPostComments.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun expandedSheetHeightWithAnimation() {
        val bottomSheetLayout: FrameLayout? =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)

        bottomSheetLayout?.let { nonNullBottomSheetLayout ->
            val sheetBehavior = BottomSheetBehavior.from(nonNullBottomSheetLayout)

            val oldSheetHeight: Int = nonNullBottomSheetLayout.height
            val newSheetHeight: Int = (getScreenHeight() * 0.85).toInt()

            // Create ValueAnimator object
            with(ValueAnimator()) {
                // animation from old height to new height
                setIntValues(oldSheetHeight, newSheetHeight)
                duration = 300L
                interpolator = AccelerateDecelerateInterpolator()
                start()

                addUpdateListener { animator ->
                    // change sheet height
                    sheetBehavior.peekHeight = animator.animatedValue as Int
                }
            }
        }
    }

    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    companion object {
        private const val TAG = "PostCommentsBottomSheet"
    }
}