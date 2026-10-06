package com.loguscore.speech.core.event

import com.loguscore.speech.core.error.SpeechError

/**
 * Lifecycle state machine representing the current condition of a speech session.
 */
sealed interface SpeechState {
    /**
     * Session is idle, no recording or listening is active.
     */
    data object Idle : SpeechState

    /**
     * Session is initializing hardware resources and audio pipelines.
     */
    data object Initializing : SpeechState

    /**
     * Session is actively listening for incoming voice activity (silence/background).
     */
    data object Listening : SpeechState

    /**
     * Active speech has been detected and is currently being buffered into a segment.
     */
    data object Recording : SpeechState

    /**
     * Session is winding down and finalizing any pending segment buffers.
     */
    data object Stopping : SpeechState

    /**
     * Session has cleanly stopped.
     */
    data object Stopped : SpeechState

    /**
     * Session encountered an error and was halted.
     */
    data class Error(val error: SpeechError) : SpeechState
}
