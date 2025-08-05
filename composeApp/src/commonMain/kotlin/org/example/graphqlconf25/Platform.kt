package org.example.graphqlconf25

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform