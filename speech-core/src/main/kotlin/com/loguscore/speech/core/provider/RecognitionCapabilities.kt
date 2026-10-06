package com.loguscore.speech.core.provider

import com.loguscore.speech.core.model.ExecutionMode
import com.loguscore.speech.core.model.HardwareAcceleration
import com.loguscore.speech.core.model.ModelState
import com.loguscore.speech.core.model.QualityLevel
import com.loguscore.speech.core.model.SpeechLanguage

/**
 * Declares operational capabilities, constraints, and supported features of a speech recognition provider.
 */
data class RecognitionCapabilities(
    val supportedLanguages: Set<SpeechLanguage>,
    val executionMode: ExecutionMode,
    val qualityLevels: Set<QualityLevel>,
    val requiresNetwork: Boolean,
    val modelState: ModelState = ModelState.AVAILABLE,
    val hardwareAcceleration: Set<HardwareAcceleration> = setOf(HardwareAcceleration.CPU),
    val supportsVocabularyHints: Boolean = false
) {
    /**
     * Checks whether the provider supports the given language, including AUTO detection.
     */
    fun supportsLanguage(language: SpeechLanguage): Boolean {
        if (language.isAuto) {
            return supportedLanguages.any { it.isAuto }
        }
        return supportedLanguages.any {
            it.tag.equals(language.tag, ignoreCase = true) ||
            it.tag.startsWith("${language.tag.substringBefore('-')}-", ignoreCase = true)
        }
    }

    /**
     * Checks whether the provider supports the required quality level.
     */
    fun supportsQuality(quality: QualityLevel): Boolean = quality in qualityLevels
}
