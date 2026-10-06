package com.loguscore.speech.core.model

/**
 * Execution topology of a speech recognition provider.
 */
enum class ExecutionMode {
    ON_DEVICE,
    REMOTE
}

/**
 * Readiness state of an on-device or remote speech recognition model.
 */
enum class ModelState {
    AVAILABLE,
    DOWNLOAD_REQUIRED,
    UNAVAILABLE
}

/**
 * Hardware acceleration required or utilized by a recognition engine.
 */
enum class HardwareAcceleration {
    CPU,
    GPU,
    NPU
}
