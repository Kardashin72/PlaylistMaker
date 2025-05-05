package com.practicum.playlistmaker.UI

import com.practicum.playlistmaker.utils.dpToPx
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.Track

class SearchRecycleViewAdapter (
    var tracks: List<Track>,
    private val onItemClick: (Track) -> Unit)
    : RecyclerView.Adapter<SearchRecycleViewAdapter.SearchViewHolder>() {

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val artworkView: ImageView = itemView.findViewById(R.id.track_artwork)
        val trackName: TextView = itemView.findViewById(R.id.track_name)
        val artistName: TextView = itemView.findViewById(R.id.track_artist_name)
        val trackTime: TextView = itemView.findViewById(R.id.track_time)

        fun bind(track: Track) {
            val artworkUrl = track.artworkUrl100

            Glide.with(artworkView)
                .load(artworkUrl)
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(artworkView.context.dpToPx(2)))
                .into(artworkView)
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.trackTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false))
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onItemClick(tracks[position])
        }
    }

    override fun getItemCount() = tracks.size
}