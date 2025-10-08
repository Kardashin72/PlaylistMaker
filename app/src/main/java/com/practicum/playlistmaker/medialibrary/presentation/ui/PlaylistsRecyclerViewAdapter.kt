package com.practicum.playlistmaker.medialibrary.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

class PlaylistsRecyclerViewAdapter(

    private val onItemClick: (Playlist) -> Unit = {}
) : RecyclerView.Adapter<PlaylistsRecyclerViewAdapter.PlaylistViewHolder>()  {

    var playlists: List<Playlist> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageCover: ImageView = itemView.findViewById(R.id.imageCover)
        private val textName: TextView = itemView.findViewById(R.id.textName)
        private val textCount: TextView = itemView.findViewById(R.id.textCount)

        fun bind(item: Playlist) {
            textName.text = item.name
            textCount.text = itemView.context.resources.getQuantityString(
                R.plurals.tracks_count,
                item.tracksCount,
                item.tracksCount
            )

            val coverPath = item.coverImagePath
            if (coverPath.isNullOrBlank()) {
                imageCover.setImageResource(R.drawable.playlist_cover_placeholder)
            } else {
                Glide.with(itemView)
                    .load(coverPath)
                    .centerCrop()
                    .placeholder(R.drawable.playlist_cover_placeholder)
                    .error(R.drawable.playlist_cover_placeholder)
                    .into(imageCover)
            }

            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}