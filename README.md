# SkillForge

A 3-screen learning app built with Kotlin, Jetpack Compose, and modern Android architecture.

## How I used AI to build this

This project was built with the assistance of AI tools.

### Which AI tools I used:
- Google Antigravity / Gemini
- (Add any others you used here)

### 2–3 of the actual prompts I sent:
1. *"Fix the buttons in the categories section..."*
2. *"The payment transfer page is not working on the second page. It still plays the logged paid video on the third page..."*
3. *(Add one of your earliest prompts here, e.g., "Build a 3-screen Android app called Skillforge...")*

### One thing AI got right, and one thing it got wrong — and how I fixed it:
- **Right:** The UI generation and architecture (Retrofit, ViewModels, Compose layouts) were spot-on. It successfully integrated offline caching with SharedPreferences and file storage.
- **Wrong:** Initially, the AI missed making the category buttons functional to filter the courses. Also, the payment screen logic wasn't fully wired to unlock the videos globally.
- **Fix:** I prompted the AI to add a `selectedCategory` state in `HomeScreen.kt` to filter the course list and strictly enforce the `isUnlocked` state from the repository in both the `CourseDetailScreen` and `LessonScreen`.

## Building the project
- Developed in Android Studio using Kotlin and Jetpack Compose.
- Networking powered by Retrofit.
- Image loading with Coil.
- Offline support via local JSON file caching.
