package org.example.graphqlconf25.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.graphqlconf25.data.ConferenceRepository
import org.example.graphqlconf25.data.Session
import org.example.graphqlconf25.data.Speaker

class SpeakerDetailsViewModel(
    private val username: String,
    private val repository: ConferenceRepository = ConferenceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpeakerDetailsUiState())
    val uiState: StateFlow<SpeakerDetailsUiState> = _uiState.asStateFlow()

    init {
        loadSpeaker()
    }

    private fun loadSpeaker() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val speaker = repository.getSpeakerByUsername(username)
                
                if (speaker != null) {
                    // Load sessions for this speaker
                    val speakerSessions = repository.getSessionsForSpeaker(username)
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            speaker = speaker,
                            sessions = speakerSessions,
                            error = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Speaker not found"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun refreshData() {
        loadSpeaker()
    }
}

data class SpeakerDetailsUiState(
    val isLoading: Boolean = false,
    val speaker: Speaker? = null,
    val sessions: List<Session> = emptyList(),
    val error: String? = null
)