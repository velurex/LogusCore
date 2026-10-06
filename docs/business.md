# Logus Core – Business & Product Vision

## 1. Product Value & Vision
Mobile speech recognition on Android is traditionally fragmented across:
1. Vendor-locked cloud services with high ongoing operational costs and vulnerability to offline outages.
2. Fragmented system APIs (`android.speech.SpeechRecognizer`) whose behavior, availability, and accuracy vary wildly across device manufacturers and OS versions.
3. Complex proprietary implementations embedded directly within individual client applications.

**Logus Core** delivers a solution via an open-source framework that unifies on-device and remote cloud speech recognition under a single, cohesive developer API.

### Key Benefits:
* **Cost Optimization:** Enables seamless utilization of local on-device models whenever hardware and requirements permit, significantly reducing cloud transcription API expenses.
* **Privacy & Offline Operation:** Sensitive voice data does not have to leave the user's device when local models are active (privacy-first & GDPR compliant).
* **High Resilience:** Automatic failover to remote cloud providers when local hardware cannot satisfy accuracy or language constraints.
* **Unified Developer Experience:** Developers integrate a single stable SDK and control provider selection declaratively via quality and operational preferences.

---

## 2. Decoupling Library from Consumer Applications
Logus Core is developed in an independent repository and represents a self-contained open-source product.

### Decoupling Rules:
* Zero client-specific business logic in Logus Core (no concepts like `FreeUser`, `PremiumTier`, custom entity models, or subscription tracking).
* Zero hard dependencies on any client's proprietary backend infrastructure.
* Ready to be published as a standard Maven artifact for the broader Android developer community.

---

## 3. Security and API Key Management
From a security and financial governance standpoint, mobile applications adhere to a fundamental rule:
> **Mobile clients cannot securely store secret API keys for commercial cloud services.**

### Architectural Implications for Logus Core:
* The core `SpeechRecognitionProvider` interface **does not require** or enforce hardcoded secret keys.
* The architecture natively supports the **Backend Gateway** pattern:
  ```
  Android App (using Logus Core)
          │  (authenticated user session token)
          ▼
  Host Application Backend
          │  (securely held provider API secret)
          ▼
  Cloud Speech Provider (OpenAI, Deepgram, Google Cloud STT, etc.)
  ```
* For on-device local providers, no network permissions or authentication tokens are required.

---

## 4. Architectural Priorities

When resolving technical trade-offs, the following priority order strictly applies:

| Priority | Principle | Description |
| :---: | :--- | :--- |
| **1.** | **Independence from Host Applications** | Strictly generic code without ties to specific consumers. |
| **2.** | **Provider Neutrality** | Public interfaces must not favor any single STT engine. |
| **3.** | **Extensibility** | Straightforward addition of new providers without touching core logic. |
| **4.** | **Testability** | Complete business logic and routing testable in pure JVM. |
| **5.** | **UI Independence** | Zero bundled or mandatory UI components. |
| **6.** | **Offline & Local First** | First-class support for fully disconnected execution. |
| **7.** | **Intelligent Automated Routing** | Dynamic provider selection based on hardware, models, and languages. |
| **8.** | **Multi-Language Support** | Support for explicit language codes as well as auto-detection. |
| **9.** | **Low Memory & Resource Footprint** | Avoid holding uncompressed large audio buffers in RAM. |
| **10.** | **Minimal Public API Surface** | Lean, stable interface with internal implementations properly hidden. |

---

## 5. Licensing and Distribution
* Logus Core is licensed under the **Apache License 2.0** (see `LICENSE` in repository root).
* The license permits commercial and non-commercial adoption, modification, and redistribution without proprietary restrictions.
