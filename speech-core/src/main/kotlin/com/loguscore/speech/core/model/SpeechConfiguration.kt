package com.loguscore.speech.core.model

/**
 * Immutable configuration controlling speech session audio capture, VAD parameters,
 * and recognition constraints.
 */
data class SpeechConfiguration(
    val language: SpeechLanguage = SpeechLanguage.AUTO,
    val processingPreference: ProcessingPreference = ProcessingPreference.AUTOMATIC,
    val qualityRequirement: QualityRequirement = QualityRequirement.BEST_AVAILABLE,
    val minSpeechDurationMs: Long = DEFAULT_MIN_SPEECH_DURATION_MS,
    val silenceThresholdMs: Long = DEFAULT_SILENCE_THRESHOLD_MS,
    val energyThreshold: Float = DEFAULT_ENERGY_THRESHOLD,
    val maxSegmentDurationMs: Long = DEFAULT_MAX_SEGMENT_DURATION_MS,
    val sampleRate: Int = DEFAULT_SAMPLE_RATE,
    val channels: Int = DEFAULT_CHANNELS,
    val vocabularyHints: List<String> = emptyList()
) {
    init {
        require(minSpeechDurationMs > 0) { "minSpeechDurationMs must be positive" }
        require(silenceThresholdMs > 0) { "silenceThresholdMs must be positive" }
        require(energyThreshold in 0.0f..1.0f) { "energyThreshold must be between 0.0 and 1.0" }
        require(maxSegmentDurationMs > minSpeechDurationMs) {
            "maxSegmentDurationMs ($maxSegmentDurationMs) must be greater than minSpeechDurationMs ($minSpeechDurationMs)"
        }
        require(sampleRate > 0) { "sampleRate must be positive" }
        require(channels in 1..2) { "channels must be 1 (mono) or 2 (stereo)" }
    }

    companion object {
        const val DEFAULT_MIN_SPEECH_DURATION_MS = 500L
        const val DEFAULT_SILENCE_THRESHOLD_MS = 1000L
        const val DEFAULT_ENERGY_THRESHOLD = 0.05f
        const val DEFAULT_MAX_SEGMENT_DURATION_MS = 60_000L
        const val DEFAULT_SAMPLE_RATE = 16_000
        const val DEFAULT_CHANNELS = 1
    }
}
