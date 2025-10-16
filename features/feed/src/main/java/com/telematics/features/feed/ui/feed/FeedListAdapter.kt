package com.telematics.features.feed.ui.feed

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.telematics.core.common.extension.color
import com.telematics.core.common.extension.drawable
import com.telematics.core.common.extension.format
import com.telematics.core.data.formatter.MeasuresFormatter
import com.telematics.core.model.measures.DistanceMeasure
import com.telematics.core.model.tracking.TripData
import com.telematics.features.feed.R
import com.telematics.features.feed.databinding.LayoutTripItemBinding
import kotlin.math.roundToInt


class FeedListAdapter(private val formatter: MeasuresFormatter) :
    RecyclerView.Adapter<FeedListAdapter.ViewHolder>() {

    private var dataSet: MutableList<TripData> = mutableListOf()
    private var lastPosition = -1
    private val animationList = mutableListOf<ValueAnimator>()
    private var clickListener: ClickListeners? = null


    fun setOnClickListener(listeners: ClickListeners) {
        this.clickListener = listeners
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(data: List<TripData>) {

        animationList.forEach {
            it.duration = 1
        }

        dataSet.addAll(data)
        notifyDataSetChanged()
    }

    fun undoTripTagChange(itemPosition: Int) {

        dataSet[itemPosition].undoTripTagChange()
        notifyItemChanged(itemPosition)
    }

    fun removeItem(itemPosition: Int) {

        dataSet.removeAt(itemPosition)
        notifyItemRemoved(itemPosition)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearAllData() {

        lastPosition = -1
        dataSet.clear()
        notifyDataSetChanged()
    }

    fun updateItemByPos(newType: TripData.TripType, position: Int) {

        dataSet[position].type = newType
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutTripItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])

        val screenWidth = holder.itemView.context.resources.displayMetrics.widthPixels
        holder.binding.eventTripMainBubble.layoutParams.width = screenWidth
        holder.binding.eventTripMainBubble.invalidate()
        holder.binding.eventTripHorizontalScroll.scrollX = 0

        setAnimation(holder.itemView, position)
    }

    override fun getItemCount(): Int = dataSet.size

    private fun setAnimation(view: View, position: Int) {

        val firstFixCount = 6

        if (position > lastPosition && position > firstFixCount) {
            val animation = ValueAnimator.ofFloat(500f, 0f)
            animation.duration = 300
            animation.addUpdateListener {
                val v = it.animatedValue as Float
                view.translationX = v
            }
            animation.start()

            animationList.add(animation)
            lastPosition = position
        }
    }

    inner class ViewHolder(val binding: LayoutTripItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tripItem: TripData) = with(binding) {

            val context = itemView.context

            formatter.getDistanceByKm(tripItem.dist.toDouble()).apply {
                eventTripMileage.text = this.format()
            }

            formatter.getDistanceMeasureValue().apply {
                val distValue = when (formatter.getDistanceMeasureValue()) {
                    DistanceMeasure.KM -> R.string.dashboard_new_km
                    DistanceMeasure.MI -> R.string.dashboard_new_mi
                }
                measureDistText.text = context.getString(distValue)
            }
            val startDate = formatter.parseFullNewDate(tripItem.timeStart!!)
            val endDate = formatter.parseFullNewDate(tripItem.timeEnd!!)
            eventTripDateStart.text = formatter.getDateWithTime(startDate)
            eventTripDateFinish.text = formatter.getDateWithTime(endDate)
            eventTripStartCity.text =
                "|  ".plus(tripItem.cityStart + ", " + tripItem.districtStart) // "125, 5th Really long name Avenue, Pittsburgh, PA"
            eventTripEndCity.text =
                "|  ".plus(tripItem.cityEnd + ", " + tripItem.districtEnd) // "47 Cherry Hill Highway, New York, NY"
            eventTripOverallScore.text = tripItem.rating.roundToInt().toString()

            eventTripOverallScore.setTextColor(
                when (tripItem.rating.roundToInt()) {
                    in 0..40 -> context.resources.color(R.color.colorRedText)
                    in 41..60 -> context.resources.color(R.color.colorOrangeText)
                    in 61..80 -> context.resources.color(R.color.colorYellowText)
                    in 80..100 -> context.resources.color(R.color.colorGreenText)
                    else -> context.resources.color(R.color.colorGreenText)
                }
            )

            eventTripDetailsClickArea.setOnClickListener {
                this@FeedListAdapter.clickListener?.onItemClick(
                    tripItem,
                    absoluteAdapterPosition
                )
            }

            itemEventTypeLayout.setOnClickListener {
                this@FeedListAdapter.clickListener?.onItemChangeTypeClick(
                    tripItem,
                    absoluteAdapterPosition
                )
            }

            eventTripDelete.setOnClickListener {
                this@FeedListAdapter.clickListener?.onItemDelete(
                    tripItem,
                    absoluteAdapterPosition
                )
            }
            eventTripHide.setOnClickListener {
                this@FeedListAdapter.clickListener?.onItemHide(
                    tripItem,
                    absoluteAdapterPosition
                )
            }

            fun updateState() {
                when (tripItem.tag.type) {
                    TripData.TagType.NONE -> {
                        eventTripNoneBtn.isChecked = true
                        eventTripBusinessBtn.isChecked = false
                        eventTripPersonalBtn.isChecked = false
                    }

                    TripData.TagType.PERSONAL -> {
                        eventTripNoneBtn.isChecked = false
                        eventTripBusinessBtn.isChecked = false
                        eventTripPersonalBtn.isChecked = true
                    }

                    TripData.TagType.BUSINESS -> {
                        eventTripNoneBtn.isChecked = false
                        eventTripBusinessBtn.isChecked = true
                        eventTripPersonalBtn.isChecked = false
                    }
                }
            }

            eventTripNoneBtn.setOnClickListener {

                if (eventTripNoneBtn.isChecked && tripItem.tag.type == TripData.TagType.NONE) return@setOnClickListener

                updateState()

                tripItem.setTag(TripData.TagType.NONE)
                @Suppress("DEPRECATION")
                clickListener?.onItemChangeTripTagClick(tripItem, adapterPosition)
            }

            eventTripBusinessBtn.setOnClickListener {
                if (eventTripBusinessBtn.isChecked && tripItem.tag.type != TripData.TagType.BUSINESS) {

                    updateState()
                }

                tripItem.setTag(TripData.TagType.BUSINESS)
                @Suppress("DEPRECATION")
                clickListener?.onItemChangeTripTagClick(tripItem, adapterPosition)
            }

            eventTripPersonalBtn.setOnClickListener {

                if (eventTripPersonalBtn.isChecked && tripItem.tag.type == TripData.TagType.PERSONAL) return@setOnClickListener

                updateState()

                tripItem.setTag(TripData.TagType.PERSONAL)
                @Suppress("DEPRECATION")
                clickListener?.onItemChangeTripTagClick(tripItem, adapterPosition)
            }

            eventTripLabel.text =
                context.getString(R.string.progress_event_trip)
            eventTripLabel.background = itemView.resources.drawable(
                R.drawable.ic_event_trip_label_bg_green,
                context
            )
        }
    }

    interface ClickListeners {
        fun onItemClick(tripData: TripData, listItemPosition: Int)
        fun onItemChangeTypeClick(tripData: TripData, listItemPosition: Int)
        fun onItemDelete(tripData: TripData, listItemPosition: Int)
        fun onItemHide(tripData: TripData, listItemPosition: Int)
        fun onItemChangeTripTagClick(tripData: TripData, listItemPosition: Int)
    }
}