package com.telematics.core.data.datasource.mappers

import com.telematics.core.database.entity.DailyScoreEntity
import com.telematics.core.database.entity.EcoScoreEntity
import com.telematics.core.database.entity.ScoreEntity
import com.telematics.core.database.entity.TripEntity
import com.telematics.core.model.Trip
import com.telematics.core.model.statistics.DailyScore
import com.telematics.core.model.statistics.EcoScore
import com.telematics.core.model.statistics.Score
import com.telematics.core.model.statistics.ScoreType
import com.telematics.core.model.tracking.TripData.TripType

fun TripEntity.toTrip() = Trip(
    timeStart = timeStart,
    timeEnd = timeEnd,
    dist = dist,

    streetStart = streetStart,
    streetEnd = streetEnd,

    cityStart = cityStart,
    cityEnd = cityEnd,

    type = try {
        TripType.valueOf(type)
    } catch (_: Exception) {
        TripType.DRIVER
    },
    isOriginChanged = isOriginChanged,
)

fun Trip.toTripEntity(userId: String) = TripEntity(
    timeStart = timeStart,
    timeEnd = timeEnd,
    dist = dist,

    streetStart = streetStart,
    streetEnd = streetEnd,

    cityStart = cityStart,
    cityEnd = cityEnd,

    type = type.name,
    isOriginChanged = isOriginChanged,
    userId = userId
)

fun ScoreEntity.toScore() =
    Score(
        type = try {
            ScoreType.valueOf(type)
        } catch (_: Exception) {
            ScoreType.OVERALL
        },
        score = score
    )

fun DailyScoreEntity.toDailyScore() =
    DailyScore(
        score = score,
        calcDate = calcDate
    )

fun Score.toScoreEntity(userId: String, position: Int) =
    ScoreEntity(
        type = type.name,
        score = score,
        position = position,
        userId = userId
    )

fun DailyScore.toDailyScoreEntity(userId: String) =
    DailyScoreEntity(
        score = score,
        calcDate = calcDate,
        userId = userId
    )

fun EcoScoreEntity.toEcoScore() =
    EcoScore(
        score = score,
        fuel = fuel,
        tires = tires,
        brakes = brakes,
        cost = cost
    )

fun EcoScore.toEcoScoreEntity(userId: String) =
    EcoScoreEntity(
        score = score,
        fuel = fuel,
        tires = tires,
        brakes = brakes,
        cost = cost,
        userId = userId
    )

