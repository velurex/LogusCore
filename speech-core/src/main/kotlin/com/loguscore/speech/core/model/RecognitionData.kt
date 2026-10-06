package com.loguscore.speech.core.model

/**
 * Encapsulates parameters for a single segment transcription request.
 */
data class RecognitionRequest(
    val language: SpeechLanguage = SpeechLanguage.AUTO,
    val qualityRequirement: QualityRequirement = QualityRequirement.BEST_AVAILABLE,
    val vocabularyHints: List<String> = emptyList()
)

/**
 * Result of a completed speech recognition task.
 */
data class RecognitionResult(
    val text: String,
    val confidence: Float? = null,
    val detectedLanguage: SpeechLanguage? = null,
    val processingTimeMs: Long = 0L
) {
    init {
        confidence?.let {
            require(it in 0.0f..1.0f) { "Confidence score must be in range [0.0, 1.0]" }
        }
        require(processingTimeMs >= 0) { "processingTimeMs must be non-negative" }
    }
}
