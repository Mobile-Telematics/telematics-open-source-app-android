package com.telematics.core.network.model.response


import com.google.gson.annotations.SerializedName
import com.telematicssdk.tracking.server.model.AddressPartsServerModel
import com.telematicssdk.tracking.server.model.TrackTagServerModel

data class FetchTripsNoTagsResponse(
    @SerializedName("AddressStart")
    var addressStart: String?,

    @SerializedName("AddressEnd")
    var addressEnd: String?,

    @SerializedName("EndDate")
    var endDate: String?,

    @SerializedName("StartDate")
    var startDate: String?,

    @SerializedName("TrackToken")
    var trackId: String?,

    @SerializedName("AccelerationCount")
    var accelerationCount: Int?,

    @SerializedName("DecelerationCount")
    var decelerationCount: Int?,

    @SerializedName("Distance")
    var distance: Double?,

    @SerializedName("Duration")
    var duration: Double?,

    @SerializedName("Rating")
    var rating: Double?,

    @SerializedName("PhoneUsage")
    var phoneUsage: Double?,

    @SerializedName("TrackOriginCode")
    var originalCode: String?,

    @SerializedName("OriginChanged")
    var hasOriginChanged: Boolean?,

    @SerializedName("MidOverSpeedMileage")
    var midOverSpeedMileage: Double?,

    @SerializedName("HighOverSpeedMileage")
    var highOverSpeedMileage: Double?,

    @SerializedName("DrivingTips")
    var drivingTips: String?,

    @SerializedName("ShareType")
    var shareType: String?,

    @SerializedName("CityStart")
    var cityStart: String?,

    @SerializedName("CityFinish")
    var cityFinish: String?,

    @SerializedName("RatingCornering")
    var ratingCornering: Double?,

    @SerializedName("RatingAcceleration")
    var ratingAcceleration: Double?,

    @SerializedName("RatingBraking")
    var ratingBraking: Double?,

    @SerializedName("RatingSpeeding")
    var ratingSpeeding: Double?,

    @SerializedName("RatingPhoneUsage")
    var ratingPhoneDistraction: Double?,

    @SerializedName("RatingTimeOfDay")
    var ratingTimeOfDay: Double?,

    @SerializedName("AddressStartParts")
    val addressStartParts: AddressPartsServerModel?,

    @SerializedName("AddressFinishParts")
    val addressFinishParts: AddressPartsServerModel?,

    @SerializedName("Tags")
    var tags: Array<TrackTagServerModel>?,

    @SerializedName("Rating100")
    var rating100: Double?,

    @SerializedName("RatingCornering100")
    var ratingCornering100: Double?,

    @SerializedName("RatingAcceleration100")
    var ratingAcceleration100: Double?,

    @SerializedName("RatingBraking100")
    var ratingBraking100: Double?,

    @SerializedName("RatingSpeeding100")
    var ratingSpeeding100: Double?,

    @SerializedName("RatingPhoneDistraction100")
    var ratingPhoneDistraction100: Double?,

    @SerializedName("RatingTimeOfDay100")
    var ratingTimeOfDay100: Double?
)