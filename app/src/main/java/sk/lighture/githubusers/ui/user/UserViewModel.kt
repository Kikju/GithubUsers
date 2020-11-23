package sk.lighture.githubusers.ui.user

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import sk.lighture.githubusers.api.model.User
import sk.lighture.githubusers.ui.abs.LceViewModel
import timber.log.Timber

class UserViewModel @ViewModelInject constructor(
    private val repository: UserRepository
): LceViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
        get() = _user

    suspend fun loadUser(login: String) {
        _loading.value = true
        try {
            _user.value = repository.loadUser(login)
        } catch (e: Exception) {
            Timber.e(e)
            _error.value = e.message
        } finally {
            _loading.value = false
        }
    }
}