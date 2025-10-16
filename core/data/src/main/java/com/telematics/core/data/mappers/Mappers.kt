package com.telematics.core.data.mappers

import com.telematics.core.data.formatter.MeasuresFormatter
import com.telematics.core.data.mappers.TripsMapper.Companion.getTripTypeByString
import com.telematics.core.model.DashboardData
import com.telematics.core.model.Trip
import com.telematics.core.model.statistics.DailyScore
import com.telematics.core.model.statistics.EcoScore
import com.telematics.core.model.statistics.Score
import com.telematics.core.model.statistics.ScoreType
import com.telematics.core.model.tracking.TripData
import com.telematics.core.model.tracking.TripData.TripType
import com.telematics.core.network.model.response.DashboardResponse

fun TripData.toTripDataShort(): Trip =
    Trip(
        timeStart = timeStart ?: "",
        timeEnd = timeEnd ?: "",
        dist = dist,
        streetStart = streetStart ?: "",
        streetEnd = streetEnd ?: "",
        cityStart = cityStart ?: "",
        cityEnd = cityEnd ?: "",
        type = type ?: TripType.DRIVER,
        isOriginChanged = isOriginChanged
    )

fun DashboardResponse.LatestTrip.Trip.toTrip(formatter: MeasuresFormatter): Trip =
    Trip(
        timeStart = tripData?.endDate?.let { formatter.getFullNewDate(formatter.parseDate(it)) }
            ?: "",
        timeEnd = tripData?.endDate?.let { formatter.getFullNewDate(formatter.parseDate(it)) }
            ?: "",
        dist = statistics?.mileage?.toFloat() ?: 0.0f,
        streetStart = tripData?.addresses?.start?.parts?.street ?: "",
        streetEnd = tripData?.addresses?.end?.parts?.street ?: "",
        cityStart = tripData?.addresses?.start?.parts?.city ?: "",
        cityEnd = tripData?.addresses?.end?.parts?.street ?: "",
        type = getTripTypeByString(tripData?.transportType?.current),
        isOriginChanged = tripData?.transportType?.isChanged ?: false
    )

fun DashboardResponse.SafetyScore.toScoreDataList(): List<Score> {
    return listOf(
        Score(ScoreType.OVERALL, safetyScore?.toInt() ?: 0),
        Score(ScoreType.ACCELERATION, accelerationScore?.toInt() ?: 0),
        Score(ScoreType.BREAKING, brakingScore?.toInt() ?: 0),
        Score(ScoreType.PHONE_USAGE, phoneUsageScore?.toInt() ?: 0),
        Score(ScoreType.SPEEDING, speedingScore?.toInt() ?: 0),
        Score(ScoreType.CORNERING, corneringScore?.toInt() ?: 0)
    )
}

fun DashboardResponse.EcoScore.toDashboardEcoScore(): EcoScore {
    return EcoScore(
        score = ecoScore?.toInt() ?: 0,
        fuel = ecoScoreFuel?.toInt() ?: 0,
        brakes = ecoScoreBrakes?.toInt() ?: 0,
        tires = ecoScoreTyres?.toInt() ?: 0,
        cost = ecoScoreDepreciation?.toInt() ?: 0
    )
}

fun DashboardResponse.DashboardData.toDashboardData(formatter: MeasuresFormatter) =
    DashboardData(
        mileageKm = this.currentYearStatistics?.let {
            if (it.isEmpty()) 0.0
            else it[0].mileageKm
        } ?: 0.0,
        tripsCount = this.currentYearStatistics?.let {
            if (it.isEmpty()) 0
            else it[0].tripsCount
        } ?: 0,
        drivingTime = this.currentYearStatistics?.let {
            if (it.isEmpty()) 0.0
            else it[0].drivingTime
        } ?: 0.0,
        lastTrip = this.latestTrip?.trips?.let {
            if (it.isEmpty()) null
            else it[0].toTrip(formatter)
        },
        dailyScores = this.currentDailySafetyScore?.map {
            DailyScore(
                score = it.safetyScore?.toInt() ?: 0,
                calcDate = it.calcDate ?: ""
            )
        } ?: listOf(),
        scores = this.currentSafetyScore?.let {
            if (it.isEmpty()) Score.empty()
            else it[0].toScoreDataList()
        } ?: Score.empty(),
        ecoScore = this.currentYearEcoScore?.let {
            if (it.isEmpty()) EcoScore()
            else it[0].toDashboardEcoScore()
        } ?: EcoScore(),
        driveCoins = this.drivecoins?.let {
            if (it.isEmpty()) null
            else it[0].totalEarnedCoins?.toInt()
        }
    )
