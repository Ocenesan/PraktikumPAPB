package com.tifd.projectcomposed.network

import retrofit2.http.GET
import retrofit2.http.Path

// Data class untuk respon GitHub
data class GitHubUser(
    val login: String,
    val name: String?,
    val followers: Int,
    val following: Int,
    val avatar_url: String
)

interface GitHubApiService {
    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): GitHubUser
}
