package com.golfperformance.app.ui.players

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfperformance.app.viewmodel.PlayerViewModelFactory
import androidx.compose.runtime.remember
import com.golfperformance.app.repository.PlayerRepository
import com.golfperformance.app.viewmodel.PlayerViewModel
import org.koin.core.context.GlobalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun PlayerListScreen(onPlayerClick: (String) -> Unit) {
    val repo: PlayerRepository = GlobalContext.get().get()
    val factory = remember { PlayerViewModelFactory(repo) }
    val vm: PlayerViewModel = viewModel(factory = factory)

    LaunchedEffect(Unit) { vm.refresh() }

    var search by remember { mutableStateOf("") }
    val players by vm.players.collectAsState()
    val clubs by vm.clubs.collectAsState()
    var selectedClub by remember { mutableStateOf("") }

    // update viewmodel when search changes
    LaunchedEffect(search) { vm.setQuery(search) }

    Column(modifier = Modifier.padding(8.dp)) {
        // Club filter row with horizontal scroll (LazyRow)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
            item {
                val allSelected = selectedClub.isEmpty()
                FilterChip(
                    selected = allSelected,
                    onClick = {
                        vm.setClub("")
                        selectedClub = ""
                    },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }

            items(clubs) { club ->
                val isSelected = selectedClub == club
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        vm.setClub(club)
                        selectedClub = club
                    },
                    label = { Text(club) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        TextField(value = search, onValueChange = { search = it }, label = { Text("Search") }, modifier = Modifier.fillMaxWidth().padding(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
        items(players) { player ->
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onPlayerClick(player.id) }
            ) {
                Row(modifier = Modifier.padding(12.dp)) {
                    val ctx = LocalContext.current
                    AsyncImage(
                        model = ImageRequest.Builder(ctx)
                            .data(player.imageUrl)
                            .crossfade(false)
                            .build(),
                        contentDescription = "Player",
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Column {
                        Text(text = player.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Club: ${player.club}")
                        Text(text = "Avg speed: ${String.format("%.1f", player.avgBallSpeed)} mph")
                    }
                }
            }
        }
    }
    }
}
