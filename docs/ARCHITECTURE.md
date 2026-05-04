# GitHub Search Architecture & Tech Stack

## 1. Project Structure (Multi-Module & Clean Arch)
- `:app`: Entry point, MainActivity, Global Theme.
- `:domain`: Pure Kotlin module. UseCases, Entities, Repository Interfaces. (No framework dependencies)
- `:data`: Implementation of Data Sources (Room, Retrofit) and Repositories.
- `:feature:*`: Independent UI modules (Compose, ViewModels).
    - `:feature:home`, `:feature:detail`, `:feature:favorite`, `:feature:setting`
- `:build-logic`: Convention Plugins for shared build logic.
- `:core`: Common utilities and shared code.

## 2. Core Tech Stack
- **UI:** Jetpack Compose (Material 3), Landscapist (Glide).
- **DI:** Dagger Hilt.
- **Async:** Coroutines & Flow.
- **Navigation:** Compose Navigation (Standard).
- **Database:** Room.
- **Network:** Retrofit2, OkHttp3, GSON.

## 3. Key Commands
- Build: `./gradlew assembleDebug`
- Format: `./gradlew spotlessApply`
