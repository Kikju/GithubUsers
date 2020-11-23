package sk.lighture.githubusers.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sk.lighture.githubusers.R
import kotlin.properties.Delegates

class LoadingViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.loading_item, parent, false))

class LoadingAdapter: RecyclerView.Adapter<LoadingViewHolder>() {
    var enabled: Boolean by Delegates.observable(false) { property, oldValue, newValue ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadingViewHolder {
        return LoadingViewHolder(parent)
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return if (enabled) 1 else 0
    }

}
