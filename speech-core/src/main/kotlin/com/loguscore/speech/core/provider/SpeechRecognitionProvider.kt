package com.loguscore.speech.core.provider

import com.loguscore.speech.core.audio.AudioSegment
import com.loguscore.speech.core.model.RecognitionRequest
import com.loguscore.speech.core.model.RecognitionResult

/**
 * Common abstraction for all on-device and remote speech recognition engines.
 */
interface SpeechRecognitionProvider {
    /**
     * Unique identifier of the provider (e.g. "vosk-local", "whisper-cloud", "fake-provider").
     */
    val id: String

    /**
     * Capabilities declared by this provider.
     */
    val capabilities: RecognitionCapabilities

    /**
     * Transcribes a finalized audio segment according to the provided request parameters.
     *
     * @param audio The finalized audio segment to transcribe.
     * @param request Parameters and constraints for the transcription.
     * @return Transcribed text and metadata wrapped in [RecognitionResult].
     */
    suspend fun transcribe(
        audio: AudioSegment,
        request: RecognitionRequest
    ): RecognitionResult
}
