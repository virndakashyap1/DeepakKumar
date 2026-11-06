package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.LibraryScreen
import com.example.ui.PlayerScreen
import com.example.ui.StudioScreen
import com.example.ui.VoiceCloneScreen
import com.example.ui.components.MiniPlayerBar
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ScreenTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentSong by viewModel.playerEngine.currentSong.collectAsState()
    val isPlaying by viewModel.playerEngine.isPlaying.collectAsState()
    val currentPos by viewModel.playerEngine.currentPositionMs.collectAsState()
    val duration by viewModel.playerEngine.durationMs.collectAsState()

    // Handle back press to return to Studio tab
    BackHandler(enabled = currentTab != ScreenTab.STUDIO) {
        viewModel.selectTab(ScreenTab.STUDIO)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = StudioBackground,
        bottomBar = {
            Column(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Show MiniPlayer if not currently on Player screen and a song is loaded
                if (currentTab != ScreenTab.PLAYER && currentSong != null) {
                    MiniPlayerBar(
                        song = currentSong,
                        isPlaying = isPlaying,
                        currentPositionMs = currentPos,
                        durationMs = duration,
                        onTogglePlayPause = { viewModel.playerEngine.togglePlayPause() },
                        onExpandPlayer = { viewModel.selectTab(ScreenTab.PLAYER) }
                    )
                }

                NavigationBar(
                    containerColor = StudioSurface,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.STUDIO,
                        onClick = { viewModel.selectTab(ScreenTab.STUDIO) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.STUDIO) Icons.Filled.MusicNote else Icons.Outlined.MusicNote,
                                contentDescription = "Studio"
                            )
                        },
                        label = {
                            Text(
                                text = "स्टूडियो",
                                fontWeight = if (currentTab == ScreenTab.STUDIO) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = StudioBackground,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_item_studio")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.VOICE_CLONE,
                        onClick = { viewModel.selectTab(ScreenTab.VOICE_CLONE) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.VOICE_CLONE) Icons.Filled.Mic else Icons.Outlined.Mic,
                                contentDescription = "Voice Clone"
                            )
                        },
                        label = {
                            Text(
                                text = "आवाज़ क्लोन",
                                fontWeight = if (currentTab == ScreenTab.VOICE_CLONE) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = StudioBackground,
                            selectedTextColor = NeonMagenta,
                            indicatorColor = NeonMagenta,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_item_voice_clone")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.PLAYER,
                        onClick = { viewModel.selectTab(ScreenTab.PLAYER) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.PLAYER) Icons.Filled.Radio else Icons.Outlined.Radio,
                                contentDescription = "Player"
                            )
                        },
                        label = {
                            Text(
                                text = "प्लेयर",
                                fontWeight = if (currentTab == ScreenTab.PLAYER) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = StudioBackground,
                            selectedTextColor = ElectricBlue,
                            indicatorColor = ElectricBlue,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_item_player")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.LIBRARY,
                        onClick = { viewModel.selectTab(ScreenTab.LIBRARY) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.LIBRARY) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic,
                                contentDescription = "Library"
                            )
                        },
                        label = {
                            Text(
                                text = "लाइब्रेरी",
                                fontWeight = if (currentTab == ScreenTab.LIBRARY) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = StudioBackground,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_item_library")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentTab,
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    ScreenTab.STUDIO -> StudioScreen(
                        viewModel = viewModel,
                        onNavigateToVoiceClone = { viewModel.selectTab(ScreenTab.VOICE_CLONE) }
                    )
                    ScreenTab.VOICE_CLONE -> VoiceCloneScreen(
                        viewModel = viewModel
                    )
                    ScreenTab.PLAYER -> PlayerScreen(
                        viewModel = viewModel
                    )
                    ScreenTab.LIBRARY -> LibraryScreen(
                        viewModel = viewModel,
                        onNavigateToStudio = { viewModel.selectTab(ScreenTab.STUDIO) }
                    )
                }
            }
        }
    }
}
