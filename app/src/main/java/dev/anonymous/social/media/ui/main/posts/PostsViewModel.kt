package dev.anonymous.social.media.ui.main.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.anonymous.social.media.api.ApiRepository
import dev.anonymous.social.media.models.Post
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(private val apiRepository: ApiRepository) : ViewModel() {
    private val _postList: MutableStateFlow<List<Post>> = MutableStateFlow(emptyList())
    val postList: StateFlow<List<Post>> = _postList

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        getPosts()
    }

    private fun getPosts() {
        job = viewModelScope.launch((Dispatchers.IO + exceptionHandler)) {
            val response = apiRepository.getPosts()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _postList.value = response.body()!!
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
}