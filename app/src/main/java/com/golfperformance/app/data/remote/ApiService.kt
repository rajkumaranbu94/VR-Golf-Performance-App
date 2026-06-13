package com.golfperformance.app.data.remote

import retrofit2.http.GET

data class ShotDto(
    val id: String,
    val ballSpeed: Double,
    val launchAngle: Double,
    val carryDistance: Double,
    val spinRate: Double,
    val club: String
)

data class PlayerDto(
    val id: String,
    val name: String,
    val club: String,
    val imageUrl: String,
    val shots: List<ShotDto>
)

interface ApiService {
    @GET("players")
    suspend fun getPlayers(): List<PlayerDto>
}


