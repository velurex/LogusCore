package com.loguscore.speech.core.event

import com.loguscore.speech.core.audio.AudioSegment
import com.loguscore.speech.core.error.SpeechError
import com.loguscore.speech.core.model.RecognitionResult

/**
 * Asynchronous events emitted sequentially throughout an active speech session.
 */
sealed interface SpeechEvent {
    /**
     * Session recording has begun.
     */
    data object RecordingStarted : SpeechEvent

    /**
     * Voice activity detector detected the onset of speech.
     */
    data object SpeechStarted : SpeechEvent

    /**
     * An audio segment has been finalized and is ready for recognition or persistence.
     */
    data class SpeechSegmentCompleted(
        val segmentId: String,
        val audio: AudioSegment
    ) : SpeechEvent

    /**
     * Provider has begun transcribing an audio segment.
     */
    data class TranscriptionStarted(
        val segmentId: String,
        val providerId: String
    ) : SpeechEvent

    /**
     * Transcription for a segment completed successfully.
     */
    data class TranscriptionCompleted(
        val segmentId: String,
        val result: RecognitionResult
    ) : SpeechEvent

    /**
     * An error occurred during audio capture or transcription.
     */
    data class ErrorOccurred(
        val error: SpeechError
    ) : SpeechEvent
}
