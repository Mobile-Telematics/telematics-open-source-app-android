package com.telematics.core.network.model.response


import com.google.gson.annotations.SerializedName
import com.telematics.core.network.model.statistics.LeaderboardUserResponse

data class DashboardResponse(
    @SerializedName("data")
    val dashboardData: DashboardData?,
    @SerializedName("success")
    val success: Boolean?
) {
    data class DashboardData(
        @SerializedName("current_daily_safety_score")
        val currentDailySafetyScore: List<SafetyScore>?,
        @SerializedName("current_safety_score")
        val currentSafetyScore: List<SafetyScore>?,
        @SerializedName("current_year_eco_score")
        val currentYearEcoScore: List<EcoScore>?,
        @SerializedName("current_year_statistics")
        val currentYearStatistics: List<Statistic>?,
        @SerializedName("drivecoins")
        val drivecoins: List<Drivecoin>?,
        @SerializedName("last_month_statistics")
        val lastMonthStatistics: List<Statistic>?,
        @SerializedName("last_week_statistics")
        val lastWeekStatistics: List<Statistic>?,
        @SerializedName("last_year_statistics")
        val lastYearStatistics: List<Statistic>?,
        @SerializedName("latest_trip")
        val latestTrip: LatestTrip?,
        @SerializedName("leaderboard")
        val leaderboard: LeaderboardUserResponse?
    )

    data class SafetyScore(
        @SerializedName("AccelerationScore")
        val accelerationScore: Double?,
        @SerializedName("AppId")
        val appId: String?,
        @SerializedName("BrakingScore")
        val brakingScore: Double?,
        @SerializedName("CalcDate")
        val calcDate: String?,
        @SerializedName("CompanyId")
        val companyId: String?,
        @SerializedName("CorneringScore")
        val corneringScore: Double?,
        @SerializedName("InstanceId")
        val instanceId: String?,
        @SerializedName("PermissionsLevel")
        val permissionsLevel: Int?,
        @SerializedName("PhoneUsageScore")
        val phoneUsageScore: Double?,
        @SerializedName("SafetyScore")
        val safetyScore: Double?,
        @SerializedName("SpeedingScore")
        val speedingScore: Double?,
        @SerializedName("TrustLevel")
        val trustLevel: Double?,
        @SerializedName("UserId")
        val userId: String?
    )

    data class EcoScore(
        @SerializedName("AppId")
        val appId: String?,
        @SerializedName("CompanyId")
        val companyId: String?,
        @SerializedName("EcoScore")
        val ecoScore: Double?,
        @SerializedName("EcoScoreBrakes")
        val ecoScoreBrakes: Double?,
        @SerializedName("EcoScoreDepreciation")
        val ecoScoreDepreciation: Double?,
        @SerializedName("EcoScoreFuel")
        val ecoScoreFuel: Double?,
        @SerializedName("EcoScoreTyres")
        val ecoScoreTyres: Double?,
        @SerializedName("InstanceId")
        val instanceId: String?,
        @SerializedName("PermissionsLevel")
        val permissionsLevel: Int?,
        @SerializedName("TrustLevel")
        val trustLevel: Double?,
        @SerializedName("UserId")
        val userId: String?
    )

    data class Statistic(
        @SerializedName("AccelerationsCount")
        val accelerationsCount: Int?,
        @SerializedName("AppId")
        val appId: String?,
        @SerializedName("AverageSpeedKmh")
        val averageSpeedKmh: Double?,
        @SerializedName("AverageSpeedMileh")
        val averageSpeedMileh: Double?,
        @SerializedName("BrakingsCount")
        val brakingsCount: Int?,
        @SerializedName("CompanyId")
        val companyId: String?,
        @SerializedName("CorneringsCount")
        val corneringsCount: Int?,
        @SerializedName("DayDrivingTime")
        val dayDrivingTime: Double?,
        @SerializedName("DriverTripsCount")
        val driverTripsCount: Int?,
        @SerializedName("DrivingTime")
        val drivingTime: Double?,
        @SerializedName("InstanceId")
        val instanceId: String?,
        @SerializedName("MaxSpeedKmh")
        val maxSpeedKmh: Double?,
        @SerializedName("MaxSpeedMileh")
        val maxSpeedMileh: Double?,
        @SerializedName("MileageKm")
        val mileageKm: Double?,
        @SerializedName("MileageMile")
        val mileageMile: Double?,
        @SerializedName("NightDrivingTime")
        val nightDrivingTime: Double?,
        @SerializedName("OtherTripsCount")
        val otherTripsCount: Int?,
        @SerializedName("PermissionsLevel")
        val permissionsLevel: Int?,
        @SerializedName("PhoneUsageDurationMin")
        val phoneUsageDurationMin: Double?,
        @SerializedName("PhoneUsageMileageKm")
        val phoneUsageMileageKm: Double?,
        @SerializedName("PhoneUsageMileageMile")
        val phoneUsageMileageMile: Double?,
        @SerializedName("PhoneUsageSpeedingDurationMin")
        val phoneUsageSpeedingDurationMin: Double?,
        @SerializedName("PhoneUsageSpeedingMileageKm")
        val phoneUsageSpeedingMileageKm: Double?,
        @SerializedName("PhoneUsageSpeedingMileageMile")
        val phoneUsageSpeedingMileageMile: Double?,
        @SerializedName("RushHoursDrivingTime")
        val rushHoursDrivingTime: Double?,
        @SerializedName("TotalSpeedingKm")
        val totalSpeedingKm: Double?,
        @SerializedName("TotalSpeedingMile")
        val totalSpeedingMile: Double?,
        @SerializedName("TripsCount")
        val tripsCount: Int?,
        @SerializedName("TrustLevel")
        val trustLevel: Double?,
        @SerializedName("UserId")
        val userId: String?
    )

    data class LatestTrip(
        @SerializedName("PagingInfo")
        val pagingInfo: PagingInfo?,
        @SerializedName("Trips")
        val trips: List<Trip>?
    ) {
        data class PagingInfo(
            @SerializedName("CurrentPage")
            val currentPage: Int?,
            @SerializedName("TotalPages")
            val totalPages: Int?
        )

        data class Trip(
            @SerializedName("Data")
            val tripData: TripData?,
            @SerializedName("DateUpdated")
            val dateUpdated: String?,
            @SerializedName("Id")
            val id: String?,
            @SerializedName("Identifiers")
            val identifiers: Identifiers?,
            @SerializedName("Scores")
            val scores: Scores?,
            @SerializedName("Statistics")
            val statistics: TripStatistics?
        ) {

            data class TripData(
                @SerializedName("Addresses")
                val addresses: Addresses?,
                @SerializedName("EndDate")
                val endDate: String?,
                @SerializedName("IncomingTrackToken")
                val incomingTrackToken: String?,
                @SerializedName("StartDate")
                val startDate: String?,
                @SerializedName("Tags")
                val tags: List<Any?>?,
                @SerializedName("TransportType")
                val transportType: TransportType?,
                @SerializedName("UnitSystem")
                val unitSystem: String?
            ) {
                data class Addresses(
                    @SerializedName("End")
                    val end: Description?,
                    @SerializedName("Start")
                    val start: Description?
                ) {
                    data class Description(
                        @SerializedName("Full")
                        val full: String?,
                        @SerializedName("Parts")
                        val parts: Parts?
                    ) {
                        data class Parts(
                            @SerializedName("City")
                            val city: String?,
                            @SerializedName("Country")
                            val country: String?,
                            @SerializedName("CountryCode")
                            val countryCode: String?,
                            @SerializedName("County")
                            val county: String?,
                            @SerializedName("District")
                            val district: String?,
                            @SerializedName("Latitude")
                            val latitude: Double?,
                            @SerializedName("Longitude")
                            val longitude: Double?,
                            @SerializedName("Street")
                            val street: String?
                        )
                    }
                }

                data class TransportType(
                    @SerializedName("ConfirmNeeded")
                    val confirmNeeded: Boolean?,
                    @SerializedName("Current")
                    val current: String?,
                    @SerializedName("IsChanged")
                    val isChanged: Boolean?
                )
            }

            data class Identifiers(
                @SerializedName("ApplicationId")
                val applicationId: String?,
                @SerializedName("CompanyId")
                val companyId: String?,
                @SerializedName("InstanceId")
                val instanceId: String?,
                @SerializedName("UserId")
                val userId: String?
            )

            data class Scores(
                @SerializedName("Acceleration")
                val acceleration: Double?,
                @SerializedName("Braking")
                val braking: Double?,
                @SerializedName("Cornering")
                val cornering: Double?,
                @SerializedName("Eco")
                val eco: Double?,
                @SerializedName("EcoBrakes")
                val ecoBrakes: Double?,
                @SerializedName("EcoDepreciation")
                val ecoDepreciation: Double?,
                @SerializedName("EcoFuel")
                val ecoFuel: Double?,
                @SerializedName("EcoTyres")
                val ecoTyres: Double?,
                @SerializedName("PhoneUsage")
                val phoneUsage: Double?,
                @SerializedName("Safety")
                val safety: Double?,
                @SerializedName("Speeding")
                val speeding: Double?
            )

            data class TripStatistics(
                @SerializedName("AccelerationsCount")
                val accelerationsCount: Double?,
                @SerializedName("AverageSpeed")
                val averageSpeed: Double?,
                @SerializedName("BrakingsCount")
                val brakingsCount: Double?,
                @SerializedName("CorneringsCount")
                val corneringsCount: Double?,
                @SerializedName("DayHours")
                val dayHours: Double?,
                @SerializedName("DurationMinutes")
                val durationMinutes: Double?,
                @SerializedName("HighSpeedingMileage")
                val highSpeedingMileage: Double?,
                @SerializedName("MaxSpeed")
                val maxSpeed: Double?,
                @SerializedName("MidSpeedingMileage")
                val midSpeedingMileage: Double?,
                @SerializedName("Mileage")
                val mileage: Double?,
                @SerializedName("NightHours")
                val nightHours: Double?,
                @SerializedName("PhoneUsageDurationMinutes")
                val phoneUsageDurationMinutes: Double?,
                @SerializedName("PhoneUsageMileage")
                val phoneUsageMileage: Double?,
                @SerializedName("PhoneUsageWithSpeedingDurationMinutes")
                val phoneUsageWithSpeedingDurationMinutes: Double?,
                @SerializedName("PhoneUsageWithSpeedingMileage")
                val phoneUsageWithSpeedingMileage: Double?,
                @SerializedName("RushHours")
                val rushHours: Double?,
                @SerializedName("TotalSpeedingMileage")
                val totalSpeedingMileage: Double?
            )
        }
    }

    data class Drivecoin(
        @SerializedName("AcquiredCoins")
        val acquiredCoins: Double?,
        @SerializedName("DeviceToken")
        val deviceToken: String?,
        @SerializedName("TotalEarnedCoins")
        val totalEarnedCoins: Double?
    )
}