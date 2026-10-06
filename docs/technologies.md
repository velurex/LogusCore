# Logus Core – Technology & Architecture

## 1. Technology Stack
**Logus Core** is built according to modern Android engineering standards:
* **Language:** Kotlin (idiomatic syntax, strict null-safety, data classes, sealed interfaces/classes).
* **Asynchronous Programming:** Kotlin Coroutines & Asynchronous Flows (`StateFlow`, `SharedFlow`).
* **Build System:** Gradle with Kotlin DSL (`build.gradle.kts`) and Version Catalogs (`gradle/libs.versions.toml`).
* **Platform:** Android SDK (modern `minSdk` compatibility, targeting latest stable Android release).
* **Platform Decoupling:** Core logic is pure Kotlin JVM (`kotlin-jvm` / platform-agnostic), while audio recording and Android system services reside in an Android library module (`com.android.library`).

---

## 2. Modular Architecture

The repository enforces strict separation of concerns through isolated Gradle modules:

```
LogusCore/
├── speech-core/               # Pure Kotlin (JVM) – interfaces, domain models, router, VAD abstractions
├── speech-android/            # Android library – AudioRecord capture, permissions, Android lifecycle
├── speech-provider-local/     # (Optional) On-device STT implementation module
├── speech-provider-remote/    # (Optional) Remote/cloud STT client module
└── sample-app/                # Android demonstration and testing app
```

### 2.1 Module `speech-core`
* **Rule:** Zero Android SDK dependencies (`android.*`). This module is executable and fully testable in a pure JVM environment.
* **Responsibilities:**
  * Configuration domain models (`SpeechConfiguration`, `RecognitionRequest`).
  * Audio abstractions (`AudioSegment`, `AudioEncodingFormat`).
  * Provider contracts (`SpeechRecognitionProvider`).
  * Capability descriptors (`RecognitionCapabilities`, `QualityLevel`, `ExecutionMode`).
  * Decision routing logic (`RecognitionRouter`, `RoutingDecision`).
  * State and event models (`SpeechState`, `SpeechEvent`).
  * Standardized error hierarchy (`SpeechError`).

### 2.2 Module `speech-android`
* **Rule:** Android-specific implementation and OS integrations.
* **Responsibilities:**
  * Audio capture via `AudioRecord`.
  * Runtime permission management (`RECORD_AUDIO`).
  * Android lifecycle integration (`LifecycleObserver`, graceful audio handling upon focus loss or phone calls).
  * Streaming audio capture into temporary files/buffers satisfying the `AudioSegment` contract.

### 2.3 Provider Modules (`speech-provider-*`)
* Each provider implements the `SpeechRecognitionProvider` interface.
* Completely self-contained modules. Adding a new provider does not require changes to `speech-core`.

---

## 3. Audio Processing & Memory Management

### 3.1 The `AudioSegment` Abstraction
To avoid memory exhaustion (`OutOfMemoryError`), raw audio data is never kept as a monolithic `ByteArray` in RAM.

```kotlin
interface AudioSegment {
    val durationMs: Long
    val sampleRate: Int
    val channels: Int
    val audioFormat: AudioEncodingFormat
    
    /** Provides a readable stream (backed by temporary storage or a file) */
    fun openStream(): InputStream
    
    /** Releases backing files and native resources */
    fun release()
}
```

The Android implementation streams PCM/WAV data continuously into app-private cache storage, creating an immutable `AudioSegment` upon block completion.

---

## 4. Voice Activity Detection (VAD) & Segmentation

In automatic mode, Logus Core analyzes the incoming audio stream from the microphone in real time.

### Configurable VAD Parameters:
* `minSpeechDurationMs` – Minimum speech duration to qualify as a segment (filters out ambient clicks and pops).
* `silenceThresholdMs` – Silence duration required to finalize an active speech segment (e.g., 800–1500 ms).
* `energyThreshold` – Signal energy threshold required to trigger voice detection.
* `maxSegmentDurationMs` – Hard ceiling for segment length (protects against perpetual segments in noisy environments).

### Session Continuity Principle:
Segmentation operates in a continuous loop:
```
Session START -> Listening -> Speech detected -> Speaking -> Silence detected ->
-> Segment finalized & sent for transcription -> Immediate return to Listening -> ... -> Session STOP
```

---

## 5. Provider System & Intelligent Router

### 5.1 The `SpeechRecognitionProvider` Interface
```kotlin
interface SpeechRecognitionProvider {
    val id: String
    val capabilities: RecognitionCapabilities

    suspend fun transcribe(
        audio: AudioSegment,
        request: RecognitionRequest
    ): RecognitionResult
}
```

### 5.2 Capability Declarations (`RecognitionCapabilities`)
Providers declare their capabilities explicitly:
* `supportedLanguages`: Set of supported BCP-47 language codes and auto-detection support (`AUTO`).
* `executionMode`: `ON_DEVICE` vs. `REMOTE`.
* `qualityLevels`: Supported accuracy levels (`BASIC`, `GOOD`, `HIGH`, `BEST_AVAILABLE`).
* `requiresNetwork`: Whether network connectivity is mandatory.
* `modelState`: Current model readiness (`AVAILABLE`, `DOWNLOAD_REQUIRED`, `UNAVAILABLE`).
* `hardwareAcceleration`: Hardware requirements (NPU, GPU, CPU).
* `supportsVocabularyHints`: Support for custom domain terms.

### 5.3 Recognition Router
Host applications do not request specific models; they declare high-level constraints:
* **ProcessingPreference:** `AUTOMATIC`, `LOCAL_ONLY`, `REMOTE_ONLY`.
* **QualityRequirement:** `BASIC`, `GOOD`, `HIGH`, `BEST_AVAILABLE`.
* **Language:** Explicit language (e.g., `en-US`, `cs-CZ`) or `AUTO`.
* **Domain / Vocabulary Hints:** Optional contextual terminology.

The router matches constraints against provider capabilities and returns a `RoutingDecision` containing the selected provider and an explicit, human-readable reason (`RoutingDecision.reason`) for diagnostics.

---

## 6. State Machine & Event Streams

The architecture cleanly decouples session state from segment processing:
1. **Session State:** `IDLE`, `INITIALIZING`, `LISTENING`, `STOPPING`, `STOPPED`, `ERROR`.
2. **Segment Events:** Streamed independently while the session remains active:
   * `RecordingStarted`
   * `SpeechStarted`
   * `SpeechSegmentCompleted(segmentId, audio)`
   * `TranscriptionStarted(segmentId, providerId)`
   * `TranscriptionCompleted(segmentId, result)`
   * `SpeechError(error)`

Host applications observe these flows and drive their own UI independently.

---

## 7. Lifecycle & Permissions

* **Runtime Permissions:** Validates `android.permission.RECORD_AUDIO`. Returns structured `SpeechError.PermissionDenied` if permissions are absent.
* **Audio Focus & Interruptions:** Responds gracefully to incoming calls or audio focus preemption by suspending or safely finalizing active recordings.
* **Lifecycle Awareness:** Configuration changes (e.g., screen rotation) or background transitions do not disrupt active recording sessions.

---

## 8. Testing Strategy & CI/CD

### 8.1 Pure JVM Testing Without Hardware
Abstractions across `AudioSource`, `VoiceActivityDetector`, and `SpeechRecognitionProvider` enable complete automated testing via fakes:
* `FakeSpeechRecognitionProvider`
* `FakeAudioSource`
* `FakeVoiceActivityDetector`

Unit tests in `speech-core` cover:
* All router matching scenarios (language, quality, hardware fallback, edge cases).
* State machine transitions.
* VAD time-series segmentation.

### 8.2 CI/CD Pipeline
Automated verification on each pull request and commit:
1. Checkout repository.
2. Setup JDK (Java 17/21).
3. Build all modules (`./gradlew assemble`).
4. Execute unit test suite (`./gradlew test`).
5. Static analysis and lint checks (`ktlint`, Android Lint).
