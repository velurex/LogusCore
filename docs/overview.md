# Logus Core – Project Overview

## 1. Project Purpose & Vision
**Logus Core** is an independent, general-purpose open-source library (SDK) for Android developed in Kotlin. It provides Android applications with a unified, clean, and technology-agnostic interface for speech audio capture and subsequent speech-to-text (STT) transcription.

The SDK addresses the complete lifecycle of voice processing on mobile devices:
* Reliable and safe microphone audio capture.
* Voice Activity Detection (VAD) and silence detection.
* Intelligent segmentation of audio into completed blocks.
* Automatic routing of transcription requests across local (on-device) and remote (cloud) engines.
* Transparent evaluation of device capabilities, model availability, quality requirements, and target languages.
* Delivery of detailed states and events to host applications without dictating any user interface (UI).

---

## 2. Key Architectural Principles

### 2.1 Strict Independence from Specific Applications
**Logus Core** is designed as a standalone, universal product. The library must not contain any concepts coupled to any specific client application (no subscription models, user accounts, specific domain entities, or proprietary business logic). The SDK is maintained in its own repository and can be integrated into any Android application.

### 2.2 Provider Neutrality
The core module (`speech-core`) has zero direct dependencies on specific speech-to-text models or commercial cloud vendors (no hardcoded Whisper, Google Cloud Speech, Vosk, Azure, etc.). Individual recognition engines are pluggable modules implementing the common `SpeechRecognitionProvider` interface.

### 2.3 Block-Based Processing (Completed Audio Segments)
The library intentionally **does not focus on streaming/partial real-time word-by-word transcription**. Processing always operates on **completed audio blocks**. This guarantees:
* Maximum recognition accuracy (models have full context of complete phrases and sentences).
* Significantly reduced battery drain and network payload.
* A robust, simplified, and reliable API surface.

### 2.4 User Interface Independence (UI Independent)
Logus Core does not bundle or enforce UI components (no hardcoded mic buttons, waveforms, audio visualizers, or Jetpack Compose UI elements). The library exposes clean reactive state and event streams (via Kotlin Coroutines and Flows) that host applications can bind to arbitrary custom interfaces.

---

## 3. Recording Modes

Logus Core provides two primary operational modes:

```
┌──────────────────────────────────────────────────────────────────┐
│                      Logus Core Recording Modes                  │
├─────────────────────────────────┬────────────────────────────────┤
│       1. Manual Recording       │   2. Automatic Recording (VAD) │
│                                 │    (Voice Activity Detection)  │
├─────────────────────────────────┼────────────────────────────────┤
│ • Started by the host app       │ • Started by the host app      │
│ • Continuous recording session  │ • Detects start of speech      │
│ • Stopped by the user / app     │ • Pause in speech = end of block│
│ • Full record = 1 audio block   │ • Segment transcribed in bg    │
│ • Transcription triggered on stop│ • Session continues listening │
└─────────────────────────────────┴────────────────────────────────┘
```

1. **Manual Recording:**
   * Ideal for push-to-talk buttons or dictating discrete voice notes.
   * When stopped, the entire recording is finalized into a single segment and sent for transcription.

2. **Automatic / Voice Activity Recording (VAD):**
   * Ideal for continuous thought capture and hands-free journaling.
   * User speaks -> speech is detected -> upon silence, the segment is finalized and submitted for transcription.
   * **Crucial Behavior:** Detected silence **does not terminate the recording session**; it only closes the current speech segment. When speech resumes, the active session captures the next segment.

---

## 4. High-Level Data Flow

```
[ Microphone ]
     │
     ▼
[ Audio Capture (speech-android) ]
     │
     ▼
[ VAD & Segmentation ]
     │
     ▼
[ Completed AudioSegment ]
     │
     ▼
[ Recognition Router ] ── (Evaluates language, quality, hardware, network, capabilities)
     │
     ▼
[ Selected Provider ] ── (Local On-Device or Remote Cloud Provider)
     │
     ▼
[ RecognitionResult ] ── (Delivered to host app via reactive event stream)
```

---

## 5. Sample Application
The repository includes a lightweight demonstration Android application (`sample-app`), which serves to:
* Facilitate manual and automated testing across physical devices and emulators.
* Demonstrate API integration patterns for external developers.
* Validate router decision-making, language switching, quality levels, and error handling.
