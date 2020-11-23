package sk.lighture.githubusers.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import sk.lighture.githubusers.ui.abs.LceViewModel
import timber.log.Timber

class SearchViewModel @ViewModelInject constructor(
    private val repository: SearchRepository
): LceViewModel() {

    private val _users: MutableLiveData<List<SearchUser>> = MutableLiveData()
    val users: LiveData<List<SearchUser>>
        get() = _users

    private var page = 1
    private var lastQuery: String = ""

    suspend fun search(query: String) {
        page = 1
        lastQuery = query
        search(page)
    }

    suspend fun loadMore() {
        page++
        search(page)
    }

    private suspend fun search(page: Int) {
        _loading.value = true
        if (page == 1)
            _users.value = emptyList()
        try {
            val searchResult = repository.search(lastQuery, page)
            if (page == 1) {
                _users.value = searchResult.items?.toUiModel() ?: emptyList()
            }
            else if (searchResult.items?.isNotEmpty() == true) {
                _users.value = (_users.value ?: emptyList()) + searchResult.items.toUiModel()
            }
        } catch (e: Exception) {
            Timber.e(e)
            _error.value = e.message
        } finally {
            _loading.value = false
        }
    }

    private fun List<sk.lighture.githubusers.api.model.SearchUser>.toUiModel() = map { SearchUser(it.id, it.login, it.avatarUrl, it.name ?: it.login) }
}