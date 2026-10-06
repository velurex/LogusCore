package com.loguscore.speech.core.model

/**
 * Host application preference for local on-device vs. remote cloud execution.
 */
enum class ProcessingPreference {
    AUTOMATIC,
    LOCAL_ONLY,
    REMOTE_ONLY
}
