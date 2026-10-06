package com.loguscore.speech.core.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class RecognitionDataTest {

    @Test
    fun `RecognitionRequest instantiates with default parameters`() {
        val request = RecognitionRequest()
        assertEquals(SpeechLanguage.AUTO, request.language)
        assertEquals(QualityRequirement.BEST_AVAILABLE, request.qualityRequirement)
        assertEquals(emptyList(), request.vocabularyHints)
    }

    @Test
    fun `RecognitionResult validates confidence and duration invariants`() {
        val validResult = RecognitionResult(
            text = "Hello world",
            confidence = 0.95f,
            detectedLanguage = SpeechLanguage.ENGLISH,
            processingTimeMs = 120L
        )

        assertEquals("Hello world", validResult.text)
        assertEquals(0.95f, validResult.confidence)
        assertEquals(120L, validResult.processingTimeMs)

        val nullConfidenceResult = RecognitionResult(text = "Unscored result")
        assertNull(nullConfidenceResult.confidence)

        assertFailsWith<IllegalArgumentException> {
            RecognitionResult(text = "Invalid", confidence = 1.05f)
        }

        assertFailsWith<IllegalArgumentException> {
            RecognitionResult(text = "Invalid", confidence = -0.1f)
        }

        assertFailsWith<IllegalArgumentException> {
            RecognitionResult(text = "Invalid", processingTimeMs = -1L)
        }
    }
}
