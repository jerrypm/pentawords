# Pentaword - Development Progress

A NYT Spelling Bee clone game for Android.

## Project Info

| Field | Value |
|-------|-------|
| **Package Name** | `com.jeripurnama.pentaword` |
| **App Name** | Pentaword |
| **Min SDK** | 26 (Android 8.0 Oreo) |
| **Target SDK** | 35 (Play Console requirement) |
| **Compile SDK** | 35 |
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material3 |
| **Architecture** | Clean Architecture (Domain, Data, Presentation) |

## Play Console Requirements

### API Level 35 Requirement

Starting August 2024, Google Play requires:
- **New apps**: Must **target** API level 35 or higher
- **App updates**: Must **target** API level 35 or higher

**Important distinction:**
- `targetSdk = 35` → Required for Play Console (what Android version you test against)
- `minSdk = 26` → Device compatibility (supports ~90% of Android devices, required for adaptive icons)
- `compileSdk = 35` → Latest APIs available for development

This project uses `targetSdk = 35` for Play Console compliance while `minSdk = 24` ensures broad device compatibility.

### Pre-Launch Checklist

- [ ] App icon (adaptive icon configured)
- [ ] Privacy policy URL
- [ ] Store listing (title, description, screenshots)
- [ ] Content rating questionnaire
- [ ] Target audience declaration
- [ ] App signing by Google Play

## Game Features

### Core Mechanics (Based on NYT Spelling Bee)

| Feature | Status |
|---------|--------|
| 7-letter honeycomb UI | Done |
| Center letter must be in every word | Done |
| Minimum 4-letter words | Done |
| Letters can be reused | Done |
| Pangram detection (all 7 letters) | Done |
| Scoring system | Done |
| Rank progression | Done |

### Scoring System

| Word Length | Points |
|-------------|--------|
| 4 letters | 1 point |
| 5+ letters | 1 point per letter |
| Pangram bonus | +7 points |

### Rank Progression

| Rank | Threshold |
|------|-----------|
| Beginner | 0% |
| Good Start | 2% |
| Moving Up | 5% |
| Good | 8% |
| Solid | 15% |
| Nice | 25% |
| Great | 40% |
| Amazing | 50% |
| Genius | 70% |
| Queen Bee | 100% |

### Android-Specific Features

| Feature | Status |
|---------|--------|
| Edge-to-edge display | Done |
| Dark mode support | Done |
| New Puzzle button | Done |
| Shuffle button | Done |
| Delete button | Done |
| Found words list (expandable) | Done |

## Project Structure

```
app/src/main/java/com/jeripurnama/pentaword/
├── data/
│   └── DictionaryRepository.kt       # Word dictionary management
├── domain/
│   ├── GameState.kt                  # Game state & rank definitions
│   ├── PuzzleGenerator.kt            # Puzzle generation logic
│   └── WordValidator.kt              # Word validation rules
├── presentation/
│   ├── components/
│   │   ├── ActionButtons.kt          # Delete, Shuffle, Enter buttons
│   │   ├── FoundWordsList.kt         # Expandable found words
│   │   ├── HexagonButton.kt          # Individual hex button
│   │   ├── Honeycomb.kt              # 7-letter honeycomb layout
│   │   ├── ScoreDisplay.kt           # Score & rank progress
│   │   └── WordDisplay.kt            # Current word input display
│   ├── screens/
│   │   └── GameScreen.kt             # Main game screen
│   └── viewmodel/
│       └── GameViewModel.kt          # Game logic & state management
├── ui/theme/
│   ├── Color.kt                      # Color definitions
│   ├── Theme.kt                      # Material3 theme
│   └── Type.kt                       # Typography
└── MainActivity.kt                   # Entry point
```

## Development Progress

### Version 1.0.0 (Current)

#### Completed Tasks

- [x] Initial project setup with Kotlin + Jetpack Compose
- [x] Configure minSdk 35, targetSdk 35
- [x] Implement honeycomb UI with hexagonal buttons
- [x] Implement word validation logic
- [x] Implement scoring system
- [x] Implement rank progression
- [x] Implement pangram detection
- [x] Create found words list (expandable)
- [x] Add action buttons (Delete, Shuffle, Enter)
- [x] Add New Puzzle button
- [x] Setup Clean Architecture structure
- [x] Create unit tests for game logic
- [x] Fix all package names to `com.jeripurnama.pentaword`
- [x] Fix all compilation errors
- [x] Push to GitHub repository

#### Pending Tasks

- [ ] Add haptic feedback
- [ ] Add sound effects
- [ ] Add daily puzzle feature (save/restore state)
- [ ] Add share score feature
- [ ] Add settings screen
- [ ] Add tutorial/how-to-play screen
- [ ] Expand word dictionary
- [ ] Add analytics
- [ ] Add AdMob integration
- [ ] Prepare for Play Store submission

## Testing

### Unit Tests

| Test Class | Tests | Status |
|------------|-------|--------|
| WordValidatorTest | 9 tests | All passing |
| GameStateTest | 8 tests | All passing |
| PuzzleGeneratorTest | 6 tests | All passing |

### Running Tests

```bash
./gradlew testDebugUnitTest
```

## Build Instructions

### Debug Build

```bash
./gradlew assembleDebug
```

### Release Build

```bash
./gradlew assembleRelease
```

## Git Repository

- **Remote**: https://github.com/jerrypm/pentawords
- **Branch**: main

## Dependencies

| Library | Version |
|---------|---------|
| Kotlin | 2.0.21 |
| AGP | 8.7.2 |
| Compose BOM | 2024.11.00 |
| Material3 | (via BOM) |
| Navigation Compose | 2.8.4 |
| Lifecycle | 2.8.7 |
| Core KTX | 1.15.0 |
| Coroutines Android | 1.9.0 |

## Project Rules

See `CLAUDE.md` for AI assistant rules including:
- Git commit/push policy (user handles)
- Mandatory error checking after code updates
- Code quality standards

## Changelog

### 2026-02-08 (Update 4)

- Fixed minSdk: changed from 35 to 26 for device compatibility
- Clarified Play Console requirement (targetSdk vs minSdk)
- App now supports Android 8.0+ (~90% of devices)

### 2026-02-08 (Update 3)

- Extracted all hardcoded strings to `strings.xml`
- Updated FoundWordsList.kt to use stringResource
- Updated ScoreDisplay.kt to use stringResource
- Updated GameScreen.kt to use stringResource
- Updated ActionButtons.kt to use stringResource
- Updated WordDisplay.kt to use stringResource
- All files now comply with no-hardcoded-strings rule

### 2026-02-08 (Update 2)

- Added `kotlinx-coroutines-android` dependency
- Created `CLAUDE.md` with project rules
- Added post-update error check requirements

### 2026-02-08

- Initial project setup
- Implemented full game mechanics
- Created comprehensive unit tests
- Fixed all package name issues
- Fixed compilation errors
- Created PROGRESS.md documentation

---

*Last updated: 2026-02-08*
