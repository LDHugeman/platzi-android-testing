package com.juandgaines.testground.domain

import com.juandgaines.testground.data.UserApi
import java.util.UUID

class UserFakeApi: UserApi {

    var users = (1..10).map {
        User(
            id = it.toString(),
            username = "User$it"
        )
    }

    var places = (1..10).map {
        Place(
            id = UUID.randomUUID().toString(),
            name = "Place$it",
            coordinates = Coordinates(
                latitude = it.toDouble(),
                longitude = it.toDouble()
            )
        )
    }

    override suspend fun getUser(userId: String): User {
        return users.find { it.id == userId } ?: throw Exception("User not found")
    }

    override suspend fun getPlaces(userId: String): List<Place> {
        return places.find { it.id == userId }?.let { listOf(it) } ?: emptyList()
    }

    override suspend fun getProfile(userId: String): Profile {
        val user = getUser(userId)
        return Profile(
            user = user,
            places = getPlaces(userId)
        )
    }
}