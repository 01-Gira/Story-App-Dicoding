package com.example.submission1intermediate.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.submission1intermediate.databinding.ItemLoadingBinding


class LoadingStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {
    class LoadingStateViewHolder(private val binding: ItemLoadingBinding , retry: () -> Unit) :
    RecyclerView.ViewHolder(binding.root){
        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }
        fun bind(loadState: LoadState) {
           binding.apply {
               if (loadState is LoadState.Error){
                   tvMsg.text = loadState.error.localizedMessage
               }
               pbLoading.isVisible = loadState is LoadState.Loading
               btnRetry.isVisible = loadState is LoadState.Error
               tvMsg.isVisible = loadState is LoadState.Error
           }

        }
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, retry)
    }
}