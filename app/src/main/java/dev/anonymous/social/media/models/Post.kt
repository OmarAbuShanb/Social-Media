package dev.anonymous.social.media.models


import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("body")
    val body: String? = null,
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("userId")
    val userId: Int? = null
)