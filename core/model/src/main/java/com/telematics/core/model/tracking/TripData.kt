package com.telematics.core.model.tracking

import com.telematics.core.model.R
import java.io.Serializable
import java.util.Date
import java.util.Locale

data class TripData(

    var date: Date? = null,
    var dateMonthDay: String? = null,
    var timeStart: String? = null,
    var timeEnd: String? = null,
    var duration: Int = 0,    // in minute

    var dist: Float = 0f,
    var streetStart: String? = null,
    var streetEnd: String? = null,
    var cityStart: String? = null,
    var cityEnd: String? = null,
    var districtStart: String? = null,
    var districtEnd: String? = null,
    var id: String? = null,
    var rating: Double = 0.0,
    var isHideDate: Boolean = false,

    var type: TripType? = null,
    var isOriginChanged: Boolean = false,
    val tag: Tag = Tag(),
    var isDeleted: Boolean = false

) : Serializable {

    private var originalTagType: TagType? = null

    fun undoTripTagChange() {
        originalTagType?.also {
            tag.type = it
        }
        originalTagType = null
    }

    fun setTag(tag: TagType) {
        originalTagType = this.tag.type
        this.tag.type = tag
    }

    enum class TripType(val nameId: Int, val imageId: Int) {
        DRIVER(R.string.progress_trip_type_driver, R.drawable.ic_event_trip_bubble_driver),
        PASSENGER(R.string.progress_trip_type_passenger, R.drawable.ic_event_trip_bubble_passenger),
        BUS(R.string.progress_trip_type_bus, R.drawable.ic_event_trip_bubble_bus),
        TAXI(R.string.progress_trip_type_taxi, R.drawable.ic_event_trip_bubble_taxi),
        TRAIN(R.string.progress_trip_type_train, R.drawable.ic_event_trip_bubble_train),
        BICYCLE(R.string.progress_trip_type_bicycle, R.drawable.ic_event_trip_bubble_bicycle),
        MOTORCYCLE(
            R.string.progress_trip_type_motorcycle,
            R.drawable.ic_event_trip_bubble_motorcycle
        ),
        WALKING(R.string.progress_trip_type_other, R.drawable.ic_event_trip_bubble_other),
        RUNNING(R.string.progress_trip_type_other, R.drawable.ic_event_trip_bubble_other),
        OTHER(R.string.progress_trip_type_other, R.drawable.ic_event_trip_bubble_other);

        override fun toString(): String {
            return when (this) {
                DRIVER -> "OriginalDriver"
                PASSENGER -> "Passanger"
                BUS -> "Bus"
                TAXI -> "Taxi"
                TRAIN -> "Train"
                BICYCLE -> "Bicycle"
                MOTORCYCLE -> "Motorcycle"
                WALKING -> "Walking"
                RUNNING -> "Running"
                OTHER -> "Other"
            }
        }
    }

    enum class TagType {
        NONE, PERSONAL, BUSINESS;

        override fun toString(): String {
            return when (this) {
                NONE -> "None"
                PERSONAL -> "Personal"
                BUSINESS -> "Business"
            }
        }

        companion object {
            fun parse(value: String?): TagType {
                return when (value?.lowercase(Locale.ROOT)) {
                    "business" -> BUSINESS
                    "personal" -> PERSONAL
                    else -> NONE
                }
            }
        }
    }

    data class Tag(var type: TagType = TagType.NONE) : Serializable
}