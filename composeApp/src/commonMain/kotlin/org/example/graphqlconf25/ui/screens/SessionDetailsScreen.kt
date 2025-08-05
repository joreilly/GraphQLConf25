package org.example.graphqlconf25.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.graphqlconf25.data.Session
import org.example.graphqlconf25.ui.SessionDetailsUiState
import org.example.graphqlconf25.ui.components.ConferenceTopAppBar
import org.example.graphqlconf25.ui.components.ErrorMessage
import org.example.graphqlconf25.ui.components.FullScreenLoading
import org.example.graphqlconf25.ui.components.NetworkImage
import org.example.graphqlconf25.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailsScreen(
    uiState: SessionDetailsUiState,
    onRefresh: () -> Unit,
    onSpeakerClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            ConferenceTopAppBar(
                title = uiState.session?.title ?: "Session Details",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    FullScreenLoading()
                }
                uiState.error != null -> {
                    ErrorMessage(
                        message = uiState.error,
                        onRetry = onRefresh
                    )
                }
                uiState.session != null -> {
                    SessionDetails(uiState.session, onSpeakerClick)
                }
            }
        }
    }
}

@Composable
fun SessionDetails(
    session: Session,
    onSpeakerClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Session title with enhanced styling
        Text(
            text = session.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Session type and subtype indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = when (session.eventType.lowercase()) {
                    "talk" -> MaterialTheme.colorScheme.primary
                    "workshop" -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.secondary
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = session.eventType.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            
            if (session.eventSubtype.isNotEmpty() && session.eventSubtype != "null") {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = session.eventSubtype.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Time and venue information in a card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Format date/time more clearly
                val formattedTime = formatSessionTime(session.start, session.end)
                
                Text(
                    text = "📅 $formattedTime",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (session.venue != null && session.venue.isNotEmpty() && session.venue != "null") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📍 ${session.venue}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Description section
        SectionHeader(title = "Description")
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = session.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Speakers section
        SectionHeader(title = "Speakers")
        
        Spacer(modifier = Modifier.height(12.dp))
        
        session.speakers.forEach { speaker ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onSpeakerClick(speaker.username) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Enhanced speaker avatar with border
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    ) {
                        NetworkImage(
                            url = speaker.avatar,
                            modifier = Modifier.clip(CircleShape),
                            size = 56.dp,
                            contentDescription = "${speaker.name}'s avatar"
                        )
                    }
                    
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = speaker.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = speaker.position,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            
                            if (speaker.company.isNotEmpty()) {
                                Text(
                                    text = " at ${speaker.company}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        if (speaker.location.isNotEmpty() && speaker.location != "null") {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "📍 ${speaker.location}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function to format session time
private fun formatSessionTime(start: String, end: String): String {
    // Simple formatting for now - in a real app, we would parse the dates and format them properly
    val startParts = start.split("T")
    val endParts = end.split("T")
    
    if (startParts.size < 2 || endParts.size < 2) {
        return "$start - $end"
    }
    
    val date = startParts[0]
    val startTime = startParts[1].substringBefore(".")
    val endTime = endParts[1].substringBefore(".")
    
    return "$date, $startTime - $endTime"
}