package com.loguscore.speech.core.fake

import com.loguscore.speech.core.audio.AudioEncodingFormat
import com.loguscore.speech.core.audio.AudioSegment
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * In-memory test implementation of [AudioSegment].
 */
class FakeAudioSegment(
    override val durationMs: Long = 1000L,
    override val sampleRate: Int = 16000,
    override val channels: Int = 1,
    override val audioFormat: AudioEncodingFormat = AudioEncodingFormat.PCM_16BIT,
    private val data: ByteArray = ByteArray(0)
) : AudioSegment {

    var isReleased: Boolean = false
        private set

    override fun openStream(): InputStream {
        check(!isReleased) { "AudioSegment has already been released." }
        return ByteArrayInputStream(data)
    }

    override fun release() {
        isReleased = true
    }
}
