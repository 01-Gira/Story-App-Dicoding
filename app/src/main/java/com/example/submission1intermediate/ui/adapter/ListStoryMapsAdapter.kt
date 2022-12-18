package com.example.submission1intermediate.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submission1intermediate.R
import com.example.submission1intermediate.data.remote.response.ListStoryItem
import com.example.submission1intermediate.data.utils.LocationConverter
import com.example.submission1intermediate.databinding.ItemRowStoryMapsBinding
import com.google.android.gms.maps.model.LatLng

class ListStoryMapsAdapter(private val listStory: List<ListStoryItem>) :
RecyclerView.Adapter<ListStoryMapsAdapter.ListViewHolder>(){
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(private var binding: ItemRowStoryMapsBinding ) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(data: ListStoryItem) {
            val latlng: LatLng? = LocationConverter.toLatlng(data.lat, data.lon)
            binding.iconLocationAvailable.visibility =
                if (latlng != null) View.VISIBLE else View.GONE
            binding.tvName.text = data.name
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.ic_launcher_foreground)
                .into(binding.imgPhoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
       val binding = ItemRowStoryMapsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStory[position])
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listStory[holder.bindingAdapterPosition])
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }

    override fun getItemCount(): Int = listStory.size
}