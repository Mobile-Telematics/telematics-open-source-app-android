package com.telematics.core.model.statistics

data class EcoScore(
    val score: Int = 0,
    val fuel: Int = 0,
    val tires: Int = 0,
    val brakes: Int = 0,
    val cost: Int = 0
)