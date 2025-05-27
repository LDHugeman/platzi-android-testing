package com.juandgaines.testground.presentation

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth
import com.juandgaines.testground.util.MainDispatcherRule
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {

    @get:Rule
    val testDispatchers = MainDispatcherRule(
        StandardTestDispatcher()
    )

    private lateinit var viewModel: ProfileViewModel
    private lateinit var repository: UserRepositoryFake

    @Before
    fun setUp() {
        repository = UserRepositoryFake()

        viewModel = ProfileViewModel(
            repository = repository,
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(
                    "userId" to repository.profileToReturn.user.id
                )
            )
        )
    }

    @Test
    fun givenValidUserId_whenLoadProfile_thenProfileIsLoaded() = runTest {
        // Act
        viewModel.loadProfile()
        advanceUntilIdle()
        // Assert
        Truth.assertThat(viewModel.state.value.profile).isEqualTo(repository.profileToReturn)
        Truth.assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun givenRepositoryError_whenLoadProfile_thenErrorStateIsSet() = runTest {
        // Arrange
        repository.errorToReturn = Exception("Test exception")

        // Act
        viewModel.loadProfile()
        advanceUntilIdle()
        // Assert
        Truth.assertThat(viewModel.state.value.profile).isNull()
        Truth.assertThat(viewModel.state.value.errorMessage).isEqualTo("Test exception")
        Truth.assertThat(viewModel.state.value.isLoading).isFalse()
    }
}