package com.juandgaines.testground.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.juandgaines.testground.domain.Coordinates
import com.juandgaines.testground.domain.Place
import com.juandgaines.testground.domain.Profile
import com.juandgaines.testground.domain.User
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun profileScreen_whenProfileLoaded_showsUserAndPlaces() {
        // Given
        val state = previewProfileState()

        // Act
        composeRule.setContent {
            MaterialTheme {
                ProfileScreen(
                    state = state,
                    onPlaceClick = {}
                )
            }
        }

        // Then
        composeRule.onNodeWithText("Welcome Test User!").isDisplayed()
        composeRule.onNodeWithText("Place 1").isDisplayed()
        composeRule.onNodeWithText("Lat: 1.0").isDisplayed()
        composeRule.onNodeWithText("Long: 1.0").isDisplayed()
        Thread.sleep(2000)
    }

    fun previewProfileState() = ProfileState(
        Profile(
            user = User(
                id = "test-user",
                username = "Test User"
            ),
            places = listOf(
                Place(
                    id = "1",
                    name = "Place 1",
                    coordinates = Coordinates(
                        latitude = 1.0,
                        longitude = 1.0
                    )
                ),
                Place(
                    id = "2",
                    name = "Place 2",
                    coordinates = Coordinates(
                        latitude = 2.0,
                        longitude = 2.0
                    )
                )
            )
        )
    )

    @Test
    fun profileScreen_whenLoading_showsLoadingIndicator() {
        // Act
        composeRule.setContent {
            MaterialTheme {
                ProfileScreen(
                    state = ProfileState(
                        isLoading = true
                    ),
                    onPlaceClick = {}
                )
            }
        }

        // Assert loading state using semantics
        composeRule.onNodeWithContentDescription("Loading Profile").assertIsDisplayed()
    }

    @Test
    fun profileScreen_whenError_showsErrorMessage() {
        // Given
        val errorMessage = "Error loading profile"

        // Act
        composeRule.setContent {
            MaterialTheme {
                ProfileScreen(
                    state = ProfileState(
                        errorMessage = errorMessage
                    ),
                    onPlaceClick = {}
                )
            }
        }

        // Assert
        composeRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }
}