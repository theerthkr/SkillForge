# Skillforge 📱

A lightweight, 3-screen Android learning app built with **Kotlin + Jetpack Compose** as part of an AI-first engineering assessment. The app follows a clean **Browse-to-Learn** flow:

**Home → Course Detail → Lesson Player**

Data is sourced from a single live JSON endpoint:
```
https://raw.githubusercontent.com/android-assesment/notes/refs/heads/main/data.json
```
The API returns a nested structure — `categories → courses → lessons` — and all querying happens on-device after a single fetch.

---

## Design → Build

<table>
<tr>
<th width="280">Design (given)</th>
<th width="40"></th>
<th width="280">Built</th>
</tr>
<tr>
<td><img src="images/design-home.png" width="260"/></td>
<td align="center">➡️</td>
<td><img src="images/built-home.jpeg" width="260"/></td>
</tr>
<tr>
<td><img src="images/design-course-detail.png" width="260"/></td>
<td align="center">➡️</td>
<td><img src="images/built-course-detail.jpeg" width="260"/></td>
</tr>
<tr>
<td><img src="images/design-lesson.png" width="260"/></td>
<td align="center">➡️</td>
<td><img src="images/built-lesson.jpeg" width="260"/></td>
</tr>
</table>

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

### Actual Prompts I Sent

**Prompts I used to get started**
> I am building an Android app in Kotlin. I need to fetch data from this URL: https://raw.githubusercontent.com/android-assesment/notes/refs/heads/main/data.json.The JSON structure is attached . Generate the necessary Retrofit interface, the Data Classes (using Kotlinx Serialization or Moshi), and a simple Repository class that uses Coroutines to fetch this data and returns a Kotlin Result or sealed class for Success/Error/Loading states.

> I am using Jetpack Compose. Write the code to define a custom Typography object using the 'Plus Jakarta Sans' font family for all text styles. Then, update my Theme.kt to enforce a strict Light Theme (no dark mode). Use a cream color (suggest a hex code) for the background, a dark teal for the primary color, and a lighter teal for secondary elements

> Write a Jetpack Compose screen called HomeScreen. It takes a UiState (Loading, Error, Success). In the Success state, it receives a list of Category objects.
Visual layout:
A top greeting text 'Welcome back' and a bold header 'Find your next skill'.
A search bar with a magnifying glass icon.
A 'Categories' section with a horizontally scrolling row of cards.
A 'Popular courses' section with a vertically scrolling list of cards.
For images, use the Coil library's AsyncImage.
Please write clean, modular Compose functions for the individual cards (e.g., CourseCard, CategoryCard) rather than putting everything in one massive function.

**Some of the prompta that I used to improve ui**
> I need to refactor the current Jetpack Compose lesson screen to match a new design reference.
Primary Goal: Integrate the 'Now Playing' state directly into the standard lesson list cards rather than keeping it as an isolated component. The core container structure must remain identical across all states.
Specific UI Requirements:
Component Spacing: Introduce clean, uniform spacing between all lesson cards in the list.
Navigation Tab States: Update the click behavior. The active tab text should shift to black, but the underline indicator must remain the existing primary teal color.
Active Video State: When a video is selected, modify the interior styling of that specific card (text color, icon swap, background tint) to reflect the active state, without altering the outer bounding box structure.

> I need to overhaul the CategoryCard composable to replace the standard Material icons with a custom abstract 'box-in-box' design.
Design Specifications:
Outer Container: A large, squarish bounding box with 20dp rounded corners and a subtle 1dp #E0E0E0 border.
Inner Container (The 'Icon'): Create a nested Box layout. The outer bounding box should use the category's accent color but with an 18% alpha transparency. Centered inside this, place a smaller Box using the same accent color at 100% opacity.
Content Preservation: Ensure the 'Category Name' and 'Course Count' text elements remain perfectly intact and properly padded below the new visual element.


### What the AI Got Right

The **offline caching architecture** was implemented correctly on the first attempt. The model understood immediately that we needed to serialise the full API response with `kotlinx.serialization`, write it to `context.filesDir`, and fall back to that file on any network failure — all without restructuring the existing ViewModel layer.

### What the AI Got Wrong — and How I Fixed It

The AI initially hardcoded `course.price() == 0.0` as the check for whether a course was free. This meant **every course appeared unlocked**, regardless of what the API actually said, which silently broke the payment flow.

The fix: classify courses based purely on the `isFree` field on individual lessons, which is what the API actually provides. Once I pointed that out explicitly, the model corrected it cleanly.

### What AI Couldn't Do

Matching the UI **exactly** to the given design — spacing, padding, the small subtle stuff — wasn't something AI could really do on its own. It can't see what's actually being rendered on screen, so when I asked it to fix specific layout details, it was mostly guessing: it would shift one thing and break another, or "fix" a padding value that didn't even need fixing. After a few rounds of that going nowhere, I just did the pixel-level matching myself — comparing the design screenshots against the running app side by side and adjusting spacing/padding manually until it matched.

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