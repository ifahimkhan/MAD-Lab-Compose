# Gemini API Compose Starter — Assignment 1

## Setup

1. Get a Gemini API key from [Google AI Studio](https://ai.google.dev/gemini-api/docs/quickstart).
2. Copy `local.properties.example` to `local.properties`.
3. Replace the placeholder with your real key: `GEMINI_API_KEY=your_api_key_here`
4. `local.properties` is git-ignored and will never be committed.

## API key security

- The key is read from `local.properties` at build time (`app/build.gradle.kts`) and
  exposed as `BuildConfig.GEMINI_API_KEY`. It is never hardcoded in Kotlin, XML, or
  committed Gradle files.
- If `local.properties` has no key (e.g. on a CI server), the build falls back to the
  `GEMINI_API_KEY` environment variable, which CI can supply as a repository secret.
- On first launch, the app copies the key from `BuildConfig` into `SecureKeyStore`
  (`data/SecureKeyStore.kt`), which encrypts it with AES-256-GCM using a key generated
  inside the Android Keystore, via `EncryptedSharedPreferences`. Only the ciphertext is
  written to disk.
- The key is decrypted in memory only at the moment `GenerativeModel` is created in
  `GeminiRepositoryImpl`. It is never logged, toasted, or displayed.
- Release builds have `isMinifyEnabled = true`, so R8 obfuscates the APK and the key is
  not trivially readable from decompiled code.

### Limits of this approach

Client-side encryption raises the bar for casually inspecting the APK, but it cannot
fully hide a key from a determined attacker who has the device: the key is decrypted
in memory at some point, and a rooted device or a debugger attached to the app process
can still recover it. A production app should not ship a client-facing Gemini key at all;
instead it should:

- Route Gemini calls through a backend proxy the app authenticates to, so the Gemini key
  itself never leaves the server, or
- Use Firebase App Check together with a restricted API key, so only requests from a
  verified instance of the real app are accepted, limiting what a key does if extracted.

## Running tests

- Unit tests: `./gradlew testDebugUnitTest`
- Instrumented / Compose UI tests: `./gradlew connectedDebugAndroidTest` (requires a
  running emulator or connected device)
