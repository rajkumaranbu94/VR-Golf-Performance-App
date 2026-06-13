package com.golfperformance.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val club: String,
    val imageUrl: String,
    val avgBallSpeed: Double
)

@Entity(tableName = "shots")
data class ShotEntity(
    @PrimaryKey val id: String,
    val playerId: String,
    val ballSpeed: Double,
    val launchAngle: Double,
    val carryDistance: Double,
    val spinRate: Double,
    val club: String
)


