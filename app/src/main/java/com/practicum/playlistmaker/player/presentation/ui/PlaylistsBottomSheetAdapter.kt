package com.practicum.playlistmaker.player.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.presentation.utils.dpToPx
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.databinding.PlaylistBottomSheetItemBinding

class PlaylistsBottomSheetAdapter(
    var playlists: List<Playlist>,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsBottomSheetAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlaylistBottomSheetItemBinding.inflate(inflater, parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position], onItemClick)
    }

    override fun getItemCount(): Int = playlists.size

    class PlaylistViewHolder(private val binding: PlaylistBottomSheetItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Playlist, onClick: (Playlist) -> Unit) {
            binding.textName.text = item.name
            binding.textCount.text = itemView.context.resources.getQuantityString(
                R.plurals.tracks_count,
                item.tracksCount,
                item.tracksCount
            )

            val coverPath = item.coverImagePath
            if (coverPath.isNullOrBlank()) {
                Glide.with(itemView)
                    .load(R.drawable.playlist_cover_placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(itemView.context.dpToPx(8)))
                    .into(binding.imageCover)
            } else {
                Glide.with(itemView)
                    .load(coverPath)
                    .centerCrop()
                    .transform(RoundedCorners(itemView.context.dpToPx(8)))
                    .placeholder(R.drawable.playlist_cover_placeholder)
                    .error(R.drawable.playlist_cover_placeholder)
                    .into(binding.imageCover)
            }

            itemView.setOnClickListener { onClick(item) }
        }
    }
}


