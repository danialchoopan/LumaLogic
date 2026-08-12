# LumaLogic 💡⚡

**LumaLogic** is an advanced 256-level optical logic puzzle game built with Kotlin and Jetpack Compose for Android. Players route laser light beams across grid matrices using movable mirrors, beam splitters, RGB color filters, energy thrift constraints, and optical logic gates (AND, OR, NOT).

---

## 🌟 Key Features

- **256 Built-In Puzzle Levels**: Distributed across 16 thematic chapters with progressive difficulty curve.
- **16 Campaign Chapters**:
  1. *Light Basics* (Levels 1–16)
  2. *Reflection & Angles* (Levels 17–32)
  3. *Precision & Constraints* (Levels 33–48)
  4. *Prism Splitters* (Levels 49–64)
  5. *Spectrum Colors* (Levels 65–80)
  6. *Chromatic Filters* (Levels 81–96)
  7. *Energy Thrift* (Levels 97–112)
  8. *Multi-Beam Array* (Levels 113–128)
  9. *AND Conjunction* (Levels 129–144)
  10. *OR Redundancy* (Levels 145–160)
  11. *NOT Inversion* (Levels 161–176)
  12. *Logic Networks* (Levels 177–192)
  13. *Expert Routing* (Levels 193–208)
  14. *Master Energy* (Levels 209–224)
  15. *Expert Conundrum* (Levels 225–240)
  16. *LumaLogic Master* (Levels 241–256)
- **Daily Puzzle Mode**: A deterministic daily challenge puzzle generated automatically each day.
- **Achievements System**: Local persistent achievements for level completion milestones, star collecting, energy management, and flawless solutions.
- **Level Editor & Custom Levels**: Create, edit, test, export, and import user-created puzzle levels.
- **Search, Filters & Favorites**: Search levels by name, filter by difficulty (ALL, FAVORITES, BEGINNER, EASY, NORMAL, HARD, EXPERT, MASTER), and preview puzzle specs before starting.
- **Player Statistics Profile**: Monitor overall completion percentage, star collection stats (up to 768 stars), total play time, hints used, and highest scores.

---

## 🛠️ Technology Stack

- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM / Repository Pattern with Constructor Injection
- **Navigation**: Jetpack Navigation Compose
- **Data Persistence**: Local JSON file storage using Moshi
- **Build System**: Gradle (Kotlin DSL)

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK version 34 (Minimum SDK: 24)

### Building & Running

1. Clone or import the repository into Android Studio.
2. Ensure Kotlin plugin & KSP are configured.
3. Build the project via `./gradlew assembleDebug` or run directly on an Android device / emulator.

---

## 🎯 Architecture & Package Structure

```
ir.danialchoopan.lumalogic
├── data
│   ├── level           # Level definitions, Chapter definitions & LevelRegistry (256 levels)
│   ├── model           # Cell, GridModel, Level, Chapter, Achievement, PlayerStats, etc.
│   └── repository      # Repositories for Levels, Progress, Achievements, Favorites, Daily Puzzles
├── di                  # Centralized dependency container (AppContainer)
├── domain              # GameEngine raytracer logic and LevelProgressManager
├── ui
│   ├── components      # Reusable Jetpack Compose UI components (LumaHeader, GlowingCard, LumaButton)
│   ├── navigation      # NavGraph and screen destination routes
│   ├── screens         # Home, ChapterSelect, LevelSelect, DailyPuzzle, Achievements, Profile, Game, Editor, Settings, About
│   └── theme           # Material3 ColorScheme, Typography, and Shapes
```

---

## 📄 License

LumaLogic is licensed under the MIT License.
