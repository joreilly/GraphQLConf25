package org.example.graphqlconf25.data

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.normalizedCache
import org.example.graphqlconf25.GetSessionDetailsQuery
import org.example.graphqlconf25.GetSessionsQuery
import org.example.graphqlconf25.GetSpeakerDetailsQuery
import org.example.graphqlconf25.GetSpeakersQuery

class ConferenceRepository {
    private val apolloClient = ApolloClient.Builder()
        .serverUrl("https://graphqlconf.app/graphql")
        .normalizedCache(MemoryCacheFactory(maxSizeBytes = 10 * 1024 * 1024))
        .build()

    suspend fun getSessions(): List<Session> {
        val response = apolloClient.query(GetSessionsQuery()).execute()
        return response.data?.sessions?.map { it.toSession() } ?: emptyList()
    }

    suspend fun getSessionById(id: String): Session? {
        val response = apolloClient.query(GetSessionDetailsQuery()).execute()
        return response.data?.sessions?.find { it.id == id }?.toSession()
    }

    suspend fun getSpeakers(): List<Speaker> {
        val response = apolloClient.query(GetSpeakersQuery()).execute()
        return response.data?.speakers?.map { it.toSpeaker() } ?: emptyList()
    }

    suspend fun getSpeakerByUsername(username: String): Speaker? {
        val response = apolloClient.query(GetSpeakerDetailsQuery()).execute()
        return response.data?.speakers?.find { it.username == username }?.toSpeaker()
    }
    
    suspend fun getSessionsForSpeaker(username: String): List<Session> {
        val response = apolloClient.query(GetSessionDetailsQuery()).execute()
        return response.data?.sessions?.filter { session ->
            session.speakers.any { it.username == username }
        }?.map { it.toSession() } ?: emptyList()
    }

    suspend fun getTimezone(): String {
        // This is a placeholder. In a real app, we would fetch the timezone from the API.
        return "UTC"
    }
    
    // Extension functions to map GraphQL types to our data models
    private fun GetSessionsQuery.Session.toSession(): Session {
        return Session(
            id = id,
            title = title,
            description = description,
            start = start.toString(),
            end = end.toString(),
            eventType = event_type,
            eventSubtype = event_subtype,
            venue = venue,
            speakers = speakers.map { it.toSpeaker() }
        )
    }
    
    private fun GetSessionDetailsQuery.Session.toSession(): Session {
        return Session(
            id = id,
            title = title,
            description = description,
            start = start.toString(),
            end = end.toString(),
            eventType = event_type,
            eventSubtype = event_subtype,
            venue = venue,
            speakers = speakers.map { it.toSpeaker() }
        )
    }
    
    private fun GetSessionsQuery.Speaker.toSpeaker(): Speaker {
        return Speaker(
            username = username,
            name = name,
            company = "",  // Not available in this query
            position = "",  // Not available in this query
            about = "",  // Not available in this query
            location = "",  // Not available in this query
            url = "",  // Not available in this query
            avatar = avatar,
            years = emptyList()  // Not available in this query
        )
    }
    
    private fun GetSessionDetailsQuery.Speaker.toSpeaker(): Speaker {
        return Speaker(
            username = username,
            name = name,
            company = company,
            position = position,
            about = about,
            location = location,
            url = url,
            avatar = avatar,
            years = emptyList()  // Not available in this query
        )
    }
    
    private fun GetSpeakersQuery.Speaker.toSpeaker(): Speaker {
        return Speaker(
            username = username,
            name = name,
            company = company,
            position = position,
            about = "",  // Not available in this query
            location = "",  // Not available in this query
            url = "",  // Not available in this query
            avatar = avatar,
            years = emptyList()  // Not available in this query
        )
    }
    
    private fun GetSpeakerDetailsQuery.Speaker.toSpeaker(): Speaker {
        return Speaker(
            username = username,
            name = name,
            company = company,
            position = position,
            about = about,
            location = location,
            url = url,
            avatar = avatar,
            years = years
        )
    }
}