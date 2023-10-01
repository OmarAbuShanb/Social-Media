package dev.anonymous.social.media.models


import com.google.gson.annotations.SerializedName

data class PostComment(
    @SerializedName("body")
    val body: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("postId")
    val postId: Int? = null
)