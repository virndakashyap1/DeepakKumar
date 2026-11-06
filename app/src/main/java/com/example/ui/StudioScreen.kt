package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.MusicGenre
import com.example.model.SongLanguage
import com.example.model.VoiceType
import com.example.ui.components.VisualizerView
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GradientEnd
import com.example.ui.theme.GradientMiddle
import com.example.ui.theme.GradientStart
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioScreen(
    viewModel: MainViewModel,
    onNavigateToVoiceClone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val promptText by viewModel.promptText.collectAsState()
    val selectedVoiceType by viewModel.selectedVoiceType.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedInstruments by viewModel.selectedInstruments.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generationStatus by viewModel.generationStatus.collectAsState()
    val activeVoiceProfile by viewModel.activeVoiceProfile.collectAsState()

    val availableInstruments = listOf(
        "बांसुरी (Flute)",
        "हारमोनियम (Harmonium)",
        "सितार (Sitar)",
        "तबला (Tabla)",
        "ढोलक (Dholak)",
        "गिटार (Guitar)",
        "808 बीट्स (Lo-Fi Beats)"
    )

    val quickPrompts = listOf(
        "🌧️ बारिश में बिछड़े प्यार की याद में एक दर्द भरा रोमांटिक गाना",
        "💃 देसी ढोलक पर थिरकने वाला मजेदार पार्टी डांस ट्रैक",
        "🙏 वृंदावन में राधा-कृष्ण का मधुर सुरीला भजन",
        "🕊️ सूफी रूहानियत से भरा दिल को छूने वाला कलाम",
        "🌅 सुबह की ताजगी और नई उम्मीदों का प्रेरक गीत",
        "✨ Acoustic soft romantic ballad in English"
    )

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Hero Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.music_studio_hero),
                        contentDescription = "Studio Hero Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
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
                            color = NeonPurple.copy(alpha = 0.8f)
                        ) {
                            Text(
                                text = "AI MUSIC STUDIO 🎶",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "AI MADHUR SANGEET",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "सिर्फ प्रॉम्प्ट लिखें — AI बनाएगा मधुर आवाज़ में संपूर्ण गाना!",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Song Prompt Input
            Text(
                text = "१. गाने का विषय / प्रॉम्प्ट लिखें (Song Prompt)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = promptText,
                onValueChange = { viewModel.promptText.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("prompt_input_field"),
                placeholder = {
                    Text(
                        text = "उदा. सावन की बारिश में भीगता हुआ एक भावुक रोमांटिक गाना...",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = StudioCardBorder,
                    focusedContainerColor = StudioSurface,
                    unfocusedContainerColor = StudioSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Prompt Suggestions
            Text(
                text = "⚡ तुरंत चुनें (Quick Prompts):",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickPrompts.forEach { prompt ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { viewModel.promptText.value = prompt }
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(20.dp)),
                        color = StudioSurfaceVariant
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Lead Voice Selection
            Text(
                text = "२. आवाज़ चुनें (Lead Singer Voice)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VoiceType.values().forEach { voice ->
                    val isSelected = selectedVoiceType == voice
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { viewModel.selectedVoiceType.value = voice }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) NeonCyan else StudioCardBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .testTag("voice_option_${voice.name}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) NeonCyan.copy(alpha = 0.15f) else StudioSurface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = when (voice) {
                                    VoiceType.MALE -> Icons.Default.Person
                                    VoiceType.FEMALE -> Icons.Default.RecordVoiceOver
                                    VoiceType.DUET -> Icons.Default.AutoAwesome
                                    VoiceType.CLONED -> Icons.Default.Mic
                                },
                                contentDescription = voice.displayName,
                                tint = if (isSelected) NeonCyan else TextSecondary,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = voice.hindiName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NeonCyan else TextPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            if (selectedVoiceType == VoiceType.CLONED) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateToVoiceClone() }
                        .border(1.dp, NeonAmber.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    color = NeonAmber.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Voice Clone",
                            tint = NeonAmber
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "सक्रिय मॉडल: ${activeVoiceProfile?.name ?: "मेरी आवाज़"}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = NeonAmber
                            )
                            Text(
                                text = "नई आवाज़ रिकॉर्ड या कैलिब्रेट करने के लिए यहाँ टैप करें ➔",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Language Selector
            Text(
                text = "३. भाषा चुनें (Song Language)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SongLanguage.values().forEach { lang ->
                    val isSelected = selectedLanguage == lang
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectedLanguage.value = lang },
                        label = { Text(text = "${lang.hindiLabel} (${lang.label})") },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
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
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Music Genre & Scale
            Text(
                text = "४. संगीत विधा व राग (Genre & Mood)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MusicGenre.values().forEach { genre ->
                    val isSelected = selectedGenre == genre
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectedGenre.value = genre },
                        label = { Text(text = "${genre.hindiTitle} • ${genre.scaleName}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue.copy(alpha = 0.25f),
                            selectedLabelColor = NeonCyan,
                            containerColor = StudioSurface,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) NeonCyan else StudioCardBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Musical Instruments
            Text(
                text = "५. संगीत वाद्ययंत्र (Instruments & Beats)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                availableInstruments.forEach { inst ->
                    val isSelected = selectedInstruments.contains(inst)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.toggleInstrument(inst) },
                        label = { Text(text = inst) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonAmber.copy(alpha = 0.25f),
                            selectedLabelColor = NeonAmber,
                            containerColor = StudioSurface,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) NeonAmber else StudioCardBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Primary Generation Action
            Button(
                onClick = { viewModel.generateSong() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .testTag("generate_song_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(GradientStart, GradientMiddle, GradientEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Create Song",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "गाना तैयार करें (Create AI Music)",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom bar
        }

        // Generating Modal Dialog
        if (isGenerating) {
            Dialog(onDismissRequest = {}) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(NeonMagenta.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = NeonCyan,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(50.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "AI MADHUR SANGEET STUDIO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = NeonAmber
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "गाना बन रहा है...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = generationStatus,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        VisualizerView(
                            amplitudes = List(24) { (0.3f + 0.6f * Math.random()).toFloat() },
                            isPlaying = true,
                            modifier = Modifier.fillMaxWidth(),
                            height = 50.dp
                        )
                    }
                }
            }
        }
    }
}
