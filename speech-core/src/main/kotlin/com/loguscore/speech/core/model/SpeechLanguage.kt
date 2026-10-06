package com.loguscore.speech.core.model

/**
 * Type-safe representation of a spoken language identified by a BCP-47 language tag or "auto".
 */
data class SpeechLanguage(val tag: String) {
    init {
        require(tag.isNotBlank()) { "Language tag must not be blank." }
    }

    /**
     * Returns true if language auto-detection is requested.
     */
    val isAuto: Boolean
        get() = tag.equals(AUTO.tag, ignoreCase = true)

    companion object {
        val AUTO = SpeechLanguage("auto")
        val ENGLISH = SpeechLanguage("en-US")
        val CZECH = SpeechLanguage("cs-CZ")
        val GERMAN = SpeechLanguage("de-DE")
        val SPANISH = SpeechLanguage("es-ES")
        val FRENCH = SpeechLanguage("fr-FR")
    }
}
