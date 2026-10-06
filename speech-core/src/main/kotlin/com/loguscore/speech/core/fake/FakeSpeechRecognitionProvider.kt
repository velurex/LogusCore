package com.loguscore.speech.core.fake

import com.loguscore.speech.core.audio.AudioSegment
import com.loguscore.speech.core.model.ExecutionMode
import com.loguscore.speech.core.model.ModelState
import com.loguscore.speech.core.model.QualityLevel
import com.loguscore.speech.core.model.RecognitionRequest
import com.loguscore.speech.core.model.RecognitionResult
import com.loguscore.speech.core.model.SpeechLanguage
import com.loguscore.speech.core.provider.RecognitionCapabilities
import com.loguscore.speech.core.provider.SpeechRecognitionProvider
import kotlinx.coroutines.delay
import java.util.Collections

/**
 * In-memory test implementation of [SpeechRecognitionProvider] for unit and integration testing.
 */
class FakeSpeechRecognitionProvider(
    override val id: String = DEFAULT_ID,
    override var capabilities: RecognitionCapabilities = defaultCapabilities()
) : SpeechRecognitionProvider {

    var delayMs: Long = 0L
    var shouldFail: Boolean = false
    var failureThrowable: Throwable = IllegalStateException("Simulated transcription failure")

    var cannedResult: RecognitionResult = RecognitionResult(
        text = "Simulated transcribed speech",
        confidence = 0.98f,
        detectedLanguage = SpeechLanguage.ENGLISH,
        processingTimeMs = 15L
    )

    var customHandler: (suspend (AudioSegment, RecognitionRequest) -> RecognitionResult)? = null

    private val _invocations = Collections.synchronizedList(mutableListOf<Invocation>())
    val invocations: List<Invocation> get() = _invocations.toList()

    data class Invocation(
        val audio: AudioSegment,
        val request: RecognitionRequest,
        val timestampMs: Long = System.currentTimeMillis()
    )

    override suspend fun transcribe(audio: AudioSegment, request: RecognitionRequest): RecognitionResult {
        _invocations.add(Invocation(audio, request))

        if (delayMs > 0) {
            delay(delayMs)
        }

        if (shouldFail) {
            throw failureThrowable
        }

        return customHandler?.invoke(audio, request) ?: cannedResult
    }

    fun reset() {
        _invocations.clear()
        delayMs = 0L
        shouldFail = false
    }

    companion object {
        const val DEFAULT_ID = "fake-provider"

        fun defaultCapabilities() = RecognitionCapabilities(
            supportedLanguages = setOf(
                SpeechLanguage.AUTO,
                SpeechLanguage.ENGLISH,
                SpeechLanguage.CZECH
            ),
            executionMode = ExecutionMode.ON_DEVICE,
            qualityLevels = setOf(
                QualityLevel.BASIC,
                QualityLevel.GOOD,
                QualityLevel.HIGH,
                QualityLevel.BEST_AVAILABLE
            ),
            requiresNetwork = false,
            modelState = ModelState.AVAILABLE,
            supportsVocabularyHints = true
        )
    }
}
