package com.practicum.playlistmaker.UI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.Track
import org.w3c.dom.Text

class SearchRecycleViewAdapter (private val tracks: List<Track>) : RecyclerView.Adapter<SearchRecycleViewAdapter.SearchViewHolder>() {

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val artworkView: ImageView = itemView.findViewById(R.id.track_artwork)
        val trackName: TextView = itemView.findViewById(R.id.track_name)
        val artistName: TextView = itemView.findViewById(R.id.track_artist_name)
        val trackTime: TextView = itemView.findViewById(R.id.track_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val artworkUrl = tracks[position].artworkUrl100
        Glide.with(holder.artworkView)
            .load(artworkUrl)
            .placeholder(R.drawable.placeholder)
            .into(holder.artworkView)
        holder.trackName.text = tracks[position].trackName
        holder.artistName.text = tracks[position].artistName
        holder.trackTime.text = tracks[position].trackTime
    }

    override fun getItemCount() = tracks.size
}