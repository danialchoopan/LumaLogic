# LumaLogic 💡⚡ (لوما لاجیک)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF.svg?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android_14_--_SDK_34-3DDC84.svg?style=flat&logo=android&logoColor=white)](https://developer.android.com)
[![Compose](https://img.shields.io/badge/UI-Jetpack_Compose_M3-4285F4.svg?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Language](https://img.shields.io/badge/Localization-Persian_%26_English-FF9800.svg?style=flat)](#localization--دو-زبانه)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**LumaLogic** is a premium, 256-level optical logic puzzle game developed with **Kotlin** and **Jetpack Compose (Material 3)** for Android by **Danial Choopan**. 

Players guide laser light beams through complex grid matrices using movable mirrors, beam splitters, RGB spectrum filters, energy budgets, and digital logic gates (**AND**, **OR**, **NOT**, **XOR**) to power designated optical targets.

---

## 🇮🇷 معرفی به زبان فارسی (Persian Overview)

**لوما لاجیک (LumaLogic)** یک بازی فکری و معمایی پیشرفته در سبک پازل‌های اپتیک و مدار‌های منطقی است که با **کاتلین** و **جت‌پک کامپوز** برای اندروید توسعه داده شده است.

در این بازی، شما پرتوهای نور لیزر را در ماتریس‌های شبکه‌ای هدایت می‌کنید و با چرخاندن و جابه‌جایی آینه‌های بازتابی، منشورهای شکافنده نور، فیلترهای تفکیک رنگ (RGB)، گیت‌های منطقی (AND / OR / NOT) و مدیریت میزان مصرف انرژی، اهداف نوری را روشن کرده و معماهای چالش‌برانگیز را حل می‌نمایید.

### ✨ ویژگی‌های برجسته:
- 💡 **۲۵۶ مرحله منحصر‌به‌فرد و دست‌ساز**: ۱۶ فصل متنوع با ساختار هندسی و چالش اختصاصی برای هر مرحله.
- 🔒 **سیستم پیشروی مرحله‌به‌مرحله و قفل فصول**: باز شدن ترتیبی مراحل و الزام به گذراندن فصول قبلی برای ورود به قلمروهای بعدی.
- 🚀 **دکمه هوشمند «شروع بازی / ادامه بازی» در صفحه اصلی**: ادامه مستقیم از آخرین مرحله باز شده یا شروع ماجراجویی از مرحله ۱.
- 🗺️ **نقشه فصول و شبکه آرکید مراحل (Game-like UI)**: رابط کاربری جذاب و گیمینگ با انیمیشن‌های نئونی، پالس مرحله فعال و کارت‌های قلمروهای اپتیک.
- ⭐ **سیستم رتبه‌بندی ۳ ستاره و روش‌های چندگانه حل**: امکان حل با مسیرهای مختلف و تشویق به بهینه‌سازی حرکات و مصرف انرژی برای دریافت ۳ ستاره طلایی.
- 🌐 **پشتیبانی کامل و یکپارچه از زبان فارسی و انگلیسی**: با تبدیل هوشمند اعداد به ارقام فارسی (`۰-۹`)، راست‌چین‌سازی دقیق و ترجمه روان تمام دیالوگ‌ها، نام مراحل و توضیحات دستاوردها.
- 🧩 **حالت معماهای روزانه (Daily Puzzle)**: تولید الگوریتمی یک مرحله اختصاصی و تازه برای هر روز سال با قابلیت ثبت امتیاز روزانه.
- 🎨 **ویرایشگر مراحل اختصاصی (Level Editor)**: ابزار پیشرفته برای طراحی، اعتبارسنجی زنده، تست و اشتراک‌گذاری مراحل دلخواه.
- 🏆 **سیستم دستاوردها و نشان‌های افتخار**: ردیابی پیشرفت، جمع‌آوری تا سقف ۷۶۸ ستاره و ثبت رکوردهای شخصی.
- 📊 **پروفایل و آمار پیشرفته بازیکن**: محاسبه درصد پیشروی در تمام فصول، زمان سپری‌شده و ستاره‌های کسب‌شده.
- ⚡ **مدیریت انرژی و بهینه‌سازی گام‌ها**: حل معماها با کمترین چرخش برای کسب ۳ ستاره طلایی.
- 🎵 **افکت‌های صوتی تعاملی و ویبره هپتیک**: بازخورد لمسی در چرخش قطعات و بازتاب نور.

---

## 🌟 16 Thematic Campaign Chapters (256 Unique Levels)

Every single level in LumaLogic has a distinct handcrafted configuration, matrix dimension, obstacle arrangement, and optical puzzle mechanic:

| Chapter | Title (EN) | عنوان فصل (FA) | Levels | Core Mechanics & Archetypes |
|:---:|:---|:---|:---:|:---|
| **01** | **Light Basics** | پایه‌های نور | 1–16 | Single/Double mirror reflections, basic ray deflection, 5x5 grids |
| **02** | **Reflection & Angles** | بازتاب و زاویه‌ها | 17–32 | Multi-bounce angles, perimeter reflections, obstacle corridors |
| **03** | **Precision & Constraints** | دقت و محدودیت | 33–48 | Minimal move budgets, narrow corridors, precision alignment |
| **04** | **Prism Splitters** | منشورهای شکافنده | 49–64 | 50/50 dual beam division, multi-target simultaneous activation |
| **05** | **Spectrum Colors** | طیف رنگ‌ها | 65–80 | Red, Blue, Green, Yellow wavelength routing & multi-sources |
| **06** | **Chromatic Filters** | فیلترهای کروماتیک | 81–96 | Selective wavelength barriers, color isolation sieves |
| **07** | **Energy Thrift** | صرفه‌جویی انرژی | 97–112 | Strict photon energy limits, shortest-path optimization |
| **08** | **Multi-Beam Arrays** | آرایه چند پرتو | 113–128 | Dual orthogonal laser emitters, intersecting beam paths |
| **09** | **AND Conjunction** | دروازه منطقی AND | 129–144 | Dual concurrent beam input requirements to open gates |
| **10** | **OR Redundancy** | دروازه منطقی OR | 145–160 | Alternate routing pathways and redundant beam triggers |
| **11** | **NOT Inversion** | دروازه وارونه‌ساز NOT | 161–176 | Active dark inverters, light diversion mechanics |
| **12** | **Logic Networks** | شبکه‌های منطقی | 177–192 | Cascading AND/OR combinations, hybrid logic matrices |
| **13** | **Expert Routing** | مسیریابی پیشرفته | 193–208 | 7x7 and 8x8 dense labyrinths with heavy barrier layouts |
| **14** | **Master Energy** | استاد انرژی | 209–224 | Micro energy budgets, high-efficiency photon circuits |
| **15** | **Expert Conundrum** | معمای پیشرفته | 225–240 | Integrated splitters, color filters, and dual logic gates |
| **16** | **LumaLogic Master** | استاد LumaLogic | 241–256 | Grand Finale master puzzles culminating at Level 256 |

---

## 🎮 Game Components & Mechanics

- 🔦 **Light Source (منبع نور)**: Emits high-energy laser beams in Cardinal directions (`0°`, `90°`, `180°`, `270°`) with distinct wavelengths (White, Red, Blue, Green, Yellow).
- 🪞 **Reflection Mirror (آینه بازتابی)**: Reflects incoming light beams by 90 degrees based on its angular orientation.
- 💎 **Prism Splitter (منشور شکافنده)**: Splits an incoming ray into two orthogonal beams.
- 🌈 **Color Filter (فیلتر رنگی)**: Allows only matching light wavelengths to pass through while absorbing mismatched colors.
- 🧱 **Obstacle Block (مانع نوری)**: Solid impassable blockades requiring clever routing around them.
- ⚡ **Logic Gates (دروازه‌های منطقی)**:
  - **AND Gate**: Emits output only when both input channels receive illumination.
  - **OR Gate**: Emits output when either (or both) input channels receive illumination.
  - **NOT Gate**: Emits light when unilluminated; turns off when an input beam enters.
  - **XOR Gate**: Emits light when strictly one input is active.
- 🎯 **Optical Target (هدف دریافت نور)**: Receptive light sensor that triggers circuit completion when illuminated with the correct wavelength.

---

## 🛠️ Technology Stack & Architecture

- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM + Clean Architecture + Dependency Injection Container
- **Raytracer Engine**: 2D custom vector-based iterative raytracer with color mixing, reflection matrices, loop cycle detection, and step budgeting
- **Audio Engine**: Android `SoundPool` + `Vibrator` with haptic feedback
- **Persistence**: High-performance local JSON serialization (Moshi)
- **Internationalization**: Runtime dynamic Locale Switching (Persian RTL / English LTR) with automatic Persian numeral formatting (`toPersianDigits`)

```
ir.danialchoopan.lumalogic
├── data
│   ├── level           # LevelRegistry (256 crafted levels), Chapter definitions, Daily Puzzles
│   ├── model           # Cell, GridModel, Level, Chapter, Achievement, PlayerStats
│   └── repository      # LevelRepository, ProgressRepository, AchievementsRepository
├── di                  # Central AppContainer dependency injection
├── domain
│   ├── engine          # GameEngine raytracing, beam propagation & collision detection
│   ├── hint            # Intelligent BFS Hint Engine
│   └── level           # LevelValidator, LevelProgressManager, DailyPuzzleGenerator
├── ui
│   ├── components      # GameCanvas, LumaHeader, GlowingCard, StarDisplay, LumaButton
│   ├── localization    # LocalizationManager, Persian digits converter, LocalLocalization
│   ├── navigation      # NavGraph and Type-safe route destinations
│   ├── screens         # Home, ChapterSelect, LevelSelect, Game, Editor, Daily, Profile, Achievements
│   └── theme           # Material3 ColorScheme, Neon Glows, Dark Theme, Typography
```

---

## 👨‍💻 Developer & Credits

- **Creator & Lead Developer**: Danial Choopan (دانیال چوپان)
- **Framework**: Jetpack Compose & Android SDK
- **License**: [MIT License](LICENSE)
