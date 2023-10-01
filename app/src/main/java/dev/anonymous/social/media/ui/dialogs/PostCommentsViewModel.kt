package dev.anonymous.social.media.ui.dialogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.anonymous.social.media.api.ApiRepository
import dev.anonymous.social.media.models.PostComment
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PostCommentsViewModel @AssistedInject constructor(
    private val apiRepository: ApiRepository,
    @Assisted postId: Int
) : ViewModel() {
    private val _postCommentsList: MutableStateFlow<List<PostComment>> =
        MutableStateFlow(emptyList())
    val postCommentsList: StateFlow<List<PostComment>> = _postCommentsList

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        getPostComments(postId)
    }

    private fun getPostComments(postId: Int) {
        job = viewModelScope.launch((Dispatchers.IO + exceptionHandler)) {
            val response = apiRepository.getPostComments(postId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _postCommentsList.value = response.body() as List<PostComment>
                    _isLoading.value = false
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }

    private fun onError(message: String) {
        runBlocking(Dispatchers.Main) {
            if (message.contains("No address associated with hostname")) {
                _errorMessage.value = "Error while connection :("
            } else {
                _errorMessage.value = message
            }
            _isLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(postId: Int): PostCommentsViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: AssistedFactory,
            postId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(postId) as T
            }
        }
    }
}