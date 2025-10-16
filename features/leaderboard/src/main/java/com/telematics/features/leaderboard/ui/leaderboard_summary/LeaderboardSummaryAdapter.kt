package com.telematics.features.leaderboard.ui.leaderboard_summary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.telematics.core.common.extension.getIconRes
import com.telematics.core.common.extension.getStringRes
import com.telematics.core.model.leaderboard.LeaderboardType
import com.telematics.core.model.leaderboard.LeaderboardUserItems
import com.telematics.features.leaderboard.R
import com.telematics.features.leaderboard.databinding.LayoutProgressCardBinding

class LeaderboardSummaryAdapter(private val listener: ClickListener) :
    ListAdapter<LeaderboardUserItems, RecyclerView.ViewHolder>(LeaderboardUserItems.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemType.ITEM.ordinal -> {
                LeaderboardItemViewHolder(
                    LayoutProgressCardBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                LeaderboardHeaderViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.leaderboard_user_placeholder_item, parent, false)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).place == -1) ItemType.PLACEHOLDER.ordinal else ItemType.ITEM.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ItemType.ITEM.ordinal -> {
                (holder as LeaderboardItemViewHolder).bind(getItem(position))
            }

            else -> {}
        }
    }

    inner class LeaderboardHeaderViewHolder(containerView: View) :
        RecyclerView.ViewHolder(containerView)

    inner class LeaderboardItemViewHolder(private val binding: LayoutProgressCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LeaderboardUserItems) {
            with(binding.progress) {
                if (item.type == LeaderboardType.Rate) {
                    highlight()
                }
                setProgressMax(item.progressMax)
                setProgress(item.progress)
                setPlace(item.place)
                setImageRes(item.type.getIconRes())
                setTextRes(item.type.getStringRes())
                setClickListener { listener.onClick(item.type) }
            }
        }

    }

    interface ClickListener {
        fun onClick(type: LeaderboardType)
    }

    internal enum class ItemType {
        ITEM, PLACEHOLDER
    }

}