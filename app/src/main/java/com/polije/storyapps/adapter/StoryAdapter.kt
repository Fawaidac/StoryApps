package com.polije.storyapps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.polije.storyapps.R
import com.polije.storyapps.model.Story

class StoryAdapter(private val itemClickListener: OnItemClickListener) :
    PagingDataAdapter<Story, StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        story?.let {
            holder.bindView(it, itemClickListener)
        }
    }

    companion object {
         val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val photoImageView: ImageView = itemView.findViewById(R.id.iv_item_photo)
    private val nameTextView: TextView = itemView.findViewById(R.id.tv_item_name)

    fun bindView(story: Story, itemClickListener: OnItemClickListener) {
        nameTextView.text = story.name

        Glide.with(itemView)
            .load(story.photoUrl)
            .into(photoImageView)

        itemView.setOnClickListener {
            itemClickListener.onItemClick(story)
        }
    }
}

interface OnItemClickListener {
    fun onItemClick(story: Story)
}
