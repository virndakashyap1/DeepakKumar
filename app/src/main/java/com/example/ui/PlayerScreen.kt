package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.MicExternalOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.VisualizerView
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GradientEnd
import com.example.ui.theme.GradientMiddle
import com.example.ui.theme.GradientStart
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

@Composable
fun PlayerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentSong by viewModel.playerEngine.currentSong.collectAsState()
    val isPlaying by viewModel.playerEngine.isPlaying.collectAsState()
    val positionMs by viewModel.playerEngine.currentPositionMs.collectAsState()
    val durationMs by viewModel.playerEngine.durationMs.collectAsState()
    val amplitudes by viewModel.playerEngine.visualizerAmplitudes.collectAsState()
    val isLooping by viewModel.playerEngine.isLooping.collectAsState()
    val speed by viewModel.playerEngine.playbackSpeed.collectAsState()
    val isKaraoke by viewModel.playerEngine.isKaraokeMode.collectAsState()

    val currentSeconds = positionMs / 1000
    val totalSeconds = durationMs / 1000

    val scrollState = rememberScrollState()

    if (currentSong == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(StudioBackground),
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
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "कोई गाना नहीं चल रहा है",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "स्टूडियो में नया गाना बनाएं या लाइब्रेरी से कोई गाना चुनें।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    val song = currentSong!!

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Song Artwork Disc
        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonMagenta, ElectricBlue, StudioSurface)
                    )
                )
                .border(3.dp, NeonCyan, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(StudioBackground)
                    .border(2.dp, NeonAmber, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Title and Singer Metadata
        Text(
            text = song.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NeonCyan.copy(alpha = 0.2f)
            ) {
                Text(
                    text = song.voiceType.hindiName,
                    color = NeonCyan,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NeonMagenta.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "${song.genre.hindiTitle} • ${song.language.hindiLabel}",
                    color = NeonMagenta,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Real-Time Animated Equalizer / Waveform
        VisualizerView(
            amplitudes = amplitudes,
            isPlaying = isPlaying,
            modifier = Modifier.fillMaxWidth(),
            height = 70.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Progress Slider
        Slider(
            value = positionMs.toFloat().coerceIn(0f, durationMs.toFloat()),
            onValueChange = { viewModel.playerEngine.seekTo(it.toInt()) },
            valueRange = 0f..durationMs.toFloat(),
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = NeonCyan,
                activeTrackColor = NeonCyan,
                inactiveTrackColor = StudioCardBorder
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = String.format("%02d:%02d", currentSeconds / 60, currentSeconds % 60),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Text(
                text = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Player Transport Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Loop Button
            IconButton(
                onClick = { viewModel.playerEngine.toggleLoop() }
            ) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Loop",
                    tint = if (isLooping) NeonCyan else TextSecondary
                )
            }

            // Seek -10s
            IconButton(
                onClick = { viewModel.playerEngine.seekRelative(-10000) }
            ) {
                Icon(
                    imageVector = Icons.Default.FastRewind,
                    contentDescription = "Rewind 10s",
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Main Play/Pause Button
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(GradientStart, GradientMiddle, GradientEnd))
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    .clickable { viewModel.playerEngine.togglePlayPause() }
                    .testTag("play_pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Seek +10s
            IconButton(
                onClick = { viewModel.playerEngine.seekRelative(10000) }
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Forward 10s",
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Speed Control
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        val nextSpeed = when (speed) {
                            1.0f -> 1.25f
                            1.25f -> 0.8f
                            else -> 1.0f
                        }
                        viewModel.playerEngine.setSpeed(nextSpeed)
                    },
                color = StudioSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Speed",
                        tint = NeonAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${speed}x",
                        color = NeonAmber,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Karaoke Mode Toggle
        FilterChip(
            selected = isKaraoke,
            onClick = { viewModel.playerEngine.toggleKaraoke() },
            label = {
                Text(
                    text = if (isKaraoke) "🎤 कराओके मोड सक्रिय (Karaoke Vocal Off)" else "🎤 कराओके मोड (Sing-Along Mode)",
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.MicExternalOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = NeonMagenta.copy(alpha = 0.25f),
                selectedLabelColor = NeonMagenta,
                containerColor = StudioSurfaceVariant,
                labelColor = TextPrimary
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = isKaraoke,
                borderColor = if (isKaraoke) NeonMagenta else StudioCardBorder
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Action Buttons: Download, Share, Copy
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.downloadCurrentSong() },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("download_song_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download Song",
                    tint = NeonCyan
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("डाउनलोड", color = NeonCyan, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.shareCurrentSong() },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("share_song_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = NeonMagenta
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("शेयर", color = NeonMagenta, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.copyCurrentLyrics() },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("copy_lyrics_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StudioSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Lyrics",
                    tint = TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("बोल (Lyrics)", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Synchronized Scrolling Lyrics Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📜 बोल व अंतरा (Lyrics):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NeonAmber
                    )
                    Text(
                        text = song.instruments.joinToString(", "),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                song.lyrics.forEachIndexed { index, stanza ->
                    val isActiveStanza = currentSeconds >= stanza.timestampSeconds &&
                            (index == song.lyrics.size - 1 || currentSeconds < song.lyrics[index + 1].timestampSeconds)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isActiveStanza) NeonCyan.copy(alpha = 0.12f) else Color.Transparent
                        ),
                        border = if (isActiveStanza) androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)) else null,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isActiveStanza) NeonCyan else StudioCardBorder
                                ) {
                                    Text(
                                        text = stanza.section,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActiveStanza) StudioBackground else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "गायक: ${stanza.singerVoice.displayName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isActiveStanza) NeonCyan else TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            stanza.lines.forEach { line ->
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isActiveStanza) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isActiveStanza) TextPrimary else TextSecondary,
                                    lineHeight = 26.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}
