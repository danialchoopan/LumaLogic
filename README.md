# LumaLogic

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF.svg?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android_14_--_SDK_34-3DDC84.svg?style=flat&logo=android&logoColor=white)](https://developer.android.com)
[![Compose](https://img.shields.io/badge/UI-Jetpack_Compose_M3-4285F4.svg?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**LumaLogic** is a premium optical logic puzzle game for Android, featuring 256 unique levels across 16 thematic chapters. Built with Kotlin and Jetpack Compose Material 3.

Players guide laser light beams through complex grid matrices using movable mirrors, beam splitters, RGB spectrum filters, energy budgets, and digital logic gates to power designated optical targets.

---

## Table of Contents

- [Features](#features)
- [Chapters](#chapters)
- [Game Components](#game-components)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Localization](#localization)
- [Credits](#credits)
- [License](#license)

---

## Features

### Core Gameplay
- **256 Handcrafted Levels** across 16 thematic chapters with progressive difficulty
- **Procedural Generation** for chapters 2-16 using mathematical algorithms (golden ratio, seeded PRNG)
- **Anti-Auto-Solve Verification** ensures every puzzle starts unsolved
- **3-Star Rating System** encouraging optimal move and energy management

### Game Mechanics
- **Mirror Reflection** - Rotate mirrors to deflect light beams by 90 degrees
- **Beam Splitters** - Divide single rays into parallel branches
- **Color Filters** - Selective wavelength barriers for RGB routing
- **Logic Gates** - AND, OR, NOT gates for complex circuit puzzles
- **Energy Management** - Strict photon budgets requiring efficient pathfinding

### UI/UX
- **Animated Light Effects** - Rotating beams and pulsing glows
- **Game-like Interface** - Neon aesthetics, arcade-style level nodes
- **Smooth Animations** - Haptic feedback and visual transitions
- **Dark Theme Support** - Full Material 3 dark mode

### Progression
- **Chapter Unlock System** - Complete 12 levels to unlock the next chapter
- **Star Collection** - Earn up to 768 stars across all levels
- **Daily Puzzles** - Unique algorithmically-generated challenges each day
- **Level Editor** - Create, validate, and share custom levels
- **Achievements** - Track milestones and accomplishments

---

## Chapters

| # | Chapter | Difficulty | Levels | Mechanics |
|:---:|:---|:---:|:---:|:---|
| 01 | Light Basics | BEGINNER | 1-16 | Single/Double mirror reflections, basic ray deflection |
| 02 | Reflection | EASY | 17-32 | Multi-bounce angles, perimeter reflections |
| 03 | Precision | EASY | 33-48 | Minimal move budgets, narrow corridors |
| 04 | Splitters | NORMAL | 49-64 | Dual beam division, multi-target activation |
| 05 | Colors | NORMAL | 65-80 | RGB wavelength routing, multi-source puzzles |
| 06 | Filters | NORMAL | 81-96 | Selective wavelength barriers |
| 07 | Energy | NORMAL | 97-112 | Strict photon energy limits |
| 08 | Multi-Beam | HARD | 113-128 | Dual orthogonal laser emitters |
| 09 | AND Logic | HARD | 129-144 | Dual concurrent beam requirements |
| 10 | OR Logic | HARD | 145-160 | Alternate routing pathways |
| 11 | NOT Logic | HARD | 161-176 | Active signal inversion |
| 12 | Logic Networks | EXPERT | 177-192 | Cascading AND/OR combinations |
| 13 | Expert Routing | EXPERT | 193-208 | Dense 7x7 and 8x8 labyrinths |
| 14 | Master Energy | EXPERT | 209-224 | Micro energy budgets |
| 15 | Expert Conundrum | MASTER | 225-240 | Integrated multi-mechanic puzzles |
| 16 | LumaLogic Master | MASTER | 241-256 | Grand Finale culminating at Level 256 |

---

## Game Components

| Component | Description |
|:---|:---|
| **Light Source** | Emits laser beams in cardinal directions with distinct wavelengths (White, Red, Blue, Green, Yellow) |
| **Mirror** | Reflects incoming light by 90 degrees based on angular orientation |
| **Splitter** | Divides an incoming ray into two orthogonal beams |
| **Filter** | Allows only matching wavelengths to pass through |
| **Block** | Solid impassable obstacles requiring clever routing |
| **AND Gate** | Emits output only when both inputs receive illumination |
| **OR Gate** | Emits output when either input receives illumination |
| **NOT Gate** | Emits light when unilluminated; turns off when input enters |
| **Target** | Light sensor that triggers completion when illuminated with correct wavelength |

---

## Architecture

```
ir.danialchoopan.lumalogic/
├── data/
│   ├── level/          # LevelRegistry, LevelGenerator, Chapter definitions
│   ├── model/          # Cell, Level, Chapter, Achievement, PlayerStats
│   └── repository/     # LevelRepository, ProgressRepository
├── di/                 # AppContainer dependency injection
├── domain/
│   ├── engine/         # GameEngine, GridEngine raytracing
│   ├── hint/           # BFS Hint Engine
│   └── level/          # LevelValidator, LevelProgressManager
├── ui/
│   ├── components/     # GameCanvas, LumaHeader, GlowingCard
│   ├── localization/   # Persian/English support, digit conversion
│   ├── navigation/     # NavGraph, route definitions
│   ├── screens/        # Home, Game, ChapterSelect, LevelSelect, Editor
│   └── theme/          # Material3 ColorScheme, typography
```

### Key Technical Features
- **Custom Raytracer** - 2D vector-based iterative raytracer with color mixing and loop detection
- **Seeded PRNG** - Mulberry32 algorithm for reproducible level generation
- **Golden Ratio Placement** - Path generation using 137.508° golden angle
- **Command Pattern** - Undo/redo system for game operations
- **Moshi Serialization** - JSON-based level import/export

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Kotlin 1.9+
- Android SDK 34

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/danialchoopan/LumaLogic.git
   ```

2. Open the project in Android Studio

3. Sync Gradle and build the project

4. Run on an emulator or physical device (Android 14+)

---

## Localization

LumaLogic supports **English** and **Persian (Farsi)** with:
- Full RTL layout support for Persian
- Automatic Persian numeral conversion (0-9 to ۰-۹)
- Context-aware translations for all UI elements
- Dynamic locale switching without app restart

---

## Credits

- **Developer**: Danial Choopan
- **Framework**: Jetpack Compose & Android SDK
- **Language**: Kotlin

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
