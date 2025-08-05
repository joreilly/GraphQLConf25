package org.example.graphqlconf25

import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.example.graphqlconf25.ui.navigation.ConferenceListScreen
import org.example.graphqlconf25.ui.theme.ConferenceTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    // Coil3 will be initialized in platform-specific code
    
    ConferenceTheme {
        Navigator(ConferenceListScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}