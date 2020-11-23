package sk.lighture.githubusers.ui.search

import android.view.ViewGroup
import coil.load
import kotlinx.android.synthetic.main.search_item.view.*
import sk.lighture.githubusers.R
import sk.lighture.githubusers.ui.abs.BaseListAdapter
import sk.lighture.githubusers.ui.abs.BaseViewHolder
import sk.lighture.githubusers.ui.abs.Diffable
import sk.lighture.githubusers.ui.abs.viewHolderCreator

data class SearchUser(
    val id: Int,
    val login: String,
    val avatarUrl: String?,
    val name: String,
): Diffable {
    override fun getId() = id
}

class SearchUserViewHolder(
    parent: ViewGroup,
    private val onItemClick: (SearchUser) -> Unit
): BaseViewHolder<SearchUser>(parent, R.layout.search_item) {
    override fun bind(item: SearchUser) {
        with(itemView) {
            name.text = item.name
            avatar.load(item.avatarUrl) {
                crossfade(true)
                placeholder(R.drawable.creeper)
            }
            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}

class SearchUserAdapter(
    onItemClick: (SearchUser) -> Unit
): BaseListAdapter<SearchUser>(
    viewHolderCreator { SearchUserViewHolder(it, onItemClick) },
)
