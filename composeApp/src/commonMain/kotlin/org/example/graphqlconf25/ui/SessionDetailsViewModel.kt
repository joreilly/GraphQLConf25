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

class SessionDetailsViewModel(
    private val sessionId: String,
    private val repository: ConferenceRepository = ConferenceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionDetailsUiState())
    val uiState: StateFlow<SessionDetailsUiState> = _uiState.asStateFlow()

    init {
        loadSession()
    }

    private fun loadSession() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val session = repository.getSessionById(sessionId)
                
                if (session != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            session = session,
                            error = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Session not found"
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
        loadSession()
    }
}

data class SessionDetailsUiState(
    val isLoading: Boolean = false,
    val session: Session? = null,
    val error: String? = null
)