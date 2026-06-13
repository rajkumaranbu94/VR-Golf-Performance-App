GolfPerformanceApp
===================

A sample Android app (Jetpack Compose) that demonstrates an offline-first golf performance viewer: players, shots, search/filter, local caching, and a detail screen with flip-card visuals and simple charts.

This repository is intended as a working sample for prototyping UI/UX and data flow using modern Android libraries and patterns (Room, Retrofit/Moshi, Kotlin Coroutines/Flow, Jetpack Compose).

Quick summary
-------------
- Language: Kotlin
- UI: Jetpack Compose
- Persistence: Room (local DB)
- Networking: Retrofit + Moshi + OkHttp (MockInterceptor serving `assets/mock_players.json`)
- DI: Koin
- Concurrency: Kotlin Coroutines + Flow
- Charts: simple Compose Canvas visuals (placeholder for richer chart libraries)

Requirements
------------
- JDK 11+ (match project's Gradle/JDK settings)
- Android SDK (compileSdk and buildTools specified in module Gradle files)
- Android Studio (Arctic Fox / Bumblebee / Chipmunk or newer) or command-line Gradle

If you are on macOS and you run into Xcode license prompts (rare for Android), accept the license:

```bash
sudo xcodebuild -license
# or non-interactive
sudo xcodebuild -license accept
```

Setup (developer machine)
-------------------------
1. Clone the repo (after pushing to GitHub):

```bash
git clone git@github.com:<your-username>/GolfPerformanceApp.git
cd GolfPerformanceApp
```

2. Open in Android Studio
- File → Open → select the project root. Allow Gradle sync to download required SDKs and Gradle.

3. Command-line build (optional):

```bash
# from project root
./gradlew clean assembleDebug
# install to a connected device or emulator
./gradlew installDebug
```

How to run
----------
- Open the app on an Android emulator or device from Android Studio (Run ▶) or via `./gradlew installDebug`.
- The app uses a mock API served from `app/src/main/assets/mock_players.json` through an OkHttp `MockInterceptor`. No external network is required.

Project structure
-----------------
- `app/` - Android application module
  - `src/main/java/com/golfperformance/app/` - application source
	- `data/remote/` - Retrofit service, DTOs
	- `data/local/` - Room entities, DAOs, database (migration included)
	- `repository/` - repository exposing Flows and refresh logic
	- `viewmodel/` - ViewModels and factories
	- `ui/` - Compose screens (player list, player detail)
  - `src/main/assets/mock_players.json` - mock API response used by `MockInterceptor`

Architecture and design decisions
---------------------------------
This app follows an offline-first single source-of-truth architecture:

1. Room as the source of truth
   - UI data comes from Room via Flows. Repository writes remote results into Room so UI is always reactive and offline-capable.

2. Networking and mock API
   - `Retrofit` + `Moshi` are used. A local `MockInterceptor` reads `assets/mock_players.json` so the app runs without a backend.

3. Coroutines + Flow
   - ViewModels expose `StateFlow` for Compose screens. Repository methods use `suspend` + `Dispatchers.IO`.

4. DI
   - Koin provides singletons for Retrofit, OkHttp, AppDatabase, DAOs, and repository.

5. UI and UX
   - Jetpack Compose with a List screen (search + horizontal club filter) and a Detail screen with a flippable header card showing simple visualizations.

6. Room migrations
   - Database versioning and a migration (1→2) are included to add the `spinRate` column when schema changes occur.

Data flow (summary)
-------------------
1. On startup the list screen triggers `refreshFromRemote()` (once) which fetches DTOs and writes them to Room.
2. DAOs expose Flows that the ViewModel collects and exposes to the UI as StateFlow.
3. Selecting a player opens the detail screen which observes `playerById` and shots Flows to show data and charts.

Known issues and TODOs
----------------------
- Material icons: some Compose icon usage may require adding `material-icons-extended` to Gradle. Replace text chevrons with vector icons for nicer visuals.
- Fonts: no custom font currently bundled — add TTFs or choose a Google Font and update `Theme.kt` to match the visual identity.
- Charts: current visuals are simple Canvas bars. Consider integrating a charting library for axes, labels and better legibility.
- Tests: add unit tests for repository and ViewModel flows (Turbine + JUnit) and UI tests for Compose screens.

Contributing
------------
1. Fork and branch.
2. Make changes and run locally.
3. Open a PR describing the change.

License
-------
No license needed

Generated on 2026-06-13.
