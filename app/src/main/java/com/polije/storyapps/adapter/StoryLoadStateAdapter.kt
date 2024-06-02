package com.polije.storyapps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.polije.storyapps.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator

class StoryLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<StoryLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.load_state_footer_view_item, parent, false)
        return LoadStateViewHolder(view, retry)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadStateViewHolder(itemView: View, retry: () -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val progressBar: CircularProgressIndicator = itemView.findViewById(R.id.progress_bar)
        private val retryButton: MaterialButton = itemView.findViewById(R.id.retry_button)
        private val errorMsg: TextView = itemView.findViewById(R.id.error_msg)

        init {
            retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                errorMsg.text = loadState.error.localizedMessage
            }
            progressBar.isVisible = loadState is LoadState.Loading
            retryButton.isVisible = loadState !is LoadState.Loading
            errorMsg.isVisible = loadState !is LoadState.Loading
        }
    }
}
