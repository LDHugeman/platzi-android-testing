package com.juandgaines.testground.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import com.google.common.truth.Truth
import com.juandgaines.testground.domain.Coordinates
import com.juandgaines.testground.domain.Place
import com.juandgaines.testground.domain.Profile
import com.juandgaines.testground.domain.User
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var testPlace: Place
    private lateinit var testProfile: Profile

    @Before
    fun setUp() {
        testPlace = Place(
            id = "1",
            name = "Test Place",
            coordinates = Coordinates(
                latitude = 1.0,
                longitude = 1.0
            )
        )

        testProfile = Profile(
            user = User(
                id = "test-user",
                username = "Test User"
            ),
            places = listOf(testPlace)
        )
    }

    @Test
    fun navigationStateIsCorrect_whenNavigationToDetailScreen() {
        composeRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            MaterialTheme {
                NavHost(
                    navController = navController,
                    startDestination = "profile"
                ) {
                    composable("profile") {
                        ProfileScreen(
                            state = ProfileState(
                                profile = testProfile,
                            ),
                            onPlaceClick = { place ->
                                navController.navigate("detail/${place.id}")
                            }
                        )
                    }
                    composable("detail/{placeId}") { backStackEntry ->
                        val placeId = backStackEntry.arguments?.getString("placeId")
                        val place = testProfile.places.find { it.id == placeId }

                        DetailPlaceScreen(
                            place = place!!,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }

        // Act
        composeRule.onNodeWithContentDescription("Place card: Test Place").performClick()
        composeRule.waitForIdle()
        Thread.sleep(1000)
        Truth.assertThat(navController.currentDestination?.route).contains("detail")

        composeRule.onNodeWithContentDescription("Navigate back").performClick()
        composeRule.waitForIdle()
        Thread.sleep(1000)
        Truth.assertThat(navController.currentDestination?.route).isEqualTo("profile")
    }
}