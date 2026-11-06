package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.ui.components.VisualizerView
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainViewModel

@Composable
fun VoiceCloneScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isRecording by viewModel.recordAnalyzer.isRecording.collectAsState()
    val liveAmplitude by viewModel.recordAnalyzer.liveAmplitude.collectAsState()
    val recordedSeconds by viewModel.recordAnalyzer.recordedSeconds.collectAsState()
    val activeProfile by viewModel.activeVoiceProfile.collectAsState()
    val profiles by viewModel.voiceProfiles.collectAsState()

    var profileNameInput by remember { mutableStateOf("मेरी नई आवाज़ (My Voice Model)") }
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            viewModel.startVoiceRecording()
        }
    }

    // Pulsing circle animation during recording
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header Art
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.voice_clone_art),
                    contentDescription = "Voice Clone Art",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    StudioBackground.copy(alpha = 0.85f),
                                    StudioBackground
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NeonMagenta.copy(alpha = 0.8f)
                    ) {
                        Text(
                            text = "VOICE CLONE LAB 🎙️",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "अपनी आवाज़ का AI मॉडल बनाएं",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "माइक में 5-10 सेकंड बोलें या गाएं — AI सीखेगा आपकी मधुर आवाज़!",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonAmber
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Vocal Guide Prompter
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📢 नीचे दिया गया वाक्य रिकॉर्डिंग के दौरान बोलें / गाएं:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = StudioSurface
                ) {
                    Text(
                        text = "\"सा रे गा मा पा धा नि सा... मेरी आवाज़ मेरा संगीत है, मैं अपने सुरों से नया गीत बनाऊँगा।\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Live Recorder Area
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurface),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (isRecording) NeonMagenta else StudioCardBorder
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isRecording) "🎙️ रिकॉर्डिंग चालू है... ($recordedSeconds सेकंड)" else "माइक बटन दबाकर रिकॉर्डिंग शुरू करें",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isRecording) NeonMagenta else TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Microphone Circle Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(110.dp)
                ) {
                    if (isRecording) {
                        Box(
                            modifier = Modifier
                                .size(105.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(NeonMagenta.copy(alpha = 0.25f))
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (isRecording) listOf(NeonMagenta, Color(0xFFFF1744))
                                    else listOf(ElectricBlue, NeonCyan)
                                )
                            )
                            .clickable {
                                if (isRecording) {
                                    viewModel.stopVoiceRecording(profileNameInput)
                                } else {
                                    if (hasMicPermission) {
                                        viewModel.startVoiceRecording()
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            }
                            .testTag("record_voice_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Live Audio Level Visualizer
                VisualizerView(
                    amplitudes = if (isRecording) {
                        List(28) { (liveAmplitude * (0.5f + (it % 5) * 0.15f)).coerceIn(0.1f, 1f) }
                    } else {
                        List(28) { 0.1f }
                    },
                    isPlaying = isRecording,
                    modifier = Modifier.fillMaxWidth(),
                    height = 50.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Name Input
        OutlinedTextField(
            value = profileNameInput,
            onValueChange = { profileNameInput = it },
            label = { Text("वॉइस मॉडल का नाम (Voice Profile Name)", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = StudioCardBorder,
                focusedContainerColor = StudioSurface,
                unfocusedContainerColor = StudioSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Active Cloned Voice Details Card
        activeProfile?.let { profile ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Active",
                            tint = NeonGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "सक्रिय मॉडल: ${profile.name}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NeonGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "आवाज़ की श्रेणी:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(text = profile.pitchCategory, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column {
                            Text(text = "मूल सुर (Pitch):", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(text = "${profile.fundamentalPitchHz.toInt()} Hz", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }
                        Column {
                            Text(text = "गर्माहट (Warmth):", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(text = "${(profile.warmthFactor * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = NeonAmber)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.testSingClonedVoice() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("test_cloned_voice_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Test Voice", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("गाकर आवाज़ टेस्ट करें (Listen Sample)", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Saved Voice Profiles List
        Text(
            text = "सहेजे गए वॉइस मॉडल (Saved Voice Models):",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        profiles.forEach { item ->
            val isActive = item.id == activeProfile?.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { viewModel.repository.selectVoiceProfile(item.id) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isActive) NeonCyan.copy(alpha = 0.12f) else StudioSurface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isActive) NeonCyan else StudioCardBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = if (isActive) NeonCyan else TextSecondary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${item.pitchCategory} • ${item.fundamentalPitchHz.toInt()} Hz",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                    if (isActive) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NeonCyan.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Active",
                                color = NeonCyan,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}
