package com.loguscore.speech.core.fake

import com.loguscore.speech.core.model.RecognitionRequest
import com.loguscore.speech.core.model.RecognitionResult
import com.loguscore.speech.core.model.SpeechLanguage
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class FakeSpeechRecognitionProviderTest {

    @Test
    fun `transcribe returns canned result and records invocation`() = runTest {
        val provider = FakeSpeechRecognitionProvider()
        val audio = FakeAudioSegment()
        val request = RecognitionRequest(language = SpeechLanguage.ENGLISH)

        val result = provider.transcribe(audio, request)

        assertEquals("Simulated transcribed speech", result.text)
        assertEquals(0.98f, result.confidence)
        assertEquals(1, provider.invocations.size)
        assertEquals(audio, provider.invocations[0].audio)
        assertEquals(request, provider.invocations[0].request)
    }

    @Test
    fun `transcribe executes custom handler when provided`() = runTest {
        val provider = FakeSpeechRecognitionProvider()
        val audio = FakeAudioSegment()
        val request = RecognitionRequest(language = SpeechLanguage.CZECH)

        provider.customHandler = { _, req ->
            RecognitionResult(
                text = "Custom result for ${req.language.tag}",
                confidence = 0.99f
            )
        }

        val result = provider.transcribe(audio, request)
        assertEquals("Custom result for cs-CZ", result.text)
        assertEquals(0.99f, result.confidence)
    }

    @Test
    fun `transcribe throws simulated exception when shouldFail is true`() = runTest {
        val provider = FakeSpeechRecognitionProvider()
        provider.shouldFail = true
        provider.failureThrowable = IllegalStateException("Simulated backend crash")

        val audio = FakeAudioSegment()
        val request = RecognitionRequest()

        val exception = assertFailsWith<IllegalStateException> {
            provider.transcribe(audio, request)
        }
        assertEquals("Simulated backend crash", exception.message)
    }

    @Test
    fun `FakeAudioSegment streams data and handles release lifecycle`() {
        val sampleData = byteArrayOf(1, 2, 3, 4)
        val audio = FakeAudioSegment(data = sampleData)

        audio.openStream().use { stream ->
            val readBytes = stream.readBytes()
            assertTrue(sampleData.contentEquals(readBytes))
        }

        audio.release()
        assertTrue(audio.isReleased)

        assertFailsWith<IllegalStateException> {
            audio.openStream()
        }
    }
}
