package sk.lighture.githubusers.ui.search

import sk.lighture.githubusers.api.GithubApi
import sk.lighture.githubusers.api.model.SearchUserResult
import javax.inject.Inject

interface SearchRepository {
    suspend fun search(query: String, page: Int): SearchUserResult
}

class SearchRepositoryImpl @Inject constructor(
    private val api: GithubApi
): SearchRepository {
    override suspend fun search(query: String, page: Int): SearchUserResult {
        return api.searchUser(query, null, null, 10, page)
    }
}