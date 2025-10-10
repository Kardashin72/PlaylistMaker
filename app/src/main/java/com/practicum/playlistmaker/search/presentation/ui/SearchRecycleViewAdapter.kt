package com.practicum.playlistmaker.search.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackItemBinding
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.core.presentation.utils.dpToPx

class SearchRecycleViewAdapter (
    var tracks: List<Track>,
    private val onItemClick: (Track) -> Unit)
    : RecyclerView.Adapter<SearchRecycleViewAdapter.SearchViewHolder>() {
    private var onItemLongClick: ((Track) -> Unit)? = null

    fun setOnItemLongClickListener(listener: ((Track) -> Unit)?) {
        onItemLongClick = listener
    }

    class SearchViewHolder(private val binding: TrackItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Track) {
            val artworkUrl = track.artworkUrl100

            Glide.with(binding.trackArtwork)
                .load(artworkUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .transform(RoundedCorners(binding.trackArtwork.context.dpToPx(2)))
                .into(binding.trackArtwork)
            binding.trackName.text = track.trackName
            binding.trackArtistName.text = track.artistName
            binding.trackTime.text = track.trackTime
        }

        companion object {
            fun from(parent: ViewGroup): SearchViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = TrackItemBinding.inflate(inflater, parent, false)
                return SearchViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SearchViewHolder.from(parent)

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onItemClick(tracks[position])
        }
        holder.itemView.isLongClickable = onItemLongClick != null
        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(tracks[position])
            onItemLongClick != null
        }
    }

    override fun getItemCount() = tracks.size
}