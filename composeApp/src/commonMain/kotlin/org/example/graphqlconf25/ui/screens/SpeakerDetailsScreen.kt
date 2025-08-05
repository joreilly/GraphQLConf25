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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.example.graphqlconf25.data.Session
import org.example.graphqlconf25.data.Speaker
import org.example.graphqlconf25.ui.SpeakerDetailsUiState
import org.example.graphqlconf25.ui.components.ConferenceTopAppBar
import org.example.graphqlconf25.ui.components.ErrorMessage
import org.example.graphqlconf25.ui.components.FullScreenLoading
import org.example.graphqlconf25.ui.components.NetworkImage
import org.example.graphqlconf25.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakerDetailsScreen(
    uiState: SpeakerDetailsUiState,
    onRefresh: () -> Unit,
    onBackClick: () -> Unit,
    onSessionClick: (String) -> Unit = {}  // Default empty implementation
) {
    Scaffold(
        topBar = {
            ConferenceTopAppBar(
                title = uiState.speaker?.name ?: "Speaker Details",
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
                uiState.speaker != null -> {
                    SpeakerDetails(
                        speaker = uiState.speaker,
                        sessions = uiState.sessions,
                        onSessionClick = onSessionClick
                    )
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

@Composable
fun SpeakerDetails(
    speaker: Speaker,
    sessions: List<Session> = emptyList(),
    onSessionClick: (String) -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Enhanced profile image with background and styling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ) { }
            
            // Profile image with border
            Surface(
                modifier = Modifier.size(140.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(4.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                NetworkImage(
                    url = speaker.avatar,
                    modifier = Modifier.clip(CircleShape),
                    size = 140.dp,
                    contentDescription = "${speaker.name}'s avatar"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Speaker name with enhanced styling
        Text(
            text = speaker.name,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Position and company with visual emphasis
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = speaker.position,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            
            if (speaker.company.isNotEmpty()) {
                Text(
                    text = " at ${speaker.company}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Speaker info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Location
                if (speaker.location.isNotEmpty() && speaker.location != "null") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "📍 Location: ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = speaker.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Website with clickable URL
                if (speaker.url.isNotEmpty() && speaker.url != "null") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🌐 Website: ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = speaker.url,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier.clickable {
                                try {
                                    uriHandler.openUri(speaker.url)
                                } catch (e: Exception) {
                                    // Handle error opening URL
                                }
                            }
                        )
                    }
                }
            }
        }
        
        // About section
        SectionHeader(title = "About")
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = speaker.about,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        // Years attended section
        if (speaker.years.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            
            SectionHeader(title = "Years Attended")
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Enhanced years display with wrapping Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Use a simple Row with wrapping for years
                // In a real app, we would use a FlowRow or LazyVerticalGrid
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    speaker.years.forEach { year ->
                        Surface(
                            modifier = Modifier.padding(4.dp),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = year.toString(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
        
        // Sessions section
        if (sessions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            
            SectionHeader(title = "Sessions")
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sessions.forEach { session ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSessionClick(session.id) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Session type indicator
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Surface(
                                    color = when (session.eventType.lowercase()) {
                                        "talk" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                        "workshop" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                                        else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                                    },
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = session.eventType.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                
                                // Add subtype if available
                                if (session.eventSubtype.isNotEmpty() && session.eventSubtype != "null") {
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text(
                                            text = session.eventSubtype.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                            
                            // Session title
                            Text(
                                text = session.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Format date/time more clearly
                            val formattedTime = formatSessionTime(session.start, session.end)
                            
                            Text(
                                text = "📅 $formattedTime",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            if (session.venue != null && session.venue.isNotEmpty() && session.venue != "null") {
                                Text(
                                    text = "📍 ${session.venue}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}