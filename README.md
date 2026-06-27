# Skillforge 📱

A lightweight, 3-screen Android learning app built with **Kotlin + Jetpack Compose** as part of an AI-first engineering assessment. The app follows a clean **Browse-to-Learn** flow:

**Home → Course Detail → Lesson Player**

Data is sourced from a single live JSON endpoint:
```
https://raw.githubusercontent.com/android-assesment/notes/refs/heads/main/data.json
```
The API returns a nested structure — `categories → courses → lessons` — and all querying happens on-device after a single fetch.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM (ViewModel + StateFlow) |
| Networking | Retrofit 2 |
| JSON Parsing | kotlinx.serialization |
| Image Loading | Coil |
| Navigation | Jetpack Navigation Compose |
| Persistence | SharedPreferences (unlock state + notes), Internal File Storage (catalog cache) |
| Testing | JUnit 4, Kotlin Coroutines Test, AndroidX Instrumented Tests |

---

## Features

- **Home Screen** — Category filter cards + course grid with thumbnail, level, title, instructor, and rating. Full-text search across title, instructor, and level.
- **Course Detail** — Hero image, instructor card, scrollable lesson list with Free/Locked badges. Sticky CTA footer: *Start Learning* (if any lesson is free) or *Enrol Now* (if all are paid). Footer disappears once the course is purchased.
- **Lesson Player** — Simulated video player (play/pause, scrubber, time display) with three tabs: Lessons, Notes (per-lesson, persisted to SharedPreferences), and Resources.
- **Payment Screen** — Mock checkout with real input validation: card number auto-spaced via `VisualTransformation`, expiry auto-formatted as MM/YY, CVV capped at 4 digits. On successful payment, the course unlocks and persists across restarts.
- **Offline Cache** — On first launch, the full JSON is written to `filesDir/catalog_cache.json`. If the device is offline on subsequent launches, the app reads the cache instantly instead of showing an error. The cache is silently refreshed whenever connectivity is available.
- **Loading + Error States** — Every screen handles both.

---

## How I Used AI

Rather than relying on a single AI assistant, I ran a distributed, multi-model workflow — routing different phases of the build to whichever model was best suited for the task. Prompts were composed using **Wispr Flow** (voice-to-text), which let me speak engineering intent naturally and get it converted into clean, structured prompts without stopping to type everything out.

### AI Tools & Model Breakdown

| Phase | Tool / Environment | Model | What It Did |
|---|---|---|---|
| Architecture & orchestration | Antigravity | Claude (via Anthropic API) | Overall build plan, dependency boundaries, screen-state design, and acting as the high-level orchestrator throughout the project |
| Core code generation | OpenCode | DeepSeek V4 Flash (via OpenRouter / Mistral API) | Scaffolding screens, Retrofit setup, ViewModels, navigation graph, repository layer |
| Frontend & UI polish | Antigravity | Gemini 3.5 Flash / Gemini 3.1 Pro | Component layouts, color tokens, typography, spacing adjustments, payment screen UX |
| Testing | Antigravity CLI + SDK (agent mode) | Gemini | Generating unit tests for HomeViewModel and SkillforgeRepository, mocking network failure states, running the test suite via agents |
| Verification & Git push | OpenCode | GPT OSS 120B (via OpenRouter) | Final code review pass, catching any remaining issues, then pushing the finished code to GitHub |
| Prompt input | Wispr Flow | — | Voice dictation → structured prompts, used across all of the above |

### Git Hygiene Convention

One deliberate workflow rule I kept throughout: **AI only pushes to the `main` branch, never directly to `master`**. The repo's default branch is `master`, so this separation created a natural checkpoint — AI-generated commits land on `main`, I review the diff manually, and only then merge into `master`. It kept me in control of what actually shipped without slowing down the AI-assisted iteration loop.

---

### Actual Prompts I Sent

**Prompt 1 — Fixing the API integration:**
> *"I don't think the API implementation is complete or correct. The JSON file fetched through the API contains multiple courses that are not available in the app, while the app includes courses missing from the API JSON. Here is the GitHub link again: `https://raw.githubusercontent.com/android-assesment/notes/refs/heads/main/data.json`. Please reconnect the API using Retrofit and kotlinx-serialization, update the implementation, and remove any posts that are not present in the API JSON."*

**Prompt 2 — Payment flow and lesson unlock:**
> *"The payment transfer page is not working on the second page. It still plays the locked paid video on the third page. Also fix on the second page so that if the user clicks on the paid item, it takes them to the paid page. Add validation to the payments page, and if the payment is completed, make sure the locked video opens."*

**Prompt 3 — Offline caching:**
> *"I want the app to work when it is first launched by the user. It should fetch data from the API and save it into the app's cache. If the user launches the app offline, it doesn't need to go online just to fetch the videos. If there is an update in the JSON API key that we are fetching from, the update should reflect in the app."*

---

### What the AI Got Right

The **offline caching architecture** was implemented correctly on the first attempt. The model understood immediately that we needed to: serialise the full API response with `kotlinx.serialization`, write it to `context.filesDir`, and fall back to that file on any network failure — all without restructuring the existing ViewModel layer. It was a non-trivial bit of state coordination and it landed cleanly.

---

### What the AI Got Wrong — and How I Fixed It

The AI initially hardcoded `course.price() == 0.0` as the check for whether a course was free. This meant **every course appeared unlocked** regardless of what the API actually said, which silently broke the entire payment flow — lessons that should've been locked were just playing freely.

The fix: classify courses based purely on the `isFree` field on individual lessons, which is what the API actually provides. Once I pointed that out explicitly in the prompt, the model corrected it cleanly. The lesson here is that AI tends to fill in gaps with plausible-sounding defaults rather than stopping to ask — so you have to actively test the edge cases yourself rather than assuming the happy path works.

---

## Project Structure

```
app/src/main/java/com/theerthkr/skillforge/
├── data/
│   ├── model/          # SkillforgeModels.kt (Category, Course, Lesson, Instructor)
│   ├── remote/         # SkillforgeApi.kt, NetworkModule.kt
│   └── repository/     # SkillforgeRepository.kt (caching, unlock, notes)
├── navigation/         # SkillforgeNavigation.kt
├── screens/
│   ├── home/           # CategoryCard.kt, CourseCard.kt
│   ├── coursedetail/   # LessonListItem.kt
│   ├── HomeScreen.kt
│   ├── CourseDetailScreen.kt
│   ├── LessonScreen.kt
│   └── PaymentScreen.kt
├── ui/theme/           # Color.kt, Type.kt, Theme.kt
├── viewmodel/          # HomeViewModel, CourseDetailViewModel, LessonViewModel
├── SkillforgeApplication.kt
└── MainActivity.kt
```

---

## Getting Started

```bash
git clone https://github.com/theerthkr/SkillForge.git
```

Open in **Android Studio Ladybug** (or newer), let Gradle sync, then run on a device or emulator.

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest
```
