package org.example.graphqlconf25.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A placeholder for images that will be replaced with Coil3 in the future.
 * This is a temporary solution until we can properly integrate Coil3.
 */
@Composable
fun PlaceholderImage(
    url: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    isCircular: Boolean = true,
    initials: String? = null
) {
    val actualModifier = if (isCircular) {
        modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
    } else {
        modifier
            .size(size)
            .background(MaterialTheme.colorScheme.primaryContainer)
    }
    
    Box(
        modifier = actualModifier,
        contentAlignment = Alignment.Center
    ) {
        // Display initials if provided, otherwise show a generic icon
        if (initials != null) {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            Text(
                text = "👤",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}