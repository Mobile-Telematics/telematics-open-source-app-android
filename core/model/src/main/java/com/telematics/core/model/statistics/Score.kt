package com.telematics.core.model.statistics

data class Score(
    val type: ScoreType = ScoreType.OVERALL,
    val score: Int = -1
) {
    companion object {
        fun empty() = mutableListOf(
            Score(ScoreType.OVERALL, -2),
            Score(ScoreType.ACCELERATION, -2),
            Score(ScoreType.BREAKING, -2),
            Score(ScoreType.PHONE_USAGE, -2),
            Score(ScoreType.SPEEDING, -2),
            Score(ScoreType.CORNERING, -2)
        )
    }
}