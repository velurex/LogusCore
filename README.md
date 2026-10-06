# Logus Core

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)

**Logus Core** is a modern, modular, and general-purpose open-source Speech SDK for Android written in Kotlin. It provides Android applications with a unified, stable, and provider-agnostic interface for speech audio capture and Speech-to-Text (STT) transcription.

---

## 🚀 Key Features

* 🎙️ **Dual Recording Modes:**
  * **Manual Recording:** Continuous recording managed by the user or application (e.g., push-to-talk).
  * **Automatic / Voice Activity Recording (VAD):** Intelligent detection of speech onset and silence with automatic segmentation. Pauses in speech do not terminate the session; they finalize the current segment and keep listening.
* 📦 **Block-Based Transcription:**
  * Avoids unstable real-time streaming transcription of partial words in favor of completed phrase/sentence blocks, optimizing accuracy and preserving battery life.
* 🧠 **Intelligent & Transparent Routing (Recognition Router):**
  * Dynamic dispatch between local on-device models and remote cloud services based on requested quality, language, model availability, and hardware capabilities.
  * Transparent routing rationale for straightforward diagnostics and debugging.
* 🔌 **Provider Neutrality (Pluggable Architecture):**
  * Core library contains zero hardcoded vendor dependencies. Speech engines (Whisper, Vosk, Google Cloud Speech, etc.) are implemented as independent modules conforming to `SpeechRecognitionProvider`.
* 🎨 **100% UI Independent:**
  * Contains no mandatory UI elements or Compose components. Exposes clean reactive state and event streams (`StateFlow`, `SharedFlow`) allowing host applications to build custom interfaces.
* 🛡️ **Client Security & Zero Mobile Secrets:**
  * Requires no hardcoded API keys inside the mobile app; natively supports backend gateway authorization patterns.
* 🧪 **100% JVM Testability:**
  * Decoupled audio abstractions allow thorough unit testing of routing, segmentation, and state transitions without physical microphones or emulators.

---

## 🏛️ Modular Architecture

```text
LogusCore/
│
├── speech-core/              # Pure Kotlin (JVM) – domain models, interfaces, routing, VAD abstractions
├── speech-android/           # Android library – AudioRecord capture, permissions, Android lifecycle
├── speech-provider-local/    # (Optional) On-device speech recognition implementation
├── speech-provider-remote/   # (Optional) Cloud / remote speech recognition implementation
└── sample-app/               # Showcase Android application demonstrating SDK usage
```

---

## 💡 Quick Start

### 1. Configuration & Initialization
```kotlin
val config = SpeechConfiguration(
    recordingMode = RecordingMode.AutomaticVAD(
        silenceThresholdMs = 1200L,
        minSpeechDurationMs = 300L
    ),
    processingPreference = ProcessingPreference.AUTOMATIC,
    qualityRequirement = QualityRequirement.HIGH,
    language = SpeechLanguage("en-US")
)

val logusCore = LogusCoreBuilder(context)
    .withConfiguration(config)
    .registerProvider(localWhisperProvider)
    .registerProvider(remoteCloudProvider)
    .build()
```

### 2. Observing Events & Results
```kotlin
lifecycleScope.launch {
    logusCore.events.collect { event ->
        when (event) {
            is SpeechEvent.RecordingStarted -> showRecordingUi()
            is SpeechEvent.SpeechStarted -> showSpeechDetected()
            is SpeechEvent.SpeechSegmentCompleted -> showProcessing(event.segmentId)
            is SpeechEvent.TranscriptionCompleted -> {
                println("Transcription [${event.result.language}]: ${event.result.text}")
                println("Processed by: ${event.result.providerId} (${event.result.executionMode})")
            }
            is SpeechEvent.Error -> handleError(event.error)
            is SpeechEvent.RecordingStopped -> hideRecordingUi()
        }
    }
}
```

### 3. Starting & Stopping Recording
```kotlin
// Start recording session (requires RECORD_AUDIO runtime permission)
logusCore.startRecording()

// Stop recording session
logusCore.stopRecording()
```

---

## 📚 Documentation

Comprehensive documentation is available in the [`docs/`](docs) directory:
* 📖 [**docs/overview.md**](docs/overview.md) – Project overview, core principles, recording modes, and data flow.
* 💼 [**docs/business.md**](docs/business.md) – Business context, host application decoupling, security models, and licensing.
* ⚙️ [**docs/technologies.md**](docs/technologies.md) – Technical stack, modules, `AudioSegment` memory model, VAD details, and testing.
* 📋 [**IMPLEMENTATION_PLAN.md**](IMPLEMENTATION_PLAN.md) – Step-by-step development roadmap from skeleton to final client integration.

---

## 🗺️ Implementation Roadmap

Development progresses through disciplined, iterative phases:
1. **Phase 1 – Skeleton & Build Setup:** Multi-module Gradle configuration (`speech-core`, `speech-android`, `sample-app`), domain models, and fake provider.
2. **Phase 2 – Audio Capture:** Permission management, microphone capture, and `AudioSegment` implementation.
3. **Phase 3 – Event & State System:** Session state machine and reactive flow pipelines.
4. **Phase 4 – VAD & Segmentation:** Voice activity detection, configurable thresholds, and continuous session loops.
5. **Phase 5 – Provider SPI:** Provider abstraction contract and capability evaluation.
6. **Phase 6 – Recognition Router:** Dynamic provider selection and transparent reasoning engine.
7. **Phases 7 & 8 – Local & Remote Providers:** Plug-in modules for on-device and cloud transcription.
8. **Phase 9 – Reference Integration:** Sample application polish and end-to-end host app validation.

For the detailed task breakdown, refer to [IMPLEMENTATION_PLAN.md](IMPLEMENTATION_PLAN.md).

---

## 👥 Adopters & Community

Are you integrating or evaluating **Logus Core** in your app, commercial product, or research? Letting us know directly supports ongoing project development and helps prioritize roadmap features:

* 🚀 **Public Projects:** Add your project to [ADOPTERS.md](ADOPTERS.md) via Pull Request.
* 🔒 **Commercial / Proprietary:** Register confidentially using our [Adoption Registration Form](ADOPTERS.md#option-2-private--confidential-adoption-form-for-proprietary-or-pre-release-apps).

---

## 📄 License

This project is licensed under the [Apache License 2.0](LICENSE).
