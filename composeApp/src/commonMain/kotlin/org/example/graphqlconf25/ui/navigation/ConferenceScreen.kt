package org.example.graphqlconf25.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.graphqlconf25.ui.ConferenceViewModel
import org.example.graphqlconf25.ui.SessionDetailsViewModel
import org.example.graphqlconf25.ui.SpeakerDetailsViewModel
import org.example.graphqlconf25.ui.screens.ConferenceScreen

class ConferenceListScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = remember { ConferenceViewModel() }
        val uiState by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        
        ConferenceScreen(
            uiState = uiState,
            onRefresh = viewModel::refreshData,
            onSessionClick = { sessionId ->
                navigator.push(SessionDetailsScreen(sessionId))
            },
            onSpeakerClick = { username ->
                navigator.push(SpeakerDetailsScreen(username))
            }
        )
    }
}

data class SessionDetailsScreen(val sessionId: String) : Screen {
    @Composable
    override fun Content() {
        val viewModel = remember { SessionDetailsViewModel(sessionId) }
        val uiState by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        
        org.example.graphqlconf25.ui.screens.SessionDetailsScreen(
            uiState = uiState,
            onRefresh = viewModel::refreshData,
            onSpeakerClick = { username ->
                navigator.push(SpeakerDetailsScreen(username))
            },
            onBackClick = {
                navigator.pop()
            }
        )
    }
}

data class SpeakerDetailsScreen(val username: String) : Screen {
    @Composable
    override fun Content() {
        val viewModel = remember { SpeakerDetailsViewModel(username) }
        val uiState by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        
        org.example.graphqlconf25.ui.screens.SpeakerDetailsScreen(
            uiState = uiState,
            onRefresh = viewModel::refreshData,
            onBackClick = {
                navigator.pop()
            },
            onSessionClick = { sessionId ->
                navigator.push(SessionDetailsScreen(sessionId))
            }
        )
    }
}