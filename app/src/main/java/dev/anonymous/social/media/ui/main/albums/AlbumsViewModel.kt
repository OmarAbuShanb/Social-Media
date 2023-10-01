package dev.anonymous.social.media.ui.main.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.anonymous.social.media.api.ApiRepository
import dev.anonymous.social.media.models.Album
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AlbumsViewModel @Inject constructor(private val apiRepository: ApiRepository) : ViewModel() {
    private val _albumList: MutableStateFlow<List<Album>> = MutableStateFlow(emptyList())
    val albumList: StateFlow<List<Album>> = _albumList

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    lateinit var backgroundColors: IntArray
    val random: Random = Random

    init {
        getAlbums()
    }

    private fun getAlbums() {
        job = viewModelScope.launch((Dispatchers.IO + exceptionHandler)) {
            val response = apiRepository.getAlbums()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _albumList.value = response.body() as List<Album>
                    backgroundColors = IntArray(_albumList.value.size)
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