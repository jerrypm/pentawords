# CLAUDE.md - Project Rules for Pentaword

This file contains rules and guidelines for Claude AI when working on this project.

## Git Rules

### Commit & Push Policy

**IMPORTANT: User handles all git commit and push operations.**

- Claude should NOT run `git commit` or `git push` commands
- Claude should only prepare files and inform user what changes were made
- User will review and commit/push changes themselves
- Claude can run `git status`, `git diff`, `git log` for information purposes

### Commit Message Format

When user asks for commit message suggestion:
```
<type>: <short description>

<detailed description if needed>

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>
```

Types: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`

## Code Quality Rules

### Post-Update Error Check (MANDATORY)

**After ANY code update, Claude MUST:**

1. Run `./gradlew compileDebugKotlin` to check compilation
2. If errors found → Fix immediately before proceeding
3. Run `./gradlew testDebugUnitTest` to verify tests pass
4. If tests fail → Fix immediately before proceeding
5. Only report success to user after all checks pass

```bash
# Required checks after code changes
./gradlew compileDebugKotlin
./gradlew testDebugUnitTest
```

### Error Handling Flow

```
Code Change Made
       ↓
Run Compile Check
       ↓
   Errors? ──Yes──→ Fix Errors ──→ Repeat Check
       ↓ No
Run Unit Tests
       ↓
   Failures? ──Yes──→ Fix Tests ──→ Repeat Check
       ↓ No
Update PROGRESS.md
       ↓
Report Success to User
```

## Clean Code Rules

### File Size Limit (MANDATORY)

**Maximum 200 lines per file.**

If a file exceeds 200 lines:
1. Split into multiple smaller files
2. Use proper separation of concerns
3. Create helper/utility files as needed

Example splits:
- `GameViewModel.kt` → `GameViewModel.kt` + `GameActions.kt`
- `GameScreen.kt` → `GameScreen.kt` + `GameContent.kt`

### No Hardcoded Strings (MANDATORY)

**All user-facing strings MUST be in `strings.xml`.**

❌ Bad:
```kotlin
Text(text = "Type or click")
```

✅ Good:
```kotlin
Text(text = stringResource(R.string.hint_type_or_click))
```

```xml
<!-- strings.xml -->
<string name="hint_type_or_click">Type or click</string>
```

### String Naming Convention

| Type | Pattern | Example |
|------|---------|---------|
| Labels | `label_*` | `label_score` |
| Buttons | `btn_*` | `btn_enter` |
| Messages | `msg_*` | `msg_too_short` |
| Hints | `hint_*` | `hint_type_or_click` |
| Titles | `title_*` | `title_found_words` |
| Errors | `error_*` | `error_not_in_list` |

### Code Organization

```kotlin
// File structure order:
// 1. Package declaration
// 2. Imports (sorted)
// 3. Class/Object declaration
// 4. Companion object (if any)
// 5. Properties (val before var)
// 6. Init block
// 7. Public functions
// 8. Private functions
```

### Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| Classes | PascalCase | `GameViewModel` |
| Functions | camelCase | `onLetterClick` |
| Constants | SCREAMING_SNAKE | `MAX_WORD_LENGTH` |
| Properties | camelCase | `currentWord` |
| Composables | PascalCase | `HexagonButton` |

### Single Responsibility

Each class/file should have ONE responsibility:
- ViewModel → State management only
- Repository → Data access only
- Composable → UI rendering only
- Use case → Single business logic operation

## Progress Tracking (MANDATORY)

### Update PROGRESS.md After Changes

**After ANY feature update or addition, Claude MUST:**

1. Update `PROGRESS.md` changelog section
2. Mark completed tasks in checklist
3. Add new pending tasks if discovered
4. Update version info if significant change

Format:
```markdown
### YYYY-MM-DD (Update N)

- Added: <new feature>
- Fixed: <bug fix>
- Changed: <modification>
- Removed: <deprecated item>
```

## Project Standards

### Package Name

```
com.jeripurnama.pentaword
```

**Never use `com.example` in any file.**

### SDK Requirements

| Setting | Value | Reason |
|---------|-------|--------|
| minSdk | 35 | Play Console requirement (Aug 2024+) |
| targetSdk | 35 | Latest stable API level |
| compileSdk | 35 | Match target SDK |

### Architecture

- **Clean Architecture**: Domain, Data, Presentation layers
- **UI Framework**: Jetpack Compose + Material3
- **State Management**: ViewModel + StateFlow
- **Async**: Kotlin Coroutines

### Mobile Game UX Best Practices

Based on research, implement:

1. **Haptic Feedback**: Tactile response on button presses
2. **Large Touch Targets**: Minimum 48dp, ideally 60-80dp
3. **Thumb-Friendly Layout**: Primary controls at bottom
4. **Instant Feedback**: Visual + audio + haptic on actions
5. **Progressive Difficulty**: Easy to start, hard to master
6. **Short Sessions**: Perfect for mobile play patterns
7. **Daily Ritual**: Encourage daily engagement

## File Locations

| Type | Location |
|------|----------|
| Source | `app/src/main/java/com/jeripurnama/pentaword/` |
| Tests | `app/src/test/java/com/jeripurnama/pentaword/` |
| Android Tests | `app/src/androidTest/java/com/jeripurnama/pentaword/` |
| Assets | `app/src/main/assets/` |
| Resources | `app/src/main/res/` |
| Strings | `app/src/main/res/values/strings.xml` |

## Documentation

- Update `PROGRESS.md` when completing features (MANDATORY)
- Keep this `CLAUDE.md` updated with new rules
- Document any workarounds or special considerations

## Common Issues & Solutions

### IDE shows "Unresolved reference" but build succeeds

This is an IDE cache issue. Solutions:
1. File → Invalidate Caches → Invalidate and Restart
2. Or run: `./gradlew clean` then sync project

### Dependency version conflicts

Always check compatibility:
- `core-ktx` version must be compatible with compileSdk
- Check release notes for minimum AGP version requirements

---

*Last updated: 2026-02-08*
