package com.golfperformance.app.ui.players

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
// ...existing code...
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.golfperformance.app.repository.PlayerRepository
import com.golfperformance.app.viewmodel.PlayerViewModel
import org.koin.core.context.GlobalContext
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfperformance.app.viewmodel.PlayerViewModelFactory
import java.util.Locale
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.unit.sp

@Composable
fun PlayerDetailScreen(playerId: String) {
    val repo: PlayerRepository = GlobalContext.get().get()
    val factory = remember { PlayerViewModelFactory(repo) }
    val vm: PlayerViewModel = viewModel(factory = factory)
    val player by vm.playerForId(playerId).collectAsState()
    val shots by vm.shotsForPlayer(playerId).collectAsState()

    // Do not trigger a remote refresh here to avoid unnecessary DB writes while navigating.
    // The list screen is responsible for refreshing from remote (LaunchedEffect in PlayerListScreen).

    var flipped by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(12.dp)) {
        // Flippable header card: front shows primary info + shots; back shows statistics visualization
        val rotation by animateFloatAsState(targetValue = if (flipped) 180f else 0f, animationSpec = tween(400))
        // moderate camera distance to reduce perspective distortion
        val cameraDist = 8f * androidx.compose.ui.platform.LocalDensity.current.density * 100f

        Surface(modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(tween(250))
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
            ) {
                // Front side (rotation 0..90): header + shots
                if (rotation <= 90f) {
                    // apply rotation only to the front content so we can control backface separately
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = cameraDist
                        }
                    ) {
                        if (player != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { flipped = !flipped }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                                    Text(text = player!!.name, style = MaterialTheme.typography.titleLarge)
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(text = "Club: ${player!!.club}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "Avg speed: ${String.format(Locale.US, "%.1f", player!!.avgBallSpeed)} mph", style = MaterialTheme.typography.bodyMedium)
                                }
                                // animated arrow indicator (larger tappable area, no ripple)
                                val arrowRotation by animateFloatAsState(targetValue = if (flipped) 180f else 0f)
                                Text(
                                    text = "▾",
                                    fontSize = 28.sp,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .graphicsLayer(rotationZ = arrowRotation)
                                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { flipped = !flipped }
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(48.dp))
                        }

                        // Shots list (visible on front)
                        LazyColumn {
                            items(shots) { shot ->
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFBDECBF), shape = MaterialTheme.shapes.small)
                                    .padding(8.dp)) {
                                    Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)) {
                                        Text(text = "Club: ${shot.club}")
                                        Text(text = "Ball speed: ${shot.ballSpeed}")
                                        Text(text = "Launch angle: ${shot.launchAngle}")
                                        Text(text = "Carry: ${shot.carryDistance}")
                                        Text(text = "Spin rate: ${shot.spinRate}")
                                    }
                                }
                                Spacer(modifier = Modifier.size(8.dp))
                            }
                        }
                    }
                } else {
                    // Back side (rotation 90..180) — draw back content with corrected rotation to avoid mirror
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .graphicsLayer {
                                // rotate the back content so it appears upright; offset by -180
                                rotationY = rotation - 180f
                                // do not flip horizontally (remove scaleX) so text and charts render readable
                                cameraDistance = cameraDist
                            }
                    ) {
                        // Small close indicator at top
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { flipped = !flipped }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "▴",
                                fontSize = 28.sp,
                                modifier = Modifier
                                    .size(40.dp)
                                    .graphicsLayer(rotationZ = 180f)
                                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { flipped = !flipped }
                            )
                        }

                        // Stats: average ball speed and average spin as progress indicators
                        val avgSpeed = player?.avgBallSpeed ?: 0.0
                        val avgSpin = if (shots.isNotEmpty()) shots.map { it.spinRate }.average() else 0.0

                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Average Ball Speed")
                            LinearProgressIndicator(progress = { (avgSpeed / 200.0).toFloat() }, modifier = Modifier.fillMaxWidth().height(8.dp))
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(text = String.format(Locale.US, "%.1f mph", avgSpeed))
                            Spacer(modifier = Modifier.size(12.dp))

                            Text(text = "Average Spin Rate")
                            LinearProgressIndicator(progress = { (avgSpin / 10000.0).toFloat() }, modifier = Modifier.fillMaxWidth().height(8.dp))
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(text = String.format(Locale.US, "%.0f rpm", avgSpin))
                            Spacer(modifier = Modifier.size(12.dp))

                            // Simple bar chart of shot ball speeds
                            Text(text = "Shot Ball Speeds")
                            val speeds = shots.map { it.ballSpeed }
                            val maxSpeed = (speeds.maxOrNull() ?: 1.0)
                            Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                                val barWidth = size.width / (speeds.size.coerceAtLeast(1))
                                speeds.forEachIndexed { index, s ->
                                    val barHeight = (s / maxSpeed * size.height).toFloat()
                                    val left = index * barWidth
                                    drawRect(color = Color(0xFF3DDC84), topLeft = androidx.compose.ui.geometry.Offset(left, size.height - barHeight), size = Size(barWidth * 0.7f, barHeight))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
