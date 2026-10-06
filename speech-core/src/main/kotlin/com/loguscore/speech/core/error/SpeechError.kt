package com.loguscore.speech.core.error

/**
 * Standardized, sealed error hierarchy representing domain errors in Logus Core.
 */
sealed interface SpeechError {
    val message: String
    val cause: Throwable?

    /**
     * Required audio permission was denied by the user or OS.
     */
    data class PermissionDenied(
        val permission: String,
        override val message: String = "Permission '$permission' was denied.",
        override val cause: Throwable? = null
    ) : SpeechError

    /**
     * Audio recording failed (e.g. mic in use by another app, buffer overflow, initialization error).
     */
    data class AudioRecordFailed(
        override val message: String = "Failed to initialize or read audio stream.",
        override val cause: Throwable? = null
    ) : SpeechError

    /**
     * No registered speech recognition provider meets the required constraints.
     */
    data class ProviderNotFound(
        val reason: String = "No matching speech recognition provider found.",
        override val message: String = reason,
        override val cause: Throwable? = null
    ) : SpeechError

    /**
     * Network connectivity is unavailable but mandatory for remote provider execution.
     */
    data class NetworkUnavailable(
        override val message: String = "Network connection is required for remote provider execution.",
        override val cause: Throwable? = null
    ) : SpeechError

    /**
     * An on-device model file is missing or not downloaded.
     */
    data class ModelNotAvailable(
        val modelIdentifier: String,
        override val message: String = "Model '$modelIdentifier' is not downloaded or available.",
        override val cause: Throwable? = null
    ) : SpeechError

    /**
     * Transcription processing failed within the provider engine.
     */
    data class TranscriptionFailed(
        val providerId: String,
        override val message: String = "Transcription failed on provider '$providerId'.",
        override val cause: Throwable? = null
    ) : SpeechError

    /**
     * An action was invoked during an invalid session state.
     */
    data class InvalidState(
        override val message: String,
        override val cause: Throwable? = null
    ) : SpeechError

    /**
     * Configuration parameters violate invariants or constraints.
     */
    data class ConfigurationError(
        override val message: String,
        override val cause: Throwable? = null
    ) : SpeechError
}
