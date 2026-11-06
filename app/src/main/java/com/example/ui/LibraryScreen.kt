package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.audio.SongExportHelper
import com.example.model.Song
import com.example.model.VoiceType
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LibraryScreen(
    viewModel: MainViewModel,
    onNavigateToStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val songs by viewModel.repository.songs.collectAsState()
    val currentPlayingSong by viewModel.playerEngine.currentSong.collectAsState()
    val isPlaying by viewModel.playerEngine.isPlaying.collectAsState()

    var filterVoice by remember { mutableStateOf<VoiceType?>(null) }
    var filterOnlyFavorites by remember { mutableStateOf(false) }

    val filteredSongs = songs.filter { song ->
        val voiceMatch = filterVoice == null || song.voiceType == filterVoice
        val favMatch = !filterOnlyFavorites || song.isFavorite
        voiceMatch && favMatch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LibraryMusic,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "मेरी लाइब्रेरी (Song Library)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "${filteredSongs.size} गाने उपलब्ध हैं",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = filterVoice == null && !filterOnlyFavorites,
                onClick = {
                    filterVoice = null
                    filterOnlyFavorites = false
                },
                label = { Text("सभी (All)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonCyan.copy(alpha = 0.25f),
                    selectedLabelColor = NeonCyan,
                    containerColor = StudioSurface,
                    labelColor = TextPrimary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = filterVoice == null && !filterOnlyFavorites,
                    borderColor = if (filterVoice == null && !filterOnlyFavorites) NeonCyan else StudioCardBorder
                )
            )

            VoiceType.values().forEach { voice ->
                val isSelected = filterVoice == voice
                FilterChip(
                    selected = isSelected,
                    onClick = { filterVoice = if (isSelected) null else voice },
                    label = { Text(voice.hindiName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonMagenta.copy(alpha = 0.25f),
                        selectedLabelColor = NeonMagenta,
                        containerColor = StudioSurface,
                        labelColor = TextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) NeonMagenta else StudioCardBorder
                    )
                )
            }

            FilterChip(
                selected = filterOnlyFavorites,
                onClick = { filterOnlyFavorites = !filterOnlyFavorites },
                label = { Text("पसंदीदा (Favorites)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonAmber.copy(alpha = 0.25f),
                    selectedLabelColor = NeonAmber,
                    containerColor = StudioSurface,
                    labelColor = TextPrimary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = filterOnlyFavorites,
                    borderColor = if (filterOnlyFavorites) NeonAmber else StudioCardBorder
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredSongs.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "यहाँ कोई गाना नहीं मिला",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "स्टूडियो में जाकर अपने मनपसंद बोल लिखकर नया गाना तैयार करें।",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToStudio,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        Text("स्टूडियो में गाना बनाएं ➔", color = StudioBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredSongs, key = { it.id }) { song ->
                    val isCurrentlyPlaying = currentPlayingSong?.id == song.id && isPlaying

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.playSong(song) }
                            .testTag("song_item_${song.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentPlayingSong?.id == song.id) StudioSurfaceVariant else StudioSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (currentPlayingSong?.id == song.id) NeonCyan else StudioCardBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Thumbnail / Play Icon
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.linearGradient(
                                            when (song.voiceType) {
                                                VoiceType.MALE -> listOf(ElectricBlue, NeonCyan)
                                                VoiceType.FEMALE -> listOf(NeonMagenta, NeonAmber)
                                                VoiceType.DUET -> listOf(NeonCyan, NeonMagenta)
                                                VoiceType.CLONED -> listOf(NeonAmber, Color(0xFFFF6D00))
                                            }
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isCurrentlyPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = song.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${song.voiceType.hindiName} • ${song.genre.hindiTitle} • ${song.language.hindiLabel}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "\"${song.prompt}\"",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonAmber.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Favorite button
                            IconButton(
                                onClick = { viewModel.repository.toggleFavorite(song.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (song.isFavorite) NeonMagenta else TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Share button
                            IconButton(
                                onClick = {
                                    viewModel.shareSpecificSong(song)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}
