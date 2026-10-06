# Logus Core – Implementation Plan

This document outlines the phased, iterative development plan for the **Logus Core** SDK in accordance with the project specification (Chapters 44–48).

Development is conducted iteratively – each phase must be built, tested, and validated before commencing subsequent stages.

---

## 🎯 Core Development Rules

1. **Iterative Progression:** Do not implement the entire system at once. Every phase must produce compilable, tested deliverables.
2. **Clean Architecture:** `speech-core` must have zero dependencies on the Android SDK (`android.*`). Platform-specific code belongs exclusively in `speech-android`.
3. **Provider Neutrality:** No commercial vendor or model names may appear in core abstractions or interfaces.
4. **Host Application Decoupling:** Zero client-specific business logic (no subscription models, user accounts, or proprietary data models).
5. **Pure JVM Testability:** Every component must be architected for automated unit testing using fake/mock implementations without requiring Android hardware.

---

## 📋 Phase Roadmap

| Phase | Title | Deliverables | Verification & Goals |
| :---: | :--- | :--- | :--- |
| **Phase 1** | **Project Skeleton & Build Setup** | Gradle setup, `speech-core`, `speech-android`, `sample-app`, Fake Provider | Successful build and pure JVM unit tests of domain models. |
| **Phase 2** | **Audio Capture & Permissions** | `AudioRecord` capture, `AudioSegment` abstraction, file/stream storage | Manual verification of recording in sample app. |
| **Phase 3** | **State & Event System** | Reactive events (`SpeechEvent`), state machine (`SpeechState`), Flow pipeline | Unit tests verifying state transitions and event emissions. |
| **Phase 4** | **VAD & Segmentation** | Voice Activity Detection (VAD), silence thresholds, continuous session | Segmentation into completed blocks while session remains listening. |
| **Phase 5** | **Provider SPI & Capabilities** | `SpeechRecognitionProvider`, `RecognitionCapabilities`, `RecognitionRequest/Result` | Provider contract testing via `FakeSpeechRecognitionProvider`. |
| **Phase 6** | **Recognition Router** | `RecognitionRouter`, quality/language/network/hardware matching | Decision engine with transparent rationale reporting. |
| **Phase 7** | **On-Device / Local Provider** | Dedicated module `speech-provider-local` (e.g. Whisper.cpp / on-device STT) | Offline transcription without internet access. |
| **Phase 8** | **Remote / Cloud Provider** | Dedicated module `speech-provider-remote` (gateway client for cloud backend) | Remote transcription with secure credential handling. |
| **Phase 9** | **Sample App Polish & Client Integration** | Complete `sample-app` UI, host application integration verification | End-to-end validation in real applications via local dependency. |

---

## 🛠️ Detailed Implementation Phases

---

### Phase 1: Project Skeleton & Build Setup (Initial Milestone)

#### Objective:
Initialize the multi-module Gradle project structure, configure library version catalogs, and establish foundational public interfaces and domain models.

#### Tasks:
* [ ] Create Gradle configuration with Kotlin DSL (`settings.gradle.kts`, root `build.gradle.kts`, `gradle/libs.versions.toml`).
* [ ] Initialize module `speech-core` as a pure Kotlin/JVM module (`plugins { id("kotlin") }`).
* [ ] Initialize module `speech-android` as an Android Library module (`plugins { id("com.android.library") }`).
* [ ] Initialize module `sample-app` as an Android Application module (`plugins { id("com.android.application") }`).
* [ ] Establish package hierarchy (`com.loguscore.speech.core.*` and `com.loguscore.speech.android.*`).
* [ ] Create baseline domain models and interfaces in `speech-core`:
  * `AudioSegment`
  * `SpeechConfiguration`
  * `SpeechLanguage`
  * `QualityRequirement` / `QualityLevel`
  * `ProcessingPreference`
  * `SpeechState` & `SpeechEvent`
  * `SpeechError`
  * `SpeechRecognitionProvider` & `RecognitionCapabilities`
* [ ] Implement `FakeSpeechRecognitionProvider` for test harnesses.
* [ ] Write unit tests verifying model instantiation and contract invariants.
* [ ] Verify that the complete project builds cleanly via `./gradlew check`.

#### Acceptance Criteria:
The project builds successfully via CLI, and unit tests in `speech-core` pass without warnings and without Android SDK dependencies.

---

### Phase 2: Audio Capture & Permissions (`speech-android`)

#### Objective:
Deliver dependable microphone audio recording on Android with a strictly managed memory footprint.

#### Tasks:
* [ ] Implement runtime permission checks for `android.permission.RECORD_AUDIO`.
* [ ] Implement internal `AudioRecorder` using Android `AudioRecord`.
* [ ] Implement continuous streaming of audio samples to private application cache files without accumulating large byte arrays in RAM.
* [ ] Implement `FileAudioSegment : AudioSegment` with proper resource cleanup (`release()`).
* [ ] Handle Android lifecycle events (releasing microphone on component destruction or audio focus loss).
* [ ] Build a simple Start/Stop manual recording interface in `sample-app` to verify segment creation.

#### Acceptance Criteria:
`sample-app` can start and stop manual recordings, generates a valid `AudioSegment`, and does not crash upon configuration changes or missing permissions.

---

### Phase 3: State & Event System

#### Objective:
Provide host applications with a robust, reactive API for observing recording and transcription progress without enforcing any UI constraints.

#### Tasks:
* [ ] Implement a state machine distinguishing global session status (`SessionState`) from individual segment processing.
* [ ] Expose an event pipeline via Kotlin Coroutines `SharedFlow<SpeechEvent>`.
* [ ] Expose state observables via `StateFlow<SpeechState>`.
* [ ] Cover all state transitions with unit tests (start -> listening -> stopping -> stopped, error states).
* [ ] Connect the `speech-android` capture pipeline to state emissions (`RecordingStarted`, `RecordingStopped`, `Error`).

#### Acceptance Criteria:
All state changes are deterministic and fully testable in pure JVM unit tests.

---

### Phase 4: Voice Activity Detection (VAD) & Segmentation

#### Objective:
Implement automated recording mode with speech and silence detection that partitions voice streams into completed audio segments while preserving session continuity.

#### Tasks:
* [ ] Define `VoiceActivityDetector` interface in `speech-core`.
* [ ] Implement baseline energy/VAD detector with configurable parameters:
  * `minSpeechDurationMs` (filters out brief transients and clicks).
  * `silenceThresholdMs` (silence required to close an active segment).
  * `maxSegmentDurationMs` (ceiling to prevent unbounded blocks in noisy rooms).
  * `energyThreshold` (signal amplitude sensitivity).
* [ ] Implement segmentation pipeline: upon detected silence, finalize the active `AudioSegment` and emit it for transcription, immediately resuming listening in the active session.
* [ ] Write unit tests with synthetic audio streams simulating speech-pause-speech patterns.
* [ ] Integrate automatic mode into `sample-app`.

#### Acceptance Criteria:
Natural pauses produce distinct completed audio segments, while the recording session remains continuously active until explicitly stopped.

---

### Phase 5: Provider SPI & Capabilities

#### Objective:
Establish a clean service provider interface (SPI) for speech recognition engines and complete capability descriptors.

#### Tasks:
* [ ] Formalize and document `SpeechRecognitionProvider`:
  * `suspend fun transcribe(audio: AudioSegment, request: RecognitionRequest): RecognitionResult`
* [ ] Elaborate `RecognitionCapabilities`:
  * Supported languages (BCP-47 codes and `AUTO` detection).
  * Execution modes (`ON_DEVICE` vs. `REMOTE`).
  * Quality levels (`BASIC`, `GOOD`, `HIGH`, `BEST_AVAILABLE`).
  * Network requirement flag (`requiresNetwork`).
  * Model readiness (`AVAILABLE`, `DOWNLOAD_REQUIRED`, `UNAVAILABLE`).
  * Domain vocabulary hints support.
* [ ] Implement provider registry mechanism within the Logus Core builder/instance.
* [ ] Expand `FakeSpeechRecognitionProvider` to simulate capability sets, latencies, and failures.

#### Acceptance Criteria:
The provider interface is clean, free of vendor-specific details, and validated via mock implementations.

---

### Phase 6: Recognition Router & Transparent Decision Engine

#### Objective:
Implement the intelligence layer that evaluates application requirements against provider capabilities and transparently reports the rationale for its decisions.

#### Tasks:
* [ ] Implement `RecognitionRouter` in `speech-core`.
* [ ] Enforce processing preferences:
  * `AUTOMATIC`: Selects the optimal provider based on capabilities.
  * `LOCAL_ONLY`: Fails with an explicit error if local models cannot satisfy requirements.
  * `REMOTE_ONLY`: Dispatches exclusively to remote providers.
* [ ] Evaluate requested quality and language (e.g. local model lacking high quality for target language routes to remote).
* [ ] Construct structured `RoutingDecision` containing `selectedProvider` and human-readable `reason`.
* [ ] Thoroughly unit-test:
  * Quality and language matching.
  * Fallbacks from local to remote when criteria are unmet.
  * Network absence and offline handling.

#### Acceptance Criteria:
Unit tests cover all router edge cases and routing decisions provide clear diagnostic rationale.

---

### Phase 7: On-Device / Local Provider Module

#### Objective:
Create the first tangible plug-in module for offline speech-to-text processing.

#### Tasks:
* [ ] Initialize module `speech-provider-local`.
* [ ] Integrate a lightweight on-device STT engine (e.g., Whisper.cpp JNI wrapper, Vosk, or platform model).
* [ ] Implement `SpeechRecognitionProvider` interface.
* [ ] Declare genuine capabilities (supported model languages, offline flag).
* [ ] Validate offline transcription in `sample-app` with airplane mode enabled.

#### Acceptance Criteria:
The library successfully transcribes recorded audio blocks on-device without any active network connection.

---

### Phase 8: Remote / Cloud Provider Module

#### Objective:
Create a remote speech recognition provider communicating with a backend gateway.

#### Tasks:
* [ ] Initialize module `speech-provider-remote`.
* [ ] Implement network client transmitting audio segments to a configurable endpoint.
* [ ] Ensure the module avoids hardcoded client secrets, accepting auth tokens or delegating to an application backend gateway.
* [ ] Map network errors, timeouts, and rate limits to standard `SpeechError` types.
* [ ] Verify cloud transcription in `sample-app`.

#### Acceptance Criteria:
When requesting `HIGH` quality or forcing `REMOTE_ONLY`, transcription completes successfully via the remote provider module.

---

### Phase 9: Sample App Polish & Client Integration

#### Objective:
Deliver a comprehensive demonstration app and validate seamless integration into external host applications.

#### Tasks:
* [ ] Complete `sample-app` UI (switching manual/automatic VAD, selecting language, quality, viewing segments and router explanations).
* [ ] Verify integration of Logus Core into host applications as a local composite Gradle build (`includeBuild` or project dependency).
* [ ] Ensure host applications function with Logus Core without architectural workarounds.
* [ ] Complete KDoc documentation for all public APIs.
* [ ] Configure GitHub Actions CI workflow for automated builds and tests on every commit.

#### Acceptance Criteria:
Logus Core functions reliably in `sample-app` and in real host applications. The core contains zero application-specific logic.
