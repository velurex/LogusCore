package com.loguscore.speech.core.provider

import com.loguscore.speech.core.model.ExecutionMode
import com.loguscore.speech.core.model.QualityLevel
import com.loguscore.speech.core.model.SpeechLanguage
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RecognitionCapabilitiesTest {

    @Test
    fun `supportsLanguage matches exact and prefix language tags`() {
        val capabilities = RecognitionCapabilities(
            supportedLanguages = setOf(
                SpeechLanguage.AUTO,
                SpeechLanguage("en-US"),
                SpeechLanguage("cs-CZ")
            ),
            executionMode = ExecutionMode.ON_DEVICE,
            qualityLevels = setOf(QualityLevel.GOOD, QualityLevel.HIGH),
            requiresNetwork = false
        )

        assertTrue(capabilities.supportsLanguage(SpeechLanguage.AUTO))
        assertTrue(capabilities.supportsLanguage(SpeechLanguage("en-US")))
        assertTrue(capabilities.supportsLanguage(SpeechLanguage("cs-CZ")))
        assertTrue(capabilities.supportsLanguage(SpeechLanguage("en-GB")), "Should match generic language prefix")
        assertFalse(capabilities.supportsLanguage(SpeechLanguage("fr-FR")))
    }

    @Test
    fun `supportsQuality evaluates quality set membership`() {
        val capabilities = RecognitionCapabilities(
            supportedLanguages = setOf(SpeechLanguage.ENGLISH),
            executionMode = ExecutionMode.REMOTE,
            qualityLevels = setOf(QualityLevel.HIGH, QualityLevel.BEST_AVAILABLE),
            requiresNetwork = true
        )

        assertTrue(capabilities.supportsQuality(QualityLevel.HIGH))
        assertTrue(capabilities.supportsQuality(QualityLevel.BEST_AVAILABLE))
        assertFalse(capabilities.supportsQuality(QualityLevel.BASIC))
    }
}
