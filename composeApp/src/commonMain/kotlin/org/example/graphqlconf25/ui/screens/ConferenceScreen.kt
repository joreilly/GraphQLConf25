package org.example.graphqlconf25.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.graphqlconf25.data.Session
import org.example.graphqlconf25.data.Speaker
import org.example.graphqlconf25.ui.ConferenceUiState
import org.example.graphqlconf25.ui.components.ErrorMessage
import org.example.graphqlconf25.ui.components.FullScreenLoading
import org.example.graphqlconf25.ui.components.NetworkImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConferenceScreen(
    uiState: ConferenceUiState,
    onRefresh: () -> Unit,
    onSessionClick: (String) -> Unit,
    onSpeakerClick: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Sessions", "Speakers")
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "GraphQL Conference",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Custom styled TabRow
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        height = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    )
                }
            }
            
            // Content with animations
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
                else -> {
                    Box(modifier = Modifier.weight(1f)) {
                        // Simple crossfade animation between tabs
                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) togetherWith 
                                fadeOut(animationSpec = tween(300))
                            }
                        ) { tabIndex ->
                            when (tabIndex) {
                                0 -> SessionsList(uiState.sessions, onSessionClick, onSpeakerClick)
                                1 -> SpeakersList(uiState.speakers, onSpeakerClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SessionsList(
    sessions: List<Session>,
    onSessionClick: (String) -> Unit,
    onSpeakerClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(sessions) { session ->
            SessionItem(session, onSessionClick, onSpeakerClick)
        }
    }
}

@Composable
fun SessionItem(
    session: Session,
    onSessionClick: (String) -> Unit,
    onSpeakerClick: (String) -> Unit
) {
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
            
            // Session description
            Text(
                text = session.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Time and venue information
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Format date/time more clearly
                val formattedTime = formatSessionTime(session.start, session.end)
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (session.venue != null && session.venue.isNotEmpty() && session.venue != "null") {
                        Text(
                            text = "Venue: ${session.venue}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Speakers section with avatars
            if (session.speakers.isNotEmpty()) {
                Text(
                    text = "Speakers",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Display speakers in a row with avatars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    session.speakers.forEach { speaker ->
                        Row(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .clickable { onSpeakerClick(speaker.username) }
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NetworkImage(
                                url = speaker.avatar,
                                modifier = Modifier.clip(CircleShape),
                                size = 24.dp,
                                contentDescription = "${speaker.name}'s avatar"
                            )
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            Text(
                                text = speaker.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
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

@Composable
fun SpeakersList(
    speakers: List<Speaker>,
    onSpeakerClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(speakers) { speaker ->
            SpeakerItem(speaker, onSpeakerClick)
        }
    }
}

@Composable
fun SpeakerItem(
    speaker: Speaker,
    onSpeakerClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(end = 16.dp)
            ) {
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
            }
            
            // Speaker information with better organization
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Speaker name
                Text(
                    text = speaker.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Position and company with visual emphasis
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
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Additional information - location
                if (speaker.location.isNotEmpty() && speaker.location != "null") {
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