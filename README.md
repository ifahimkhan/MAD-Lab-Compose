# 🤖 Gemini Memory Chatbot

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Kotlin%202.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/AI-Google%20Gemini%201.5-8E75FF?style=for-the-badge&logo=google&logoColor=white" />
  <img src="https://img.shields.io/badge/Database-Room%20(SQLite)-FFA000?style=for-the-badge&logo=sqlite&logoColor=white" />
</p>

A clean, modern Android Chatbot built with **Kotlin**, **Jetpack Compose (Material 3)**, **Google Gemini 1.5 API**, and **Room Database**. It features persistent memory that remembers user information across app restarts, voice interaction, and chat export.

---

## 🌟 Features

### 1. Chatbot
Communicates directly with Google's **`gemini-1.5-flash`** model via Retrofit and Kotlin Coroutines. Stored user facts are dynamically injected into system instructions so Gemini maintains personalized context across conversations.

<p align="center">
  <img src="screenshots/00_chat_conversation.png" width="300" alt="Chatbot Conversation" />
</p>

---

### 2. Memory
Automatically detects and extracts personal facts (e.g., name, college, courses, interests) as you chat and stores them in a local SQLite database using Room.
- **Persistent Across Restarts**: Closing or restarting the app retains all learned user facts.
- **Memory Dashboard**: Tap the brain (🧠) icon in the top bar to view, add, or delete stored memories.

<p align="center">
  <img src="screenshots/01_memory_dashboard.png" width="300" alt="Memory Dashboard" />
</p>

---

### 3. Voice to Text
- **Speech Input**: Tap the microphone (🎙️) button to speak your queries directly.
- **Runtime Permissions**: Requests microphone access on demand using Jetpack Compose permission handling.
- **Text-to-Speech**: Tap the speaker (🔊) icon on any bot response to hear Gemini read the answer aloud.

<p align="center">
  <img src="screenshots/03_mic_permission.png" width="280" alt="Microphone Permission" />
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="screenshots/02_voice_listening.png" width="280" alt="Voice Listening State" />
</p>

---

### 4. Share Conversation
Tap the share (📤) button in the top bar to export a formatted transcript of the conversation with timestamps. Uses Android's native share sheet to send your chat log to WhatsApp, Email, Notes, or any app.

<p align="center">
  <img src="screenshots/04_share_conversation.png" width="300" alt="Share Conversation Sheet" />
</p>

---

### 5. Clear Chat History
Easily wipe the active conversation using the trash (🗑️) button. A confirmation dialog prevents accidental clicks while ensuring your learned Room database facts remain safely preserved.

<p align="center">
  <img src="screenshots/05_clear_chat_dialog.png" width="300" alt="Clear Chat Confirmation" />
</p>

---

## 🏗️ Architecture

The app follows Google's recommended **MVVM** and **Unidirectional Data Flow (UDF)** architecture:

```text
app/src/main/java/com/fahim/mad_lab_compose/
├── MainActivity.kt                  # Entry point with Compose permission handling & screen routing
├── ChatbotApplication.kt            # App class initializing Room DB and Repository singletons
├── data/
│   ├── api/
│   │   ├── ApiClient.kt             # OkHttp & Retrofit network client
│   │   ├── GeminiApiService.kt      # Gemini REST API interface
│   │   └── GeminiModels.kt          # Request & Response data models
│   ├── database/
│   │   ├── AppDatabase.kt           # Room Database with SQLite tables
│   │   ├── MemoryDao.kt             # DAO for user memories
│   │   ├── MessageDao.kt            # DAO for chat messages
│   │   ├── SummaryDao.kt            # DAO for AI summaries
│   │   └── Entities.kt              # Room entities
│   └── repository/
│       ├── ChatRepository.kt        # Repository bridging Room DB and Gemini API
│       └── MemoryExtractor.kt       # Pattern extraction engine for user facts
├── ui/
│   ├── ChatScreen.kt                # Main Chat UI composables & previews
│   ├── MemoryScreen.kt              # Memory Management Dashboard UI
│   ├── ApiKeyDialog.kt              # In-app API key setup dialog
│   ├── ChatViewModel.kt             # ViewModel managing StateFlow<ChatState>
│   ├── ChatState.kt                 # Immutable UI state
│   └── theme/                       # Material 3 Color Schemes & Theme
└── voice/
    └── VoiceHelper.kt               # SpeechRecognizer (STT) and TextToSpeech (TTS) manager
```

---

## 🚀 Quick Setup

### 1. Obtain a Gemini API Key
1. Visit [Google AI Studio](https://aistudio.google.com/) and create a free API key.
2. Open `local.properties` in the project root directory.
3. Add your key:
   ```properties
   GEMINI_API_KEY=your_actual_api_key_here
   ```
*(Note: `local.properties` is git-ignored and never committed to version control. You can also configure the key inside the app via the Key 🔑 icon in the top toolbar).*

---

### 2. Run the App
1. Open the project in **Android Studio**.
2. Select your device or emulator (e.g., `Medium Phone API 35`).
3. Click the green **Run (▶)** button (or press `Shift + F10`).
