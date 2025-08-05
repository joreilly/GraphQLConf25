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

class ConferenceViewModel(
    private val repository: ConferenceRepository = ConferenceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConferenceUiState())
    val uiState: StateFlow<ConferenceUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val sessions = repository.getSessions()
                val speakers = repository.getSpeakers()
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        sessions = sessions,
                        speakers = speakers,
                        error = null
                    )
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
        loadData()
    }
}

data class ConferenceUiState(
    val isLoading: Boolean = false,
    val sessions: List<Session> = emptyList(),
    val speakers: List<Speaker> = emptyList(),
    val error: String? = null
)