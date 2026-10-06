package com.loguscore.speech.core.event

import com.loguscore.speech.core.error.SpeechError
import com.loguscore.speech.core.fake.FakeAudioSegment
import com.loguscore.speech.core.model.RecognitionResult
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpeechStateAndEventTest {

    @Test
    fun `SpeechState supports exhaustive pattern matching`() {
        val states: List<SpeechState> = listOf(
            SpeechState.Idle,
            SpeechState.Initializing,
            SpeechState.Listening,
            SpeechState.Recording,
            SpeechState.Stopping,
            SpeechState.Stopped,
            SpeechState.Error(SpeechError.PermissionDenied("RECORD_AUDIO"))
        )

        val names = states.map { state ->
            when (state) {
                is SpeechState.Idle -> "idle"
                is SpeechState.Initializing -> "init"
                is SpeechState.Listening -> "listen"
                is SpeechState.Recording -> "record"
                is SpeechState.Stopping -> "stopping"
                is SpeechState.Stopped -> "stopped"
                is SpeechState.Error -> "error: ${state.error.message}"
            }
        }

        assertEquals(7, names.size)
        assertTrue(names.last().contains("RECORD_AUDIO"))
    }

    @Test
    fun `SpeechEvent hierarchy instantiate expected payload types`() {
        val audio = FakeAudioSegment()
        val result = RecognitionResult(text = "sample text")
        val error = SpeechError.NetworkUnavailable()

        val events: List<SpeechEvent> = listOf(
            SpeechEvent.RecordingStarted,
            SpeechEvent.SpeechStarted,
            SpeechEvent.SpeechSegmentCompleted("seg-1", audio),
            SpeechEvent.TranscriptionStarted("seg-1", "fake-provider"),
            SpeechEvent.TranscriptionCompleted("seg-1", result),
            SpeechEvent.ErrorOccurred(error)
        )

        assertEquals(6, events.size)

        val lastEvent = events.last()
        assertTrue(lastEvent is SpeechEvent.ErrorOccurred)
        assertEquals("Network connection is required for remote provider execution.", lastEvent.error.message)
    }

    @Test
    fun `SpeechError types preserve messages and causes`() {
        val cause = RuntimeException("I/O failure")
        val recordError = SpeechError.AudioRecordFailed(message = "Mic hardware busy", cause = cause)

        assertEquals("Mic hardware busy", recordError.message)
        assertEquals(cause, recordError.cause)

        val providerError = SpeechError.ProviderNotFound("No engine supports cs-CZ")
        assertEquals("No engine supports cs-CZ", providerError.message)
    }
}
