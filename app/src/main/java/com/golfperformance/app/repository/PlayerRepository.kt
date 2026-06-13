package com.golfperformance.app.repository

import com.golfperformance.app.data.local.PlayerDao
import com.golfperformance.app.data.local.PlayerEntity
import com.golfperformance.app.data.local.ShotEntity
import com.golfperformance.app.data.remote.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlayerRepository(
    private val api: ApiService,
    private val dao: PlayerDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun playersFlow(): Flow<List<PlayerEntity>> = dao.playersFlow()

    fun playersFlowFiltered(query: String): Flow<List<PlayerEntity>> = dao.playersFlowFiltered(query)

    fun playersFlowByClub(club: String): Flow<List<PlayerEntity>> = dao.playersFlowByClub(club)

    fun shotsForPlayer(playerId: String): Flow<List<ShotEntity>> = dao.shotsForPlayer(playerId)

    fun playerById(playerId: String): Flow<PlayerEntity?> = dao.playerByIdFlow(playerId)

    fun clubsFlow(): Flow<List<String>> = dao.distinctClubsFlow()

    suspend fun refreshFromRemote() = withContext(ioDispatcher) {
        val playersDto = api.getPlayers()
        val players = playersDto.map { dto ->
            val avgSpeed = if (dto.shots.isNotEmpty()) dto.shots.map { it.ballSpeed }.average() else 0.0
            PlayerEntity(dto.id, dto.name, dto.club, dto.imageUrl, avgSpeed)
        }
        val shots = playersDto.flatMap { dto ->
            dto.shots.map { s ->
                ShotEntity(s.id, dto.id, s.ballSpeed, s.launchAngle, s.carryDistance, s.spinRate, s.club)
            }
        }

        dao.insertPlayers(players)
        dao.insertShots(shots)
    }
}





