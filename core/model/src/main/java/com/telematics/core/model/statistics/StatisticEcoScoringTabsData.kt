package com.telematics.core.model.statistics

data class StatisticEcoScoringTabsData(
    var week: StatisticEcoScoringTabData = StatisticEcoScoringTabData(),
    var month: StatisticEcoScoringTabData = StatisticEcoScoringTabData(),
    var year: StatisticEcoScoringTabData = StatisticEcoScoringTabData()
)