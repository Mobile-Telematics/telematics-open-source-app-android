package com.telematics.features.dashboard.dashboard

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.telematics.core.model.statistics.Score
import com.telematics.features.dashboard.view.ProgressSemiWheelIndicator


class DashboardTypePagerAdapter(
    private val scoreData: MutableList<Score>
) :
    RecyclerView.Adapter<DashboardTypePagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val page = ProgressSemiWheelIndicator(parent.context)
        page.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        return ViewHolder(page)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(scoreData: List<Score>) {
        this.scoreData.clear()
        this.scoreData.addAll(scoreData)
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = scoreData[position]

    override fun getItemCount(): Int {
        return scoreData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(private val containerView: ProgressSemiWheelIndicator) :
        RecyclerView.ViewHolder(containerView) {
        fun bind(position: Int) {
            containerView.setProgress(scoreData[position].score)
        }
    }
}