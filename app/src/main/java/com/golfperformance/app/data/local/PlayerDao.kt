package com.golfperformance.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY name")
    fun playersFlow(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE name LIKE :query OR club LIKE :query ORDER BY name")
    fun playersFlowFiltered(query: String): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE club = :club ORDER BY name")
    fun playersFlowByClub(club: String): Flow<List<PlayerEntity>>

    @Query("SELECT DISTINCT club FROM players ORDER BY club")
    fun distinctClubsFlow(): Flow<List<String>>

    @Query("SELECT * FROM shots WHERE playerId = :playerId ORDER BY id")
    fun shotsForPlayer(playerId: String): Flow<List<ShotEntity>>

    @Query("SELECT * FROM players WHERE id = :playerId LIMIT 1")
    fun playerByIdFlow(playerId: String): Flow<PlayerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShots(shots: List<ShotEntity>)
}





