package com.golfperformance.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golfperformance.app.data.local.ShotEntity
import com.golfperformance.app.data.local.PlayerEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.golfperformance.app.repository.PlayerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModel(private val repository: PlayerRepository) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _club = MutableStateFlow("")

    // Expose players as a StateFlow that updates when the search query or club filter changes
    val players: StateFlow<List<PlayerEntity>> =
        combine(_query, _club) { q, c -> q to c }
            .flatMapLatest { (q, c) ->
                when {
                    c.isNotBlank() -> repository.playersFlowByClub(c)
                    q.isBlank() -> repository.playersFlow()
                    else -> repository.playersFlowFiltered("%${q}%")
                }
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val clubs: StateFlow<List<String>> =
        repository.clubsFlow().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun refresh() {
        viewModelScope.launch {
            repository.refreshFromRemote()
        }
    }

    fun shotsForPlayer(playerId: String): StateFlow<List<ShotEntity>> {
        return repository.shotsForPlayer(playerId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    fun playerForId(playerId: String): StateFlow<PlayerEntity?> {
        return repository.playerById(playerId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    }

    fun setQuery(q: String) {
        _query.value = q
    }

    fun setClub(c: String) {
        _club.value = c
    }
}



