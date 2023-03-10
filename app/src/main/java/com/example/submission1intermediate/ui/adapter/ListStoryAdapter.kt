package com.example.submission1intermediate.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submission1intermediate.data.local.model.UserModel
import com.example.submission1intermediate.data.remote.response.ListStoryItem
import com.example.submission1intermediate.databinding.ItemRowStoryBinding
import com.example.submission1intermediate.ui.detail.DetailActivity

class ListStoryAdapter:
   PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    inner class ListViewHolder(private val binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            binding.tvName.text = data.name
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.ivPhoto)
            itemView.setOnClickListener {
                val intent =Intent(itemView.context, DetailActivity::class.java)

                val user = UserModel (
                    data.id,
                    data.name!!,
                    data.description!!,
                    data.photoUrl!!,
                    data.createdAt!!,
                    data.lat.toString(),
                    data.lon.toString()
                )
                intent.putExtra(DetailActivity.EXTRA_STORY, user)
                itemView.context.startActivity(intent,
                    ActivityOptionsCompat
                        .makeSceneTransitionAnimation(it.context as Activity)
                        .toBundle()
                )
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}