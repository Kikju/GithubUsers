package sk.lighture.githubusers.ui.search

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import sk.lighture.githubusers.R
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.search_fragment) {

    private val viewModel: SearchViewModel by viewModels()

    private val searchAdapter = SearchUserAdapter {
        findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToUserFragment(it.login))
    }
    private val loadingAdapter = LoadingAdapter()
    private val adapter = ConcatAdapter(searchAdapter, loadingAdapter)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val searchView = (toolbar.menu.findItem(R.id.menuSearchItem)?.actionView as SearchView)
            searchView.onQueryTextChange()
                .debounce(500)
                .onEach {
                    Timber.d("trigger search")
                    viewModel.search(if (it.isBlank()) "a" else it)
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
            val edit = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            val colorOnPrimary = resources.getColor(R.color.colorOnPrimary, requireActivity().theme)
            edit.setTextColor(colorOnPrimary)
            edit.setHintTextColor(colorOnPrimary)
        } catch (e: Exception) {
            Timber.e(e)
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.addItemDecoration(DividerItemDecoration(requireContext(), (recycler.layoutManager as LinearLayoutManager).orientation))
        recycler.adapter = adapter
        recycler.addOnScrollListener(object: OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!loadingAdapter.enabled) {
                    if ((recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == searchAdapter.itemCount - 2) {
                        Timber.d("load more")
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.loadMore()
                        }
                    }
                }
            }
        })

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading && !loadingAdapter.enabled)
                loadingAdapter.enabled = true
            else if (!loading && loadingAdapter.enabled)
                loadingAdapter.enabled = false
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Snackbar.make(coordinator, error ?: "Unknown error", Snackbar.LENGTH_LONG).show()
        }

        viewModel.users.observe(viewLifecycleOwner) { users ->
            searchAdapter.submitList(users)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.search("a")
        }
    }


    private fun SearchView.onQueryTextChange() = callbackFlow<String> {
        setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Timber.d("onQueryTextChange")
                newText?.let { offer(it) }
                return false
            }
        })
        awaitClose { setOnQueryTextListener(null) }
    }
}