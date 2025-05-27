package com.juandgaines.testground.domain

import com.google.common.truth.Truth
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.juandgaines.testground.data.UserApi
import com.juandgaines.testground.data.UserRepositoryImpl
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit

class UserRepositoryTest {
    private lateinit var userRepository: UserRepositoryImpl
    private lateinit var api: UserFakeApi
    private lateinit var mockWebServer: MockWebServer
    private lateinit var mockApi: UserApi

    @Before
    fun setUp() {
        api = UserFakeApi()
        userRepository = UserRepositoryImpl(api)

        val contentType = "application/json".toMediaType()
        mockWebServer = MockWebServer()
        mockApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(UserApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun givenValidUserId_whenGetProfileWithMockWebServer_thenReturnsProfile() = runTest {
        // Given
        val user = User(id = "1", username = "test-user")
        val places = listOf(
            Place(
                id = "1",
                name = "Test Place 1",
                coordinates = Coordinates(1.0, 1.0)
            ),
            Place(
                id = "2",
                name = "Test Place 2",
                coordinates = Coordinates(2.0, 2.0)
            ),
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                        {
                            "id":"1",
                            "username":"test-user"
                        }
                    """.trimIndent()
                )
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                        [
                            {
                                "id":"1",
                                "name":"Test Place 1",
                                "coordinates":{
                                    "latitude":1.0,
                                    "longitude":1.0
                                }
                            },
                            {
                                "id":"2",
                                "name":"Test Place 2",
                                "coordinates":{
                                    "latitude":2.0,
                                    "longitude":2.0
                                }
                            }
                        ]
                    """.trimIndent()
                )
        )

        // Act
        val repository = UserRepositoryImpl(mockApi)

        val result = repository.getProfile("1")

        // Assert
        Truth.assertThat(result.isSuccess).isTrue()
        Truth.assertThat(result.getOrThrow().user).isEqualTo(user)
        Truth.assertThat(result.getOrThrow().places).isEqualTo(places)
    }

    @Test
    fun givenInvalidUserId_whenGetProfile_thenReturnsError() = runTest {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody(
                    """
                        {
                            "error": "User not found",
                            "status": "404"
                        }
                    """.trimIndent()
                )
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody(
                    """
                        {
                            "error": "Places not found",
                            "status": "404"
                        }
                    """.trimIndent()
                )
        )

        // Act
        val repository = UserRepositoryImpl(mockApi)
        val result = repository.getProfile("invalid-id")

        // Assert
        Truth.assertThat(result.isFailure).isTrue()
        Truth.assertThat(result.exceptionOrNull()).isInstanceOf(HttpException::class.java)
    }

    @Test
    fun givenValidUserId_whenGetProfileWithFakeApi_thenReturnsProfile() = runTest {
        // Given
        val userId = "1"
        //Act
        val profileResult = userRepository.getProfile("1")

        // Assert
        Truth.assertThat(profileResult.isSuccess).isTrue()
        Truth.assertThat(profileResult.getOrThrow().user.id).isEqualTo("1")

        val expectedPlaces = api.places.filter { it.id == "1" }
        Truth.assertThat(profileResult.getOrThrow().places).isEqualTo(expectedPlaces)
    }
}