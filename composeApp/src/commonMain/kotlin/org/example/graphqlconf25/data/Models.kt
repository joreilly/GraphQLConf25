package org.example.graphqlconf25.data

data class Session(
    val id: String,
    val title: String,
    val description: String,
    val start: String,
    val end: String,
    val eventType: String,
    val eventSubtype: String,
    val venue: String?,
    val speakers: List<Speaker>
)

data class Speaker(
    val username: String,
    val name: String,
    val company: String,
    val position: String,
    val about: String,
    val location: String,
    val url: String,
    val avatar: String,
    val years: List<Int>
)