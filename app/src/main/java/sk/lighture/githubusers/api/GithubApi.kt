package sk.lighture.githubusers.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import sk.lighture.githubusers.api.model.SearchUserResult
import sk.lighture.githubusers.api.model.User

interface GithubApi {

    @GET("search/users")
    suspend fun searchUser(
        @Query("q") query: String,
        @Query("sort") sort: String?,
        @Query("order") order: String?,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): SearchUserResult

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): User
}