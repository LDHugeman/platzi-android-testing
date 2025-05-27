package com.juandgaines.testground.domain

import com.google.common.truth.Truth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CoroutineTestExample {

    private suspend fun delayOperation(): Int {
        delay(1000)
        return 42
    }

    //runBlocking ejecuta los delay
    //runTest no ejecuta los delay

    @Test
    fun givenDelayOperation_whenUsingRunBlocking_thenWaitsForRealDelay(): Unit = runBlocking {
        val result = delayOperation()
        Truth.assertThat(result).isEqualTo(42)
    }

    @Test
    fun givenDelayOperation_whenUsingRunTest_thenWaitsForRealDelay(): Unit = runTest {
        val result = delayOperation()
        Truth.assertThat(result).isEqualTo(42)
    }

    private fun numberFlow() = flow {
        emit(1)
        delay(1000)
        emit(2)
        delay(1000)
        emit(3)
    }

    @Test
    fun flowWithoutTimeControl() = runBlocking {
        val numbers = mutableListOf<Int>()
        numberFlow().collect {
            numbers.add(it)
        }
        Truth.assertThat(numbers).isEqualTo(listOf(1, 2, 3))
    }

    @Test
    fun flowWithTimeControl() = runTest {
        val numbers = mutableListOf<Int>()
        val job = launch {
            numberFlow().collect {
                numbers.add(it)
            }
        }
        advanceTimeBy(500)
        Truth.assertThat(numbers).isEqualTo(listOf(1))

        advanceTimeBy(600)
        Truth.assertThat(numbers).isEqualTo(listOf(1, 2))

        advanceTimeBy(1000)
        Truth.assertThat(numbers).isEqualTo(listOf(1, 2, 3))
    }
}