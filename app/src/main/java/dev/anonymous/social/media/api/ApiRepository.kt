package dev.anonymous.social.media.api

class ApiRepository(private val apiService: ApiService) {
    suspend fun getPosts() = apiService.getPosts()
    suspend fun getPostComments(postId: Int) = apiService.getPostComments(postId)

    suspend fun getAlbums() = apiService.getAlbums()
    suspend fun getAlbumPhotos(albumId: Int) = apiService.getAlbumPhotos(albumId)
}