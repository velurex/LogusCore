package com.loguscore.speech.core.audio

import java.io.InputStream

/**
 * Represents a discrete, finalized segment of audio captured for transcription.
 *
 * Implementations stream audio data on demand via [openStream] rather than
 * retaining monolithic byte arrays in memory, avoiding OutOfMemoryError.
 */
interface AudioSegment {
    /**
     * Estimated or exact duration of the audio segment in milliseconds.
     */
    val durationMs: Long

    /**
     * Audio sampling rate in Hertz (e.g. 16000).
     */
    val sampleRate: Int

    /**
     * Number of audio channels (e.g. 1 for mono, 2 for stereo).
     */
    val channels: Int

    /**
     * Encoding format of the audio stream.
     */
    val audioFormat: AudioEncodingFormat

    /**
     * Opens a new readable [InputStream] of raw or encoded audio data.
     * The caller is responsible for closing the stream.
     */
    fun openStream(): InputStream

    /**
     * Releases backing resources, such as cached temporary files or native memory.
     */
    fun release()
}
