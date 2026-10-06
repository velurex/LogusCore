package com.loguscore.speech.core.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SpeechConfigurationTest {

    @Test
    fun `default configuration has expected invariants`() {
        val config = SpeechConfiguration()
        assertEquals(SpeechLanguage.AUTO, config.language)
        assertEquals(ProcessingPreference.AUTOMATIC, config.processingPreference)
        assertEquals(QualityRequirement.BEST_AVAILABLE, config.qualityRequirement)
        assertEquals(500L, config.minSpeechDurationMs)
        assertEquals(1000L, config.silenceThresholdMs)
        assertEquals(0.05f, config.energyThreshold)
        assertEquals(60_000L, config.maxSegmentDurationMs)
        assertEquals(16_000, config.sampleRate)
        assertEquals(1, config.channels)
        assertEquals(emptyList(), config.vocabularyHints)
    }

    @Test
    fun `valid custom configuration is instantiated successfully`() {
        val config = SpeechConfiguration(
            language = SpeechLanguage.CZECH,
            processingPreference = ProcessingPreference.LOCAL_ONLY,
            qualityRequirement = QualityRequirement.HIGH,
            minSpeechDurationMs = 300L,
            silenceThresholdMs = 800L,
            energyThreshold = 0.1f,
            maxSegmentDurationMs = 30_000L,
            sampleRate = 48_000,
            channels = 2,
            vocabularyHints = listOf("Logus", "Android")
        )

        assertEquals(SpeechLanguage.CZECH, config.language)
        assertEquals(ProcessingPreference.LOCAL_ONLY, config.processingPreference)
        assertEquals(QualityRequirement.HIGH, config.qualityRequirement)
        assertEquals(listOf("Logus", "Android"), config.vocabularyHints)
    }

    @Test
    fun `throws when minSpeechDurationMs is non-positive`() {
        assertFailsWith<IllegalArgumentException> {
            SpeechConfiguration(minSpeechDurationMs = 0L)
        }
        assertFailsWith<IllegalArgumentException> {
            SpeechConfiguration(minSpeechDurationMs = -10L)
        }
    }

    @Test
    fun `throws when silenceThresholdMs is non-positive`() {
        assertFailsWith<IllegalArgumentException> {
            SpeechConfiguration(silenceThresholdMs = 0L)
        }
    }

    @Test
    fun `throws when energyThreshold is outside range`() {
        assertFailsWith<IllegalArgumentException> {
            SpeechConfiguration(energyThreshold = -0.01f)
        }
        assertFailsWith<IllegalArgumentException> {
            SpeechConfiguration(energyThreshold = 1.01f)
        }
    }

    @Test
    fun `throws when maxSegmentDurationMs is not greater than minSpeechDurationMs`() {
        assertFailsWith<IllegalArgumentException> {
            SpeechConfiguration(minSpeechDurationMs = 500L, maxSegmentDurationMs = 500L)
        }
        assertFailsWith<IllegalArgumentException> {
            SpeechConfiguration(minSpeechDurationMs = 1000L, maxSegmentDurationMs = 500L)
        }
    }

    @Test
    fun `throws when sampleRate or channels are invalid`() {
        assertFailsWith<IllegalArgumentException> {
            SpeechConfiguration(sampleRate = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            SpeechConfiguration(channels = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            SpeechConfiguration(channels = 3)
        }
    }
}
