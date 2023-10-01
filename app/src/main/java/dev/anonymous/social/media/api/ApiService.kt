package dev.anonymous.social.media.api

import dev.anonymous.social.media.models.Album
import dev.anonymous.social.media.models.AlbumPhoto
import dev.anonymous.social.media.models.Post
import dev.anonymous.social.media.models.PostComment
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("posts")
    suspend fun getPosts(): Response<List<Post>>

    @GET("posts/{postId}/comments")
    suspend fun getPostComments(@Path("postId") postId: Int): Response<List<PostComment>>

    @GET("albums")
    suspend fun getAlbums(): Response<List<Album>>

    @GET("albums/{albumId}/photos")
    suspend fun getAlbumPhotos(@Path("albumId") albumId: Int): Response<List<AlbumPhoto>>

}