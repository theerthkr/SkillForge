# SkillForge 📱

A 3-screen Android learning app built with **Kotlin**, **Jetpack Compose**, and modern Android architecture — developed as part of an AI-assisted engineering assessment.

---

## What We Built

SkillForge is a clean, polished course-browsing app with three main screens:

**Home → Course Detail → Lesson Player**

Data is sourced from a single live JSON endpoint:

```
https://raw.githubusercontent.com/android-assesment/notes/refs/heads/main/data.json
```

The API returns a nested structure: `categories → courses → lessons`. All querying (find course by ID, find lesson by ID) happens on-device after a single network fetch.

---

## Feature Breakdown

### 🌐 API Integration (Retrofit + kotlinx-serialization)
- Connected via **Retrofit** with **kotlinx-serialization** for JSON parsing.
- A single `GET` call fetches the entire catalog — no per-resource endpoints needed.
- The `SkillforgeRepository` exposes `getCategories()`, `getCourseById()`, and `getLessonById()` as suspend functions backed by this single network call.

### 📦 Offline Caching
- On every successful network fetch, the full JSON response is serialised and written to `catalog_cache.json` in the app's internal files directory (`context.filesDir`).
- On subsequent launches, if the device is **offline**, the app reads from this cached file and renders the UI instantly — no spinner, no error.
- If the user clears the app's storage (cache/data), the cache file is removed. The app will require a network connection on the next launch to restore data.
- When connectivity is restored, the app silently re-fetches the endpoint and overwrites the cache file with the latest data, keeping the UI always up to date.

### 🏠 Screen 1 — Home
- Displays all **categories** as horizontal, tappable cards (Android Development, Backend & APIs, Product & UI Design).
- Tapping a category **filters the "Popular Courses" section** to show only that category's courses. Tapping again deselects and shows all.
- Full-text **search bar** filters courses across title, instructor name, and level.
- Uses **Coil** to load all thumbnail and avatar images from their API URLs.

### 📚 Screen 2 — Course Detail
- Displays hero image, course metadata, instructor card, and a scrollable lesson list.
- Each lesson is marked **Free** or **Locked** based on the `isFree` field in the API.
- A sticky footer shows the enrolment CTA:
  - If any lesson is free → **"Start Learning"** (goes straight to first lesson).
  - If all lessons are paid → **"Enrol Now"** (goes to the payment screen).
- Once a course is purchased, the sticky footer **disappears** and all locked lessons become tappable.

### 🎬 Screen 3 — Lesson Player
- Displays a simulated video player (gradient header with play/pause toggle, scrubber, and time display) since the API returns no real video URLs.
- Contains three **horizontal tap-to-switch tabs**:
  - **Lessons** — scrollable list of all course lessons; free ones play immediately, locked ones route to payment (unless the course is already purchased).
  - **Notes** — a full-height `OutlinedTextField` where the user can write and save notes for the current lesson. Saving shows a toast confirmation and dismisses the keyboard.
  - **Resources** — placeholder: *"No resources for this course yet."*

### 💳 Payment Screen
A realistic mock checkout page with proper UX and real input validation.

**Input Formatting:**
- **Card Number** — numeric keyboard only; raw digits capped at 16; auto-displayed as `1234 5678 9012 3456` via a `VisualTransformation` (spaces are visual only, not stored in state).
- **Expiry Date** — raw 4-digit input (MMYY); auto-displayed as `MM/YY` once two digits are typed; capped at 4 digits.
- **CVV** — numeric keyboard only; capped at 4 digits.

**Validation (in order on Pay Now):**
1. All fields must be non-empty.
2. Card number must be 13–16 digits.
3. Expiry month must be 01–12.
4. Expiry date must not be in the past → *"Your card has expired."*
5. CVV must be 3–4 digits.

On success → shows a success screen and calls `repository.unlockCourse(courseId)`.

### 🔓 Course Unlock Persistence
- Purchased courses are stored in **`SharedPreferences`** under the key `unlocked_{courseId}`.
- State persists across app restarts.
- Once unlocked, the course detail screen hides the payment footer and grants access to all lessons — both on the Course Detail screen and in the Lesson screen's playlist.

### 📝 Note Persistence
- Notes are stored per-lesson in **`SharedPreferences`** under the key `note_{courseId}_{lessonId}`.
- When a user switches to a different lesson, the notes field automatically loads that lesson's previously saved note.
- Tapping **Save Note** writes to `SharedPreferences`, dismisses the keyboard, clears focus, and shows a *"Note saved!"* toast.

### 🧪 Testing

**Local Unit Tests (`src/test/`):**
- `HomeViewModelTest` — verifies that:
  - Loading the catalog transitions the `HomeViewModel` UI state to `Success` with the correct category data.
  - A network failure (`IOException`) with no local cache transitions state to `Error` with the correct message.
- `SkillforgeRepositoryTest` — verifies repository-level caching and data-retrieval logic.

**Instrumented Android Tests (`src/androidTest/`):**
- `ExampleInstrumentedTest` — runs on a connected device; verifies the app package name resolves correctly against the Android context.
- Run with: `./gradlew connectedAndroidTest`
- Output confirms: *"Finished 1 tests on Pixel 6a — BUILD SUCCESSFUL"*

### 🎨 UI & Design
- Light theme with **cream background** (`#FDF8F0`) and **dark teal** (`#0F5353`) accent, matching the provided design spec.
- Typography: **Plus Jakarta Sans** (loaded via Google Fonts).
- Category cards highlight on selection with a coloured border and subtle background tint.
- Loading and error states are implemented on every screen.

---

## How I Used AI

This project was built primarily with **Google Antigravity / Gemini** as the AI pair-programming tool. The development followed an iterative, conversational workflow — essentially a long-running AI-assisted coding session.

### Actual Prompts Sent

1. *"I don't think the API implementation is complete or correct. The JSON file fetched through the API contains multiple courses that are not available in the app, while the app includes courses missing from the API JSON. Here is the GitHub link again: `https://raw.githubusercontent.com/android-assesment/notes/refs/heads/main/data.json`. Please reconnect the API using Retrofit and kotlinx-serialization, update the implementation, and remove any posts that are not present in the API JSON."*

2. *"The payment transfer page is not working on the second page. It still plays the locked paid video on the third page. Also fix on the second page so that if the user clicks on the paid item, it takes them to the paid page. Add validation to the payments page, and if the payment is completed, make sure the locked video opens."*

3. *"I want the app to work when it is first launched by the user. It should fetch data from the API and save it into the app's cache. If the user launches the app offline, it doesn't need to go online just to fetch the videos. If there is an update in the JSON API key that we are fetching from, the update should reflect in the app."*

4. *"Third page, the save note button is not working. Check the payment page and improve its UX. The card-number field lacks auto-spacing and does not limit the number of digits. The expiry-date field has no automatic formatting. Update the validations and add appropriate formatting and length limits for these fields."*

### One Thing AI Got Right

The offline caching architecture was implemented cleanly on the first attempt — reading the full JSON response, serialising it with `kotlinx.serialization`, writing it to `filesDir`, and falling back to it on network failure, all wired into the existing repository without restructuring the ViewModel layer.

### One Thing AI Got Wrong — and How We Fixed It

The AI initially hardcoded `course.price() == 0.0` as a fallback in the "is this course free?" check, which made every course appear free regardless of its lesson data. This caused the payment screen to be bypassed entirely. The fix was to base the free/paid classification purely on whether any lesson in the course has `isFree = true` — which is what the API actually provides.

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
