package com.juandgaines.testground

import com.google.common.truth.Truth
import com.juandgaines.testground.domain.Coordinates
import com.juandgaines.testground.domain.ExperienceCalculator
import com.juandgaines.testground.domain.Place
import org.junit.Before
import org.junit.Test

class ExperienceCalculatorTest {
    private lateinit var experienceCalculator: ExperienceCalculator

    @Before
    fun setup() {
        experienceCalculator = ExperienceCalculator()
    }

    @Test
    fun givenTouristSpot_whenCalculatorExperience_thenReturns5Points() {
        // Given
        val touristSpot = Place(
            id = "1",
            name = "Times Square",
            coordinates = Coordinates(40.7, -73.5)
        )

        // Act
        val result = experienceCalculator.calculateExperience(listOf(touristSpot))

        // Assert
        Truth.assertThat(result).isEqualTo(5)
    }

    @Test
    fun givenCulturalPlace_whenCalculatorExperience_thenReturns4Points() {
        // Given
        val culturalPlace = Place(
            id = "1",
            name = "Smithsonian Museum",
            coordinates = Coordinates(38.5, -76.5)
        )

        // Act
        val result = experienceCalculator.calculateExperience(listOf(culturalPlace))

        // Assert
        Truth.assertThat(result).isEqualTo(4)
    }

    @Test
    fun givenMultiplePlaces_whenCalculatorExperience_thenReturnsSumOfScores() {
        // Given
        val places = listOf(
            Place("1", "Tourist Spot", Coordinates(40.5, -73.5)), // 5 points
            Place("2", "Cultural Place", Coordinates(38.5, -76.5)), // 4 points
            Place("3", "Unknown Place", Coordinates(0.0, 0.0)) // 1 point
        )

        // Act
        val result = experienceCalculator.calculateExperience(places)

        // Assert
        Truth.assertThat(result).isEqualTo(10)
    }

    @Test
    fun givenEmptyList_whenCalculatorExperience_thenReturnsZero() {
        // Act
        val result = experienceCalculator.calculateExperience(emptyList())

        // Assert
        Truth.assertThat(result).isEqualTo(0)
    }
}