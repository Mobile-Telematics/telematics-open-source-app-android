package com.telematics.core.data.mappers

import com.telematicssdk.tracking.server.model.sdk.AddressParts
import com.telematicssdk.tracking.server.model.sdk.Track
import com.telematicssdk.tracking.server.model.sdk.TrackDetails
import com.telematicssdk.tracking.server.model.sdk.TrackPoint
import com.telematics.core.data.BuildConfig
import com.telematics.core.data.formatter.MeasuresFormatter
import com.telematics.core.model.tracking.TripData
import com.telematics.core.model.tracking.TripDetailsAddressesData
import com.telematics.core.model.tracking.TripDetailsData
import com.telematics.core.model.tracking.TripImageHolder
import com.telematics.core.model.tracking.TripPointData
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

class TripsMapper @Inject constructor(
    private val formatter: MeasuresFormatter
) {

    fun transformTripDetails(trip: TrackDetails?): TripDetailsData? {
        if (trip == null) {
            return null
        }

        val tripDetailsData = TripDetailsData()

        tripDetailsData.id = trip.trackId
        tripDetailsData.addressStartParts = trip.addressStartParts?.let { convertAdressesParts(it) }
        tripDetailsData.addressFinishParts =
            trip.addressFinishParts?.let { convertAdressesParts(it) }
        tripDetailsData.cityStart = trip.cityStart
        tripDetailsData.cityFinish = trip.cityFinish
        tripDetailsData.startAddress = trip.addressStart
        tripDetailsData.endAddress = trip.addressEnd

        tripDetailsData.startDate =
            trip.startDate?.let { formatter.getTime(formatter.parseDate(it)) }
        tripDetailsData.endDate = trip.endDate?.let { formatter.getTime(formatter.parseDate(it)) }

        tripDetailsData.time = (trip.duration * 60).roundToInt() // in seconds     //32.35
        tripDetailsData.distance = trip.distance.toFloat()     //16757.53814847113

        tripDetailsData.rating = trip.rating100.roundToInt()
        tripDetailsData.ratingAcceleration = trip.ratingAcceleration100.roundToInt()
        tripDetailsData.ratingBraking = trip.ratingBraking100.roundToInt()
        tripDetailsData.ratingSpeeding = trip.ratingSpeeding100.roundToInt()
        tripDetailsData.ratingTimeOfDay = trip.ratingTimeOfDay100.roundToInt()
        tripDetailsData.ratingPhoneUsage = trip.ratingPhoneDistraction100.roundToInt()
        tripDetailsData.ratingCornering = trip.ratingCornering100.roundToInt()

        tripDetailsData.accelerationCount = trip.accelerationCount
        tripDetailsData.speedCount =
            ((trip.highOverSpeedMileage + trip.midOverSpeedMileage).toFloat())
        tripDetailsData.brakingCount = trip.decelerationCount
        tripDetailsData.phoneCount = trip.phoneUsage.toFloat()

        tripDetailsData.points = convertPointsList(trip.points?.asList())
        tripDetailsData.isOriginChanged = trip.hasOriginChanged
        tripDetailsData.type = getTripTypeByString(trip.originalCode)
        tripDetailsData.tripTips = trip.drivingTips

        tripDetailsData.shareType = trip.shareType?.let { transformSharedTripType(it) }

        tripDetailsData.tag.type = if (!trip.tags.isNullOrEmpty()) {

            trip.tags?.find {
                it.source?.lowercase(Locale.ROOT) == BuildConfig.SOURCE.lowercase(Locale.ROOT) &&
                        it.tag.lowercase(Locale.ROOT) != "del"
            }?.let {
                TripData.TagType.parse(it.tag)
            } ?: TripData.TagType.NONE

        } else {
            TripData.TagType.NONE
        }

        return tripDetailsData
    }

    private fun convertAdressesParts(addressParts: AddressParts): TripDetailsAddressesData {
        return TripDetailsAddressesData(
            city = addressParts.city,
            country = addressParts.country,
            countryCode = addressParts.countryCode,
            county = addressParts.county,
            distinct = addressParts.distinct,
            house = addressParts.house,
            postalCode = addressParts.postalCode,
            state = addressParts.state,
            street = addressParts.street
        )
    }

    private fun convertPointsList(pointsListRest: List<TrackPoint>?): List<TripPointData> {
        if (pointsListRest == null) {
            return emptyList()
        }

        val tripPointsDataList = ArrayList<TripPointData>(pointsListRest.size)
        pointsListRest.forEachIndexed { i, _ ->
            val tripPointData = convertPoint(pointsListRest[i])
            if (tripPointData != null) {
                tripPointsDataList.add(tripPointData)
            }
        }
        return tripPointsDataList
    }

    private fun convertPoint(tripPoint: TrackPoint?): TripPointData? {
        if (tripPoint == null) {
            return null
        }
        val tripPointData = TripPointData()

        tripPointData.longitude = tripPoint.longitude
        tripPointData.latitude = tripPoint.latitude
        tripPointData.speedType = tripPoint.speedType
        tripPointData.alertType = tripPoint.alertType
        tripPointData.usePhone = tripPoint.phoneUsage
        tripPointData.alertValue = tripPoint.alertValue
        tripPointData.speed = tripPoint.speed
        tripPointData.fullDate = tripPoint.pointDate!!
        val d = formatter.parseDate(tripPoint.pointDate!!)
        tripPointData.date = formatter.getDateYearTime(d)
        if (tripPoint.cornering) tripPointData.alertType = "turn"
        return tripPointData
    }

    private fun transformSharedTripType(rest: String): TripDetailsData.ShareType {
        return when (rest) {
            "Shared" -> TripDetailsData.ShareType.SHARED
            "NotShared" -> TripDetailsData.ShareType.NOT_SHARED
            else -> throw RuntimeException("Unknown Progress TripType")
        }
    }

    fun transformTripsList(listTripsRest: List<Track>?): List<TripData> {
        if (listTripsRest == null) return emptyList()

        val listTripsData = ArrayList<TripData>(listTripsRest.size)
        listTripsRest.indices.mapTo(listTripsData) {
            convertTrip(listTripsRest[it])
        }
        return listTripsData
    }

    private fun convertTrip(tripRest: Track): TripData {

        val tripData = TripData()

        val startDate = tripRest.startDate?.let {
            formatter.parseDate(it)
        }

        tripData.date = startDate
        tripData.dateMonthDay = formatter.getDateMonthDay(startDate)

        tripData.timeStart = formatter.getFullNewDate(startDate)
        tripData.timeEnd =
            tripRest.endDate?.let { formatter.getFullNewDate(formatter.parseDate(it)) }
        tripData.duration = tripRest.duration.roundToInt() // in minute

        tripData.dist = tripRest.distance.toFloat()
        tripData.streetStart = tripRest.addressStart
        tripData.streetEnd = tripRest.addressEnd
        tripData.cityStart = tripRest.addressStartParts?.city
        tripData.cityEnd = tripRest.addressFinishParts?.city
        tripData.id = tripRest.trackId
        tripData.rating = tripRest.rating100
        tripData.isHideDate = false
        tripData.districtStart = tripRest.addressStartParts?.distinct ?: ""
        tripData.districtEnd = tripRest.addressFinishParts?.distinct ?: ""

        tripData.type = getTripTypeByString(tripRest.originalCode)
        tripData.isOriginChanged = tripRest.hasOriginChanged

        tripData.tag.type = if (!tripRest.tags.isNullOrEmpty()) {

            val deleted = tripRest.tags?.find {
                it.source?.lowercase(Locale.ROOT) == BuildConfig.SOURCE.lowercase(Locale.ROOT) &&
                        it.tag.lowercase(Locale.ROOT) == "del"
            } != null

            if (deleted) {
                tripData.isDeleted = true
                TripData.TagType.NONE
            } else {
                tripRest.tags?.find {
                    it.source?.lowercase(Locale.ROOT) == BuildConfig.SOURCE.lowercase(Locale.ROOT) &&
                            it.tag.lowercase(Locale.ROOT) != "del"
                }?.let {
                    TripData.TagType.parse(it.tag)
                } ?: TripData.TagType.NONE
            }

        } else {
            TripData.TagType.NONE
        }

        return tripData
    }

    private fun getTripTypeByString(tripTypeString: String?): TripData.TripType {

        return when (tripTypeString) {
            "OriginalDriver" -> TripData.TripType.DRIVER
            "Passanger" -> TripData.TripType.PASSENGER
            "Passenger" -> TripData.TripType.PASSENGER
            "Bus" -> TripData.TripType.BUS
            "Taxi" -> TripData.TripType.TAXI
            "Train" -> TripData.TripType.TRAIN
            "Bicycle" -> TripData.TripType.BICYCLE
            "Motorcycle" -> TripData.TripType.MOTORCYCLE
            "Moto" -> TripData.TripType.MOTORCYCLE
            "Walking" -> TripData.TripType.WALKING
            "Running" -> TripData.TripType.RUNNING
            "Other" -> TripData.TripType.OTHER
            else -> TripData.TripType.DRIVER
        }
    }

    fun sort(trips: List<TripData>, last: TripData?): List<TripData> {

        val fullDate = SimpleDateFormat("d", Locale.getDefault())

        var i = if (last == null) 1 else 0

        while (i < trips.size) {
            val tripDataPrev: TripData?
            if (i != 0) {
                tripDataPrev = trips[i - 1]
            } else {
                tripDataPrev = last
            }
            val timePrev = fullDate.format(tripDataPrev!!.date!!)

            val tripData = trips[i]
            val time = fullDate.format(tripData.date!!)
            if (timePrev == time) {
                tripData.isHideDate = true
            }
            i++
        }

        return trips
    }

    fun transform(trip: TripDetailsData): TripImageHolder {
        val r = StringBuilder()
        r.append("r=")
        trip.points!!.withIndex().forEach {
            r.append("${it.value.latitude},${it.value.longitude},")
        }
        /*val m = "${trip.points!![0].latitude}," +
                "${trip.points!![0].longitude}," +
                "${trip.points!!.last().latitude}," +
                "${trip.points!!.last().longitude}"*/
        r.deleteCharAt(r.length - 1)

        val url = StringBuilder()
        // url to the image
        val endpoint = url.toString()
        return TripImageHolder(endpoint, r.toString())
    }

    companion object {

        fun getTripTypeByString(tripTypeString: String?): TripData.TripType {

            return when (tripTypeString) {
                "OriginalDriver" -> TripData.TripType.DRIVER
                "Passanger" -> TripData.TripType.PASSENGER
                "Passenger" -> TripData.TripType.PASSENGER
                "Bus" -> TripData.TripType.BUS
                "Taxi" -> TripData.TripType.TAXI
                "Train" -> TripData.TripType.TRAIN
                "Bicycle" -> TripData.TripType.BICYCLE
                "Motorcycle" -> TripData.TripType.MOTORCYCLE
                "Moto" -> TripData.TripType.MOTORCYCLE
                "Walking" -> TripData.TripType.WALKING
                "Running" -> TripData.TripType.RUNNING
                "Other" -> TripData.TripType.OTHER
                else -> TripData.TripType.DRIVER
            }
        }
    }
}
