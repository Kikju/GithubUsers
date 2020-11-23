package sk.lighture.githubusers.ui.user

import sk.lighture.githubusers.api.GithubApi
import sk.lighture.githubusers.api.model.User
import javax.inject.Inject

interface UserRepository {
    suspend fun loadUser(login: String): User
}

class UserRepositoryImpl @Inject constructor(
    private val api: GithubApi
): UserRepository {
    override suspend fun loadUser(login: String): User {
        return api.getUser(login)
    }
}